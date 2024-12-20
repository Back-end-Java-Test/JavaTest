package com.test.javatest.user.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRes {

    private String username;
    private String nickname;
    private List<Authority> authorities;

    // Authority 클래스
    @Getter
    public static class Authority {
        private String authorityName;

        public Authority(String authorityName) {
            this.authorityName = authorityName;
        }
    }
}
