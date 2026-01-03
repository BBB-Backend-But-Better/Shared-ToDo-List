package com.todoapp.shared_todo.domain.attachment.service;

import com.todoapp.shared_todo.domain.attachment.entity.Attachment;
import com.todoapp.shared_todo.domain.attachment.entity.ShareLink;
import com.todoapp.shared_todo.domain.attachment.repository.ShareLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 공유 링크 관리 서비스 (DB 기반)
 * UUID 기반 공유 링크 생성 및 만료 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareLinkService {

    private final ShareLinkRepository shareLinkRepository;

    @Value("${share.link.base-url}")
    private String baseUrl;

    @Value("${share.link.max-ttl-minutes:10080}")
    private Integer maxTtlMinutes;

    /**
     * 공유 링크 생성
     * @param attachment 첨부파일
     * @param ttlMinutes TTL (분 단위)
     * @return 공유 링크 UUID
     */
    @Transactional
    public String createShareLink(Attachment attachment, Integer ttlMinutes) {
        // TTL 검증 (최대값 제한)
        int validTtl = Math.min(ttlMinutes, maxTtlMinutes);
        if (validTtl < 1) {
            throw new IllegalArgumentException("TTL은 최소 1분 이상이어야 합니다.");
        }

        // 기존 활성 링크가 있으면 비활성화
        List<ShareLink> existingLinks = shareLinkRepository.findByAttachmentAndIsActiveTrue(attachment);
        existingLinks.forEach(ShareLink::deactivate);

        // 새로운 공유 링크 생성
        ShareLink shareLink = ShareLink.create(attachment, validTtl);
        ShareLink savedShareLink = shareLinkRepository.save(shareLink);

        log.info("공유 링크 생성: shareUuid={}, attachmentId={}, ttl={}분", savedShareLink.getShareUuid(), attachment.getId(), validTtl);
        return savedShareLink.getShareUuid();
    }

    /**
     * 공유 링크로 첨부파일 조회
     * @param shareUuid 공유 링크 UUID
     * @return 첨부파일 (없으면 null)
     */
    public Attachment getAttachmentByShareUuid(String shareUuid) {
        ShareLink shareLink = shareLinkRepository.findByShareUuidAndValid(shareUuid, LocalDateTime.now())
                .orElse(null);

        if (shareLink == null) {
            log.warn("공유 링크가 만료되었거나 존재하지 않음: shareUuid={}", shareUuid);
            return null;
        }

        log.info("공유 링크로 첨부파일 조회: shareUuid={}, attachmentId={}", shareUuid, shareLink.getAttachment().getId());
        return shareLink.getAttachment();
    }

    /**
     * 공유 링크 삭제 (만료 전 수동 삭제)
     * @param shareUuid 공유 링크 UUID
     */
    @Transactional
    public void deleteShareLink(String shareUuid) {
        shareLinkRepository.findByShareUuidAndValid(shareUuid, LocalDateTime.now())
                .ifPresent(shareLink -> {
                    shareLink.deactivate();
                    shareLinkRepository.save(shareLink);
                    log.info("공유 링크 삭제: shareUuid={}", shareUuid);
                });
    }

    /**
     * 공유 링크 유효성 검증
     * @param shareUuid 공유 링크 UUID
     * @return 유효 여부
     */
    public boolean isValidShareLink(String shareUuid) {
        return shareLinkRepository.findByShareUuidAndValid(shareUuid, LocalDateTime.now())
                .isPresent();
    }

    /**
     * 공유 링크 전체 URL 생성
     * @param shareUuid 공유 링크 UUID
     * @return 전체 공유 URL
     */
    public String generateShareUrl(String shareUuid) {
        return baseUrl + "/" + shareUuid;
    }

    /**
     * 공유 링크 만료 시각 조회
     * @param shareUuid 공유 링크 UUID
     * @return 만료 시각 (없으면 null)
     */
    public LocalDateTime getExpiresAt(String shareUuid) {
        return shareLinkRepository.findByShareUuidAndValid(shareUuid, LocalDateTime.now())
                .map(ShareLink::getExpiresAt)
                .orElse(null);
    }
}

