package com.hexaware.simplyfly.controller;

import com.hexaware.simplyfly.dto.AuthRequest;
import com.hexaware.simplyfly.dto.AuthResponse;
import com.hexaware.simplyfly.dto.RegisterRequest;
import com.hexaware.simplyfly.entity.Role;
import com.hexaware.simplyfly.security.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * FlightOwnerAuthController handles authentication endpoints for Flight Owners.
 * It provides separate registration and login APIs under /api/v1/auth/owner.
 * During registration, the role is explicitly set to FLIGHT_OWNER before processing.
 *
 * The controller delegates all authentication logic to AuthenticationService.
 *
 * Developer: N Lohith Reddy  
 * Created on: May 29, 2025  
 * Last Modified: May 30, 2025  
 */

@RestController
@RequestMapping("/api/v1/auth/owner")
public class FlightOwnerAuthController {

    private final AuthenticationService authService;

    public FlightOwnerAuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        request.setRole(Role.FLIGHT_OWNER);
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
