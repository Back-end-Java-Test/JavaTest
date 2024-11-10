package com.test.javatest.user.dto;

import lombok.Getter;

@Getter
public class LoginRes {

    private String token;
    public LoginRes(String token) {
        this.token = token;
    }
}
