package com.agrosys.web.dto.login;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String userName;
    private Integer userId;
    private String rol;
    private List<String> modules;
    private Date expiresIn;
}