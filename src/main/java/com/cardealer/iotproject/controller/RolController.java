// src/main/java/com/cardealer/iotproject/controller/RolController.java
package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.RolDTO;
import com.cardealer.iotproject.model.dto.UsuarioRolDTO;
import com.cardealer.iotproject.model.entity.Permiso;
import com.cardealer.iotproject.model.entity.UserRole;
import com.cardealer.iotproject.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    // ========== ROLES ==========

    @GetMapping
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<UserRole> roles = rolService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles obtenidos exitosamente", roles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable Long id) {
        UserRole role = rolService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success("Rol obtenido exitosamente", role));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createRole(@RequestBody RolDTO rolDTO) {
        UserRole role = rolService.createRole(rolDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Rol creado exitosamente", role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRole(@PathVariable Long id, @RequestBody RolDTO rolDTO) {
        UserRole role = rolService.updateRole(id, rolDTO);
        return ResponseEntity.ok(ApiResponse.success("Rol actualizado exitosamente", role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable Long id) {
        rolService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Rol desactivado exitosamente", null));
    }

    // ========== PERMISOS ==========

    @GetMapping("/permisos")
    public ResponseEntity<ApiResponse> getAllPermisos() {
        List<Permiso> permisos = rolService.getAllPermisos();
        return ResponseEntity.ok(ApiResponse.success("Permisos obtenidos exitosamente", permisos));
    }

    @GetMapping("/permisos/agrupados")
    public ResponseEntity<ApiResponse> getPermisosAgrupados() {
        Map<String, List<Permiso>> permisos = rolService.getPermisosAgrupados();
        return ResponseEntity.ok(ApiResponse.success("Permisos agrupados obtenidos exitosamente", permisos));
    }

    // ========== ASIGNACIÓN DE ROLES ==========

    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse> getUsuariosConRoles() {
        List<UsuarioRolDTO> usuarios = rolService.getUsuariosConRoles();
        return ResponseEntity.ok(ApiResponse.success("Usuarios obtenidos exitosamente", usuarios));
    }

    @PostMapping("/usuarios/{userId}/asignar/{roleId}")
    public ResponseEntity<ApiResponse> asignarRol(@PathVariable Long userId, @PathVariable Long roleId) {
        rolService.asignarRol(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success("Rol asignado exitosamente", null));
    }

    @PatchMapping("/usuarios/{userId}/estado")
    public ResponseEntity<ApiResponse> cambiarEstadoUsuario(@PathVariable Long userId, @RequestParam Boolean isActive) {
        rolService.cambiarEstadoUsuario(userId, isActive);
        return ResponseEntity.ok(ApiResponse.success("Estado del usuario actualizado exitosamente", null));
    }
}