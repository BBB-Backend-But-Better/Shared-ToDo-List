package com.todoapp.shared_todo.infrastructure.s3;

import com.todoapp.shared_todo.domain.attachment.port.S3ServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

/**
 * AWS S3 서비스
 * 파일 업로드/다운로드 및 Presigned URL 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service implements S3ServicePort {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.presigned-url-expiration:3600}")
    private Integer presignedUrlExpiration;

    /**
     * AWS 자격 증명 검증
     */
    private void validateAwsCredentials() {
        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new IllegalStateException("AWS Access Key가 설정되지 않았습니다.");
        }
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("AWS Secret Key가 설정되지 않았습니다.");
        }
    }

    /**
     * S3Client 생성
     */
    private S3Client createS3Client() {
        validateAwsCredentials();
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    /**
     * S3Presigner 생성
     */
    private S3Presigner createS3Presigner() {
        validateAwsCredentials();
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    /**
     * 파일 업로드
     * @param inputStream 파일 입력 스트림
     * @param s3Key S3 객체 키
     * @param contentType 파일 MIME 타입
     * @param contentLength 파일 크기
     * @return S3 객체 키
     */
    @Override
    public String uploadFile(InputStream inputStream, String s3Key, String contentType, long contentLength) {
        try (S3Client s3Client = createS3Client()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, contentLength));
            log.info("파일 업로드 성공: s3Key={}", s3Key);
            return s3Key;
        } catch (S3Exception e) {
            log.error("S3 파일 업로드 실패: s3Key={}, error={}", s3Key, e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    /**
     * Presigned URL 생성 (다운로드용)
     * @param s3Key S3 객체 키
     * @return Presigned URL
     */
    @Override
    public String generatePresignedDownloadUrl(String s3Key) {
        try (S3Presigner presigner = createS3Presigner()) {
            software.amazon.awssdk.services.s3.model.GetObjectRequest getObjectRequest =
                    software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(builder -> builder
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .getObjectRequest(getObjectRequest)
                    .build());

            String url = presignedRequest.url().toString();
            log.info("Presigned 다운로드 URL 생성: s3Key={}", s3Key);
            return url;
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: s3Key={}, error={}", s3Key, e.getMessage());
            throw new RuntimeException("Presigned URL 생성에 실패했습니다.", e);
        }
    }

    /**
     * Presigned URL 생성 (업로드용)
     * 클라이언트가 직접 S3에 업로드할 수 있는 URL 생성
     * @param s3Key S3 객체 키
     * @param contentType 파일 MIME 타입
     * @return Presigned URL
     */
    @Override
    public String generatePresignedUploadUrl(String s3Key, String contentType) {
        try (S3Presigner presigner = createS3Presigner()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String url = presignedRequest.url().toString();
            log.info("Presigned 업로드 URL 생성: s3Key={}", s3Key);
            return url;
        } catch (Exception e) {
            log.error("Presigned 업로드 URL 생성 실패: s3Key={}, error={}", s3Key, e.getMessage());
            throw new RuntimeException("Presigned 업로드 URL 생성에 실패했습니다.", e);
        }
    }

    /**
     * 파일 삭제
     * @param s3Key S3 객체 키
     */
    @Override
    public void deleteFile(String s3Key) {
        try (S3Client s3Client = createS3Client()) {
            s3Client.deleteObject(builder -> builder
                    .bucket(bucketName)
                    .key(s3Key)
                    .build());
            log.info("파일 삭제 성공: s3Key={}", s3Key);
        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패: s3Key={}, error={}", s3Key, e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    /**
     * 파일 확장자 추출
     * @param filename 파일명
     * @return 확장자 (점 포함, 없으면 빈 문자열)
     */
    private String extractExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * S3 Key 생성 (UUID 기반)
     * @param originalFilename 원본 파일명
     * @return S3 Key (경로 포함)
     */
    @Override
    public String generateS3Key(String originalFilename) {
        String extension = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return "attachments/" + uuid + extension;
    }

    /**
     * 저장 파일명 생성 (UUID 기반)
     * @param originalFilename 원본 파일명
     * @return 저장 파일명
     */
    @Override
    public String generateStoreFilename(String originalFilename) {
        String extension = extractExtension(originalFilename);
        return UUID.randomUUID().toString() + extension;
    }
}

