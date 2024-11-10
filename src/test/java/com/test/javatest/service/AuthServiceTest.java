package com.test.javatest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.test.javatest.user.dto.LoginReq;
import com.test.javatest.user.dto.SignUpReq;
import com.test.javatest.user.dto.SignUpRes;
import com.test.javatest.user.entity.User;
import com.test.javatest.user.entity.UserRoleEnum;
import com.test.javatest.user.repository.UserRepository;
import com.test.javatest.user.security.JwtUtil;
import com.test.javatest.user.service.AuthService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    private User user;
    private LoginReq loginReq;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Test user setup
        user = new User("nickname", "username", "encodedPassword", UserRoleEnum.USER);

        // LoginReq setup
        loginReq = new LoginReq("nickname", "password");
    }


    @Test
    void testSignUp() {
        SignUpReq signUpReq = new SignUpReq("nickname", "username", "password");
        User user = new User("nickname", "username", "encodedPassword", UserRoleEnum.USER);

        when(userRepository.findByNickname("nickname")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        SignUpRes signUpRes = authService.signUp(signUpReq);

        assertEquals("username", signUpRes.getUsername());
        assertEquals("nickname", signUpRes.getNickname());
    }

    @Test
    void testSignUpWithDuplicateUser() {
        // Given
        SignUpReq signUpReq = new SignUpReq("duplicateNickname", "username", "password");

        // 중복된 닉네임이 이미 존재한다고 설정
        when(userRepository.findByNickname("duplicateNickname")).thenReturn(Optional.of(new User()));

        // When & Then
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> authService.signUp(signUpReq)
        );

        assertEquals("중복된 사용자가 존재합니다.", exception.getMessage());
    }

//    @Test
//    void testLogin_Success() {
//        // Arrange: Mocking authenticationManager.authenticate()
//        Authentication authentication = mock(Authentication.class);
//        UserDetailsImpl userDetails = new UserDetailsImpl(user);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//            .thenReturn(authentication);
//
//        // Mocking JWT token creation
//        when(jwtUtil.createToken(any(Long.class), any(String.class), any(UserRoleEnum.class)))
//            .thenReturn("testToken");
//
//        // Act: Call the login method
//        LoginRes loginRes = authService.login(loginReq);
//
//        // Assert: Verify the token is returned and correct
//        assertNotNull(loginRes);
//        assertNotNull(loginRes.getToken());
//        assertEquals("testToken", loginRes.getToken());
//
//        // Verify the mock methods were called
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtUtil).createToken(any(Long.class), any(String.class), any(UserRoleEnum.class));
//    }

    @Test
    void testLoginWithInvalidCredentials() {
        // Given
        LoginReq loginReq = new LoginReq("invalidNickname", "wrongPassword");

        // 인증 실패 상황 설정
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("잘못된 아이디 혹은 비밀번호를 입력했습니다."));

        // When & Then
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> authService.login(loginReq)
        );

        assertEquals("잘못된 아이디 혹은 비밀번호를 입력했습니다.", exception.getMessage());
    }
}