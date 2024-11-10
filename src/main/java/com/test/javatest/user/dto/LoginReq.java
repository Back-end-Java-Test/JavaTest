package com.test.javatest.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginReq {

    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min = 3, max = 10, message = "아이디는 최소 3자 최대 10자 입니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 1, max = 10, message = "비밀번호는 최소 1자 최대 10자 입니다.")
    private String password;
}