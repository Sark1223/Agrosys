package com.agrosys.auth.dto.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private Integer userId;
    private String userName;
    private String message;
    private boolean success;
}