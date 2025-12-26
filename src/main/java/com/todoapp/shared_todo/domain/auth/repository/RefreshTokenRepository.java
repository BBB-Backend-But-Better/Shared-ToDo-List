package com.todoapp.shared_todo.domain.auth.repository;

import com.todoapp.shared_todo.domain.auth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//    검증용: 토큰 값으로 엔티티 조회
    Optional<RefreshToken> findByToken(String token);

// 로그아웃용: 특정 유저의 토큰 전체 삭제
    @Modifying
    @Transactional
    @Query("delete from RefreshToken r where r.userLoginId = :userLoginId")
    void deleteByUserLoginId(@Param("userLoginId") String userLoginId);
}
