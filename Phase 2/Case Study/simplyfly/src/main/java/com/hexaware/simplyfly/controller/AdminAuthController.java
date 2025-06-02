package com.hexaware.simplyfly.controller;

import com.hexaware.simplyfly.dto.AuthRequest;
import com.hexaware.simplyfly.dto.AuthResponse;
import com.hexaware.simplyfly.dto.RegisterRequest;
import com.hexaware.simplyfly.entity.Role;
import com.hexaware.simplyfly.security.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AdminAuthController handles authentication endpoints specific to Admin users.
 * It provides separate registration and login APIs for admins under /api/v1/auth/admin.
 * Internally, it sets the role to ADMIN during registration and delegates logic to AuthenticationService.
 * 
 * Developer: N Lohith Reddy  
 * Created on: May 29, 2025  
 * Last Modified: May 30, 2025  
 */
@RestController
@RequestMapping("/api/v1/auth/admin")
public class AdminAuthController {

    private final AuthenticationService authService;

    public AdminAuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        request.setRole(Role.ADMIN);
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
