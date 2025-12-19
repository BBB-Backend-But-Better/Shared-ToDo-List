package com.todoapp.shared_todo.repository.users;

import com.todoapp.shared_todo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    //로그인 시 아이디로 회원찾기
    Optional<Users> findByLoginId(String loginId);

    //회원가입시 아이디 중복 체크
    Boolean existsByLoginId(String loginId);


}
