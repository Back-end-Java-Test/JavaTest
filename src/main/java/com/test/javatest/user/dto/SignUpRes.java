package com.test.javatest.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRes {

    @NotBlank
    private String nickname;

    @NotBlank
    private String username;

    @NotBlank
    private String role;


}
