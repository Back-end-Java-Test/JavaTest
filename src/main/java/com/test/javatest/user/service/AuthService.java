package com.test.javatest.user.service;

import com.test.javatest.user.dto.LoginReq;
import com.test.javatest.user.dto.LoginRes;
import com.test.javatest.user.dto.SignUpReq;
import com.test.javatest.user.dto.SignUpRes;
import com.test.javatest.user.entity.User;
import com.test.javatest.user.entity.UserRoleEnum;
import com.test.javatest.user.repository.UserRepository;
import com.test.javatest.user.security.JwtUtil;
import com.test.javatest.user.security.UserDetailsImpl;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    @Transactional
    public SignUpRes signUp(SignUpReq req) {
        String nickname = req.getNickname();
        String username = req.getUsername();
        String password = passwordEncoder.encode(req.getPassword());

        // 회원 중복 확인
        Optional<User> checkNickname = userRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 기본고객
        UserRoleEnum role = UserRoleEnum.USER;

        // 사용자 등록
        User user = new User(nickname, username, password, role);
        userRepository.save(user);

        // SignUpRes 객체 생성 및 반환
        SignUpRes signUpRes = new SignUpRes();
        signUpRes.setUsername(username);
        signUpRes.setNickname(nickname);
        signUpRes.setAuthorities(Collections.singletonList(new SignUpRes.Authority(role.getAuthority())));

        return signUpRes;
    }

    // 로그인
    public LoginRes login(LoginReq req) {

        if (req.getNickname() == null || req.getNickname().isEmpty() || req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new RuntimeException("아이디 또는 비밀번호가 비어있습니다.");
        }

        try {
            // 사용자 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    req.getNickname(),
                    req.getPassword()
                )
            );

            // 인증 성공 시 사용자 정보 가져오기
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();

            Long userId = userDetails.getUser().getUserId();
            String username = userDetails.getUsername(); // 실제로는 nickname
            UserRoleEnum role = userDetails.getUser().getRole();

            // JWT 토큰 생성
            String token = jwtUtil.createToken(userId, username, role);

            // JWT 토큰을 LoginRes 객체에 담아서 반환
            return new LoginRes(token);

        } catch (AuthenticationException e) {
            throw new RuntimeException("잘못된 아이디 혹은 비밀번호를 입력했습니다.");
        }
    }
}
