package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetAutoLoginReq {
    private String email;
    private String password;
    private Boolean checkAutoLogin;
}