package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.AppUser;
import com.cardealer.iotproject.model.entity.UserRole;
import com.cardealer.iotproject.dto.LoginRequest;
import com.cardealer.iotproject.model.dto.UserDTO;
import com.cardealer.iotproject.repository.AppUserRepository;
import com.cardealer.iotproject.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AuthService {
    
    private static final Logger log = Logger.getLogger(AuthService.class.getName());
    
    @Autowired
    private AppUserRepository appUserRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Transactional
    public UserDTO authenticate(LoginRequest loginRequest) {
        Optional<AppUser> userOpt = appUserRepository.findByUsername(loginRequest.getUsername());
        
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            
            // Simple password check (in production, use BCrypt)
            if (user.getPassword().equals(loginRequest.getPassword())) {
                user.setLastLogin(LocalDateTime.now());
                appUserRepository.save(user);
                
                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(user.getUserId());
                userDTO.setUsername(user.getUsername());
                userDTO.setFirstName(user.getFirstName());
                userDTO.setLastName(user.getLastName());
                userDTO.setEmail(user.getEmail());
                userDTO.setCellPhone(user.getCellPhone());
                userDTO.setRoleTitle(user.getRole().getRoleTitle());
                userDTO.setIsActive(user.getIsActive());
                userDTO.setLastLogin(user.getLastLogin());
                
                return userDTO;
            }
        }
        return null;
    }
    
    @Transactional
    public AppUser registerUser(AppUser user, String roleTitle) {
        // Check if user exists
        if (appUserRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (appUserRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Get role
        UserRole role = userRoleRepository.findByRoleTitle(roleTitle)
            .orElseThrow(() -> new RuntimeException("Role not found: " + roleTitle));
        
        user.setRole(role);
        
        // In production, encode password with BCrypt
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return appUserRepository.save(user);
    }
    
    @Transactional
    public void initRoles() {
        String[] roles = {"admin", "manager", "employee", "client"};
        for (String roleTitle : roles) {
            if (!userRoleRepository.existsByRoleTitle(roleTitle)) {
                UserRole role = new UserRole();
                role.setRoleTitle(roleTitle);
                role.setDescription(roleTitle.substring(0, 1).toUpperCase() + roleTitle.substring(1) + " role");
                userRoleRepository.save(role);
                log.info("Created role: " + roleTitle);
            }
        }
    }
    
    @Transactional
    public void createDefaultAdmin() {
        if (!appUserRepository.existsByUsername("admin")) {
            UserRole adminRole = userRoleRepository.findByRoleTitle("admin")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
            
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@cardealer.com");
            admin.setPassword("admin123"); // In production, encode this
            admin.setCellPhone("809-555-0000");
            admin.setSecretCode("ADMIN001");
            admin.setRole(adminRole);
            admin.setIsActive(true);
            
            appUserRepository.save(admin);
            log.info("Created default admin user");
        }
    }
}