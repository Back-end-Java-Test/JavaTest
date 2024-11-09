package com.test.javatest.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRes {
    private String token;

    public LoginRes(String token) {
        this.token = token;
    }
}
