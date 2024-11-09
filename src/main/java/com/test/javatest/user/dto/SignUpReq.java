package com.test.javatest.user.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class SignUpReq {

    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min = 3, max = 10, message = "아이디는 최소 3자 최대 10자 입니다.")
    private String nickname;

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(min = 1, max = 30, message = "이름은 최소 1자 최대 30자 입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 1, max = 10, message = "비밀번호는 최소 1자 최대 10자 입니다.")
    private String password;

}
