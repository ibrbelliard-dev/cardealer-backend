package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.dto.LoginRequest;
import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.UserDTO;
import com.cardealer.iotproject.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cardealer.iotproject.config.AppConfig;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
// @CrossOrigin(origins = "http://localhost:8085")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
      private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public AuthController(AuthService authService, AppConfig appConfig) {
        this.authService = authService;
        this.appConfig = appConfig;
    }

    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UserDTO user = authService.authenticate(loginRequest);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", "demo-token-" + System.currentTimeMillis());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        }
        return ResponseEntity.status(401).body(ApiResponse.error("Invalid credentials"));
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse> register(@RequestBody com.cardealer.iotproject.model.entity.AppUser user, 
                                                  @RequestParam(defaultValue = "employee") String role) {
        try {
            com.cardealer.iotproject.model.entity.AppUser savedUser = authService.registerUser(user, role);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/init")
    @Operation(summary = "Initialize roles and default admin")
    public ResponseEntity<ApiResponse> init() {
        authService.initRoles();
        authService.createDefaultAdmin();
        return ResponseEntity.ok(ApiResponse.success("Roles and admin user initialized", null));
    }
}