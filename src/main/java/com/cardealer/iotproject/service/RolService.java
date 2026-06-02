// src/main/java/com/cardealer/iotproject/service/RolService.java
package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.dto.RolDTO;
import com.cardealer.iotproject.model.dto.UsuarioRolDTO;
import com.cardealer.iotproject.model.entity.AppUser;
import com.cardealer.iotproject.model.entity.Permiso;
import com.cardealer.iotproject.model.entity.UserRole;
import com.cardealer.iotproject.repository.AppUserRepository;
import com.cardealer.iotproject.repository.PermisoRepository;
import com.cardealer.iotproject.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RolService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Transactional(readOnly = true)
    public List<UserRole> getAllRoles() {
        return userRoleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserRole getRoleById(Long id) {
        return userRoleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    @Transactional
    public UserRole createRole(RolDTO rolDTO) {
        if (userRoleRepository.existsByRoleTitle(rolDTO.getRoleTitle())) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rolDTO.getRoleTitle());
        }

        UserRole role = new UserRole();
        role.setRoleTitle(rolDTO.getRoleTitle());
        role.setDescription(rolDTO.getDescription());
        role.setIsActive(true);

        if (rolDTO.getPermisoIds() != null && !rolDTO.getPermisoIds().isEmpty()) {
            Set<Permiso> permisos = new HashSet<>(permisoRepository.findAllById(rolDTO.getPermisoIds()));
            role.setPermisos(permisos);
        }

        return userRoleRepository.save(role);
    }

    @Transactional
    public UserRole updateRole(Long id, RolDTO rolDTO) {
        UserRole role = getRoleById(id);

        if (rolDTO.getRoleTitle() != null && !rolDTO.getRoleTitle().equals(role.getRoleTitle())) {
            if (userRoleRepository.existsByRoleTitle(rolDTO.getRoleTitle())) {
                throw new RuntimeException("Ya existe un rol con el nombre: " + rolDTO.getRoleTitle());
            }
            role.setRoleTitle(rolDTO.getRoleTitle());
        }

        if (rolDTO.getDescription() != null) {
            role.setDescription(rolDTO.getDescription());
        }

        if (rolDTO.getIsActive() != null) {
            role.setIsActive(rolDTO.getIsActive());
        }

        if (rolDTO.getPermisoIds() != null) {
            Set<Permiso> permisos = new HashSet<>(permisoRepository.findAllById(rolDTO.getPermisoIds()));
            role.setPermisos(permisos);
        }

        role.setUpdatedAt(LocalDateTime.now());
        return userRoleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        UserRole role = getRoleById(id);
        role.setIsActive(false);
        role.setUpdatedAt(LocalDateTime.now());
        userRoleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public List<Permiso> getAllPermisos() {
        return permisoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<String, List<Permiso>> getPermisosAgrupados() {
        List<Permiso> allPermisos = permisoRepository.findAll();
        return allPermisos.stream()
            .collect(Collectors.groupingBy(Permiso::getModulo));
    }

    @Transactional(readOnly = true)
    public List<UsuarioRolDTO> getUsuariosConRoles() {
        List<AppUser> usuarios = appUserRepository.findAll();
        return usuarios.stream().map(this::convertToUsuarioRolDTO).collect(Collectors.toList());
    }

    @Transactional
    public void asignarRol(Long userId, Long roleId) {
        AppUser user = appUserRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UserRole role = getRoleById(roleId);
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        appUserRepository.save(user);
    }

    @Transactional
    public void cambiarEstadoUsuario(Long userId, Boolean isActive) {
        AppUser user = appUserRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setIsActive(isActive);
        user.setUpdatedAt(LocalDateTime.now());
        appUserRepository.save(user);
    }

    private UsuarioRolDTO convertToUsuarioRolDTO(AppUser user) {
        UsuarioRolDTO dto = new UsuarioRolDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCellPhone(user.getCellPhone());
        dto.setIsActive(user.getIsActive());
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getRoleId());
            dto.setRoleTitle(user.getRole().getRoleTitle());
        }
        return dto;
    }
}