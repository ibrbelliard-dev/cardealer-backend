package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.AppUser;
import com.cardealer.iotproject.model.entity.UserRole;
import com.cardealer.iotproject.repository.AppUserRepository;
import com.cardealer.iotproject.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {
    
    private static final Logger log = Logger.getLogger(UserService.class.getName());
    
    @Autowired
    private AppUserRepository appUserRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Transactional(readOnly = true)
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public AppUser getUserById(Long id) {
        return appUserRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<UserRole> getAllRoles() {
        return userRoleRepository.findAll();
    }
    
    @Transactional
    public AppUser registerUser(AppUser user) {
        // Check if username exists
        if (appUserRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        // Check if email exists
        if (appUserRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        // Get default role (employee) if not specified
        UserRole role;
        if (user.getRole() != null && user.getRole().getRoleId() != null) {
            role = userRoleRepository.findById(user.getRole().getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            role = userRoleRepository.findByRoleTitle("employee")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        }
        
        user.setRole(role);
        user.setIsActive(true);
        
        // In production, encode password with BCrypt
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return appUserRepository.save(user);
    }
    
    @Transactional
    public AppUser updateUser(Long id, AppUser userDetails) {
        AppUser user = getUserById(id);
        
        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEmail() != null) {
            // Check if email is taken by another user
            if (!user.getEmail().equals(userDetails.getEmail()) && 
                appUserRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already exists: " + userDetails.getEmail());
            }
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getCellPhone() != null) {
            user.setCellPhone(userDetails.getCellPhone());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            // In production, encode password
            user.setPassword(userDetails.getPassword());
        }
        if (userDetails.getRole() != null && userDetails.getRole().getRoleId() != null) {
            UserRole role = userRoleRepository.findById(userDetails.getRole().getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }
        if (userDetails.getIsActive() != null) {
            user.setIsActive(userDetails.getIsActive());
        }
        
        return appUserRepository.save(user);
    }
    
    @Transactional
    public AppUser toggleUserStatus(Long id) {
        AppUser user = getUserById(id);
        user.setIsActive(!user.getIsActive());
        log.info("User " + user.getUsername() + " status toggled to: " + user.getIsActive());
        return appUserRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        AppUser user = getUserById(id);
        appUserRepository.delete(user);
        log.info("User deleted: " + user.getUsername());
    }
}