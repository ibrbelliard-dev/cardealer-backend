package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.AppUser;
import com.cardealer.iotproject.model.entity.UserRole;
import com.cardealer.iotproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
// @CrossOrigin(origins = "http://localhost:8085")
public class UserController {
    
    @Autowired
    private UserService userService;

    private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public UserController(UserService userService, AppConfig appConfig) {
        this.userService = userService;
        this.appConfig = appConfig;
    }
    
    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<AppUser> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        AppUser user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved", user));
    }
    
    @GetMapping("/roles")
    @Operation(summary = "Get all user roles")
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<UserRole> roles = userService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved", roles));
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody AppUser user) {
        try {
            AppUser savedUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody AppUser user) {
        try {
            AppUser updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle user active status")
    public ResponseEntity<ApiResponse> toggleUserStatus(@PathVariable Long id) {
        try {
            AppUser user = userService.toggleUserStatus(id);
            return ResponseEntity.ok(ApiResponse.success("User status updated", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}