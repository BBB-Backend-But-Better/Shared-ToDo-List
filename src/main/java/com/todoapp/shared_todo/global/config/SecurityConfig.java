package com.todoapp.shared_todo.global.config;


import com.todoapp.shared_todo.domain.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.todoapp.shared_todo.domain.auth.service.CustomOAuth2UserService;
import com.todoapp.shared_todo.global.security.JwtAuthenticationFilter;
import com.todoapp.shared_todo.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//필터는 다음 단계에서 만들꺼임. 지금은 이름만 만들어둘꺼임
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    //비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http // rest api 할꺼라서 기본 보안 끄기
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                //세션 설정, stateless로 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )


                //URL별 권환 관리(회원가입, 로그인 외에 접속 불가)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/oauth2/**", "/login/**", "/favicon.ico", //OAuth2인증
                                "/v3/api-docs/**", //스웨거
                                "/swagger-ui/**", //스웨거
                                "/swagger-ui.html") //스웨거
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler))
                
                //필터 등록 JWT 검사기 장착, 생성자 두개 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider,redisTemplate), UsernamePasswordAuthenticationFilter.class);

        
        return http.build();
    }
}
