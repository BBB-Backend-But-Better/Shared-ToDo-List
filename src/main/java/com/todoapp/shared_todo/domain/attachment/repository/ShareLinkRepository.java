package com.todoapp.shared_todo.domain.attachment.repository;

import com.todoapp.shared_todo.domain.attachment.entity.Attachment;
import com.todoapp.shared_todo.domain.attachment.entity.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("unused") // Spring Data JPA가 런타임에 자동으로 구현하여 사용
public interface ShareLinkRepository extends JpaRepository<ShareLink, Long> {

    /**
     * 공유 링크 UUID로 조회 (유효한 링크만)
     */
    @Query("SELECT sl FROM ShareLink sl WHERE sl.shareUuid = :shareUuid AND sl.isActive = true AND sl.expiresAt > :now")
    Optional<ShareLink> findByShareUuidAndValid(@Param("shareUuid") String shareUuid, @Param("now") LocalDateTime now);

    /**
     * 첨부파일별 활성 공유 링크 조회
     */
    List<ShareLink> findByAttachmentAndIsActiveTrue(Attachment attachment);

    /**
     * 만료된 공유 링크 조회 (Batch 작업용)
     */
    @Query("SELECT sl FROM ShareLink sl WHERE sl.expiresAt <= :now AND sl.isActive = true")
    List<ShareLink> findExpiredShareLinks(@Param("now") LocalDateTime now);

    /**
     * 특정 시각 이전에 만료된 공유 링크 조회 (Batch 작업용)
     */
    @Query("SELECT sl FROM ShareLink sl WHERE sl.expiresAt <= :before AND sl.isActive = true")
    List<ShareLink> findExpiredShareLinksBefore(@Param("before") LocalDateTime before);
}

