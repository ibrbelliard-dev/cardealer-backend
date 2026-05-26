package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.Client;
import com.cardealer.iotproject.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clients")
@Tag(name = "Client Management", description = "Endpoints for client CRUD operations")
// @CrossOrigin(origins = "http://localhost:8085")
public class ClientController {
    
    @Autowired
    private ClientService clientService;
        private final AppConfig appConfig;  // ← AGREGAR

    
         // ← AGREGAR constructor
    public ClientController(ClientService clientService, AppConfig appConfig) {
        this.clientService = clientService;
        this.appConfig = appConfig;
    }
    @GetMapping
    @Operation(summary = "Get all clients")
    public ResponseEntity<ApiResponse> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        Page<Client> clients;
        if (search != null && !search.isEmpty()) {
            clients = clientService.searchClients(search, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        } else {
            clients = clientService.getAllClients(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Clientes recuperados", clients));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID")
    public ResponseEntity<ApiResponse> getClientById(@PathVariable Long id) {
        Client client = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponse.success("Cliente recuperado", client));
    }
    
    @GetMapping("/cedula/{cedula}")
    @Operation(summary = "Get client by cedula")
    public ResponseEntity<ApiResponse> getClientByCedula(@PathVariable String cedula) {
        var client = clientService.getClientByCedula(cedula);
        if (client.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Cliente recuperado", client.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Cliente no encontrado con cédula: " + cedula));
    }
    
    @GetMapping("/rnc/{rnc}")
    @Operation(summary = "Get client by RNC")
    public ResponseEntity<ApiResponse> getClientByRnc(@PathVariable String rnc) {
        var client = clientService.getClientByRnc(rnc);
        if (client.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Cliente recuperado", client.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Cliente no encontrado con RNC: " + rnc));
    }
    
    @PostMapping
    @Operation(summary = "Create a new client")
    public ResponseEntity<ApiResponse> createClient(@Valid @RequestBody Client client) {
        try {
            Client saved = clientService.createClient(client);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente creado exitosamente", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al crear cliente: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update client")
    public ResponseEntity<ApiResponse> updateClient(@PathVariable Long id, @Valid @RequestBody Client client) {
        try {
            Client updated = clientService.updateClient(id, client);
            return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al actualizar cliente: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update client status")
    public ResponseEntity<ApiResponse> updateClientStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            Client updated = clientService.updateClientStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Estado del cliente actualizado", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al actualizar estado: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete client")
    public ResponseEntity<ApiResponse> deleteClient(@PathVariable Long id) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.ok(ApiResponse.success("Cliente eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al eliminar cliente: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get client statistics")
    public ResponseEntity<ApiResponse> getClientStats() {
        List<Client> allClients = clientService.getAllClients();
        long totalClients = allClients.size();
        long activeClients = allClients.stream().filter(c -> c.getStatus() == 1).count();
        long inactiveClients = totalClients - activeClients;
        long businessClients = allClients.stream().filter(c -> c.getTipo() != null && c.getTipo() == 1).count();
        long individualClients = allClients.stream().filter(c -> c.getTipo() != null && c.getTipo() == 2).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClients", totalClients);
        stats.put("activeClients", activeClients);
        stats.put("inactiveClients", inactiveClients);
        stats.put("businessClients", businessClients);
        stats.put("individualClients", individualClients);
        
        return ResponseEntity.ok(ApiResponse.success("Estadísticas de clientes", stats));
    }
}