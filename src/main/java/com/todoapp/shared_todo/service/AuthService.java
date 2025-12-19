package com.todoapp.shared_todo.service;

import com.todoapp.shared_todo.dto.users.LoginRequestDto;
import com.todoapp.shared_todo.dto.users.SignupRequestDto;
import com.todoapp.shared_todo.entity.Users;
import com.todoapp.shared_todo.repository.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRe;
    //스프링 시큐리티 암호화

    //회원 생성
    @Transactional
    public Users signUp(SignupRequestDto dto) {
        if (usersRe.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디 입니다.");
        }

        //비밀 번호 암호화 메서드 있어야됨

        Users users = Users.builder()
                .loginId(dto.getLoginId())
                .password(dto.getPassword())//비밀번호 암호화 시켜주기
                .build();

        return usersRe.save(users);
    }

    //아이디 비밀 번호 검증
    public Users login(LoginRequestDto dto) {
        //아이디 확인
        Users user = usersRe.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 틀립니다."));

        //비밀 번호 확인
        if () {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 틀립니다.");
        }

        return user;
    }

}
