package com.todoapp.shared_todo.domain.user.service;


import com.todoapp.shared_todo.domain.user.dto.request.ChangeNicknameRequest;
import com.todoapp.shared_todo.domain.user.dto.request.ChangePasswordRequest;
import com.todoapp.shared_todo.domain.user.dto.request.PasswordCheckRequest;
import com.todoapp.shared_todo.domain.user.dto.response.UserResponse;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    //정보조회
    public UserResponse getMyProfile(long userId) {
        User user = findUserById(userId);
        return UserResponse.from(user);
    }

    //닉네임 변경
    @Transactional
    public UserResponse changeNickname (long userId, ChangeNicknameRequest newNickname) {
        User user = findUserById(userId);

        if(!user.getNickname().equals(newNickname.nickname())){
            throw new RuntimeException("닉네임이 일치하지 않습니다.");
        }

        user.updateNickname(newNickname.nickname());

        return UserResponse.from(user);
    }

    //비밀번호 변경
    @Transactional
    public UserResponse changePassword(long userId, ChangePasswordRequest newPassword) {
        User user = findUserById(userId);

        //현재 비밀번호 일치 여부 확인
        if(!passwordEncoder.matches(newPassword.currentPassword(), user.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치 하지 않습니다.");
        }

        // 새 비밀번호가 맞는지 확인
        if(user.getPassword().equals(newPassword.newPassword())){
            throw new IllegalArgumentException("새 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다.");
        }

        //바뀐 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(newPassword.newPassword());
        user.updatePassword(encodedNewPassword);
        return UserResponse.from(user);
    }

    // 2-step 비밀번호 확인
    public void checkPassword(Long userId, PasswordCheckRequest request) {
        User user = findUserById(userId);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    //회원 탈퇴
    @Transactional
    public void withdraw(Long userId) {
        User user = findUserById(userId);
        user.withdraw(); // Status를 DELETED로 변경
    }

    private User findUserById(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

    }


}
