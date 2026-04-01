package com.agrosys.web.dto.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;
    private String password;
}

