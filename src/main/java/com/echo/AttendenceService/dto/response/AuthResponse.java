package com.echo.AttendenceService.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String name;
    private List<String> roles;

    public AuthResponse(String token, Long userId, String email, String name, List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }
}