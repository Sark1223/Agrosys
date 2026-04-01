package com.agrosys.auth.dto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String userName;
    private Integer userId;
    private String rol;
    private List<String> modules;
    private Date expiresIn;
}