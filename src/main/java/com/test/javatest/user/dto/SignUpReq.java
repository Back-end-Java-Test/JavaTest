package com.test.javatest.user.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class SignUpReq {

    @NotBlank
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    private String username;

    @NotBlank
    private String password;

}
