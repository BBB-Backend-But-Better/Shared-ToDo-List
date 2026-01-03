package com.todoapp.shared_todo.domain.attachment.port;

import java.io.InputStream;

/**
 * S3 서비스 Port 인터페이스
 * 도메인 계층과 인프라 계층 간의 의존성 역전을 위한 인터페이스
 */
public interface S3ServicePort {

    /**
     * 파일 업로드
     * @param inputStream 파일 입력 스트림
     * @param s3Key S3 객체 키
     * @param contentType 파일 MIME 타입
     * @param contentLength 파일 크기
     * @return S3 객체 키
     */
    String uploadFile(InputStream inputStream, String s3Key, String contentType, long contentLength);

    /**
     * Presigned URL 생성 (다운로드용)
     * @param s3Key S3 객체 키
     * @return Presigned URL
     */
    String generatePresignedDownloadUrl(String s3Key);

    /**
     * Presigned URL 생성 (업로드용)
     * @param s3Key S3 객체 키
     * @param contentType 파일 MIME 타입
     * @return Presigned URL
     */
    String generatePresignedUploadUrl(String s3Key, String contentType);

    /**
     * 파일 삭제
     * @param s3Key S3 객체 키
     */
    void deleteFile(String s3Key);

    /**
     * S3 Key 생성 (UUID 기반)
     * @param originalFilename 원본 파일명
     * @return S3 Key (경로 포함)
     */
    String generateS3Key(String originalFilename);

    /**
     * 저장 파일명 생성 (UUID 기반)
     * @param originalFilename 원본 파일명
     * @return 저장 파일명
     */
    String generateStoreFilename(String originalFilename);
}

