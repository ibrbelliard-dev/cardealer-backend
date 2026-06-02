package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.AppUser;
import com.cardealer.iotproject.model.entity.UserRole;
import com.cardealer.iotproject.model.entity.Permiso;
import com.cardealer.iotproject.dto.LoginRequest;
import com.cardealer.iotproject.model.dto.UserDTO;
import com.cardealer.iotproject.repository.AppUserRepository;
import com.cardealer.iotproject.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    private static final Logger log = Logger.getLogger(AuthService.class.getName());
    
    @Autowired
    private AppUserRepository appUserRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    

// src/main/java/com/cardealer/iotproject/service/AuthService.java

@Transactional
public Map<String, Object> authenticate(LoginRequest loginRequest) {
    Optional<AppUser> userOpt = appUserRepository.findByUsername(loginRequest.getUsername());
    
    if (userOpt.isPresent()) {
        AppUser user = userOpt.get();
        
        if (user.getPassword().equals(loginRequest.getPassword())) {
            user.setLastLogin(LocalDateTime.now());
            appUserRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("email", user.getEmail());
            response.put("cellPhone", user.getCellPhone());
            response.put("isActive", user.getIsActive());
            response.put("lastLogin", user.getLastLogin());
            
            // Generar un token simple (en producción usar JWT)
            String token = generateSimpleToken(user.getUsername());
            response.put("token", token);
            
            // Información del rol
            Map<String, Object> roleInfo = new HashMap<>();
            roleInfo.put("id", user.getRole().getRoleId());
            roleInfo.put("name", user.getRole().getRoleTitle());
            
            // Obtener permisos del rol
            if (user.getRole().getPermisos() != null) {
                List<Map<String, String>> permisosList = user.getRole().getPermisos().stream()
                    .map(p -> {
                        Map<String, String> perm = new HashMap<>();
                        perm.put("nombre", p.getNombre());
                        perm.put("modulo", p.getModulo());
                        perm.put("accion", p.getAccion());
                        perm.put("descripcion", p.getDescripcion());
                        return perm;
                    })
                    .collect(Collectors.toList());
                roleInfo.put("permisos", permisosList);
            } else {
                roleInfo.put("permisos", List.of());
            }
            
            response.put("role", roleInfo);
            
            return response;
        }
    }
    return null;
}

private String generateSimpleToken(String username) {
    // Token simple - en producción usar JWT
    return Base64.getEncoder().encodeToString((username + ":" + System.currentTimeMillis()).getBytes());
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
        String[] roles = {"SUPER_ADMIN", "ADMIN", "GERENTE", "CONTADOR", "VENDEDOR", "MECANICO", "CONSULTOR"};
        for (String roleTitle : roles) {
            if (!userRoleRepository.existsByRoleTitle(roleTitle)) {
                UserRole role = new UserRole();
                role.setRoleTitle(roleTitle);
                role.setDescription(getRoleDescription(roleTitle));
                userRoleRepository.save(role);
                log.info("Created role: " + roleTitle);
            }
        }
    }
    
    private String getRoleDescription(String roleTitle) {
        switch (roleTitle) {
            case "SUPER_ADMIN": return "Acceso total al sistema";
            case "ADMIN": return "Administrador del sistema";
            case "GERENTE": return "Gerente general";
            case "CONTADOR": return "Acceso al módulo de contabilidad";
            case "VENDEDOR": return "Acceso a ventas y clientes";
            case "MECANICO": return "Acceso a servicio y mantenimiento";
            case "CONSULTOR": return "Acceso solo de lectura";
            default: return roleTitle + " role";
        }
    }
    
    @Transactional
    public void createDefaultAdmin() {
        if (!appUserRepository.existsByUsername("admin")) {
            // Buscar rol SUPER_ADMIN o ADMIN
            UserRole adminRole = userRoleRepository.findByRoleTitle("SUPER_ADMIN")
                .orElse(userRoleRepository.findByRoleTitle("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found")));
            
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@cardealer.com");
            admin.setPassword("admin123");
            admin.setCellPhone("809-555-0000");
            admin.setSecretCode("ADMIN001");
            admin.setRole(adminRole);
            admin.setIsActive(true);
            
            appUserRepository.save(admin);
            log.info("Created default admin user with role: " + adminRole.getRoleTitle());
        }
    }
}