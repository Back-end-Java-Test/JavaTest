package com.test.javatest.user.security;

import com.test.javatest.user.entity.User;
import com.test.javatest.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new UsernameNotFoundException("Not Found " + nickname));


        // UserDetailsImpl 객체를 생성할 때 User 객체를 전달합니다.
        return new UserDetailsImpl(user);
    }
}

