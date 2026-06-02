package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.dto.LoginRequest;
import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.AppUser;
import com.cardealer.iotproject.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> userData = authService.authenticate(loginRequest);
        
        if (userData != null) {
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", userData));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Credenciales inválidas"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody AppUser user, @RequestParam String roleTitle) {
        try {
            AppUser savedUser = authService.registerUser(user, roleTitle);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario registrado exitosamente", savedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/init")
    public ResponseEntity<ApiResponse> init() {
        authService.initRoles();
        authService.createDefaultAdmin();
        return ResponseEntity.ok(ApiResponse.success("Roles y administrador inicializados", null));
    }
}