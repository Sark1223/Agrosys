package com.agrosys.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgrosysUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Integer userId;
    
    @Column(name = "userName", unique = true, nullable = false, length = 50)
    private String userName;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "token")
    private String token;
    
    @Column(name = "rolId")
    private Integer rolId;
    
}