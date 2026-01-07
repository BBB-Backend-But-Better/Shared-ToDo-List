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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
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
 * AWS S3 서비스 구현체
 * S3ServicePort 인터페이스의 구현으로, AWS S3 SDK를 사용하여 파일 업로드/다운로드 및 Presigned URL을 생성합니다.
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
     * Access Key와 Secret Key가 설정되어 있는지 확인합니다.
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
     * AWS 자격 증명을 사용하여 S3 클라이언트를 생성합니다.
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
     * Presigned URL 생성을 위한 Presigner를 생성합니다.
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
     * S3 버킷에 파일을 업로드합니다.
     * 
     * @param inputStream 파일 입력 스트림
     * @param s3Key S3 객체 키 (파일 경로)
     * @param contentType 파일 MIME 타입
     * @param contentLength 파일 크기
     * @return 업로드된 S3 객체 키
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
     * S3 파일을 다운로드하기 위한 임시 URL을 생성합니다.
     * 이 URL은 제한된 시간 동안만 유효하며, 만료 후에는 사용할 수 없습니다.
     * 
     * @param s3Key S3 객체 키
     * @return Presigned URL (만료 시간 포함)
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
     * 클라이언트가 직접 S3에 파일을 업로드할 수 있는 임시 URL을 생성합니다.
     * 
     * @param s3Key S3 객체 키
     * @param contentType 파일 MIME 타입
     * @return Presigned URL (업로드용)
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
     * S3 버킷에서 파일을 삭제합니다.
     * 
     * @param s3Key 삭제할 S3 객체 키
     */
    @Override
    public void deleteFile(String s3Key) {
        try (S3Client s3Client = createS3Client()) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("파일 삭제 성공: s3Key={}", s3Key);
        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패: s3Key={}, error={}", s3Key, e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    /**
     * S3 Key 생성 (UUID 기반)
     * 원본 파일명을 기반으로 S3에 저장할 고유한 키를 생성합니다.
     * 경로 구조: "attachments/{년도}/{월}/uuid-원본파일명"
     * 
     * @param originalFilename 원본 파일명
     * @return S3 Key (경로 포함)
     */
    @Override
    public String generateS3Key(String originalFilename) {
        String extension = extractExtension(originalFilename);
        String uuidFilename = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
        
        // 경로 구조: attachments/2024/01/uuid-filename.pdf
        java.time.LocalDate now = java.time.LocalDate.now();
        return String.format("attachments/%d/%02d/%s", now.getYear(), now.getMonthValue(), uuidFilename);
    }

    /**
     * 저장 파일명 생성 (UUID 기반)
     * 원본 파일명을 기반으로 UUID가 포함된 저장 파일명을 생성합니다.
     * 
     * @param originalFilename 원본 파일명
     * @return 저장 파일명 (UUID 포함)
     */
    @Override
    public String generateStoreFilename(String originalFilename) {
        String extension = extractExtension(originalFilename);
        return UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
    }

    /**
     * 파일 확장자 추출
     * 파일명에서 확장자를 추출합니다.
     * 
     * @param filename 파일명
     * @return 확장자 (점 제외)
     */
    private String extractExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}

