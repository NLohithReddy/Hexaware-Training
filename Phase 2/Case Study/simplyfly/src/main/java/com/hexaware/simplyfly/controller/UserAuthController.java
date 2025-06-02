package com.hexaware.simplyfly.controller;


import com.hexaware.simplyfly.dto.AuthRequest;
import com.hexaware.simplyfly.dto.AuthResponse;
import com.hexaware.simplyfly.dto.RegisterRequest;
import com.hexaware.simplyfly.entity.Role;
import com.hexaware.simplyfly.security.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * UserAuthController manages authentication endpoints for general users.
 * It provides dedicated APIs for user registration and login under /api/v1/auth/user.
 * During registration, the role is explicitly assigned as USER before processing.
 *
 * The controller forwards authentication logic to AuthenticationService.
 *
 * Developer: N Lohith Reddy  
 * Created on: May 29, 2025  
 * Last Modified: May 30, 2025  
 */

@RestController
@RequestMapping("/api/v1/auth/user")
public class UserAuthController {

    private final AuthenticationService authService;

    public UserAuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        request.setRole(Role.USER);
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
