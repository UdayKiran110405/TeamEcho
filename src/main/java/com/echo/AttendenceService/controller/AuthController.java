package com.echo.AttendenceService.controller;

import com.echo.AttendenceService.dto.request.LoginRequest;
import com.echo.AttendenceService.dto.request.RegisterRequest;
import com.echo.AttendenceService.dto.response.ApiResponse;
import com.echo.AttendenceService.dto.response.AuthResponse;
import com.echo.AttendenceService.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully", null), HttpStatus.CREATED);
    }
}