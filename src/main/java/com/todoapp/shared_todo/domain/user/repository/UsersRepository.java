package com.todoapp.shared_todo.domain.user.repository;


import com.todoapp.shared_todo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {
    //  로그인 아이디로 사용자 조최
    Optional<User> findByLoginId(String loginId);
    //  회원가입 용: 중복 ID 검사 
    Boolean existsByLoginId(String loginId);
    // 유저코드(초대코드) 중복 검사
    Boolean existsByUserCode(String userCode);
    //초대 기능 용: 코드로 유저를 찾아야 초대를 보냄
    Optional<User> findByUserCode(String userCode);

}
