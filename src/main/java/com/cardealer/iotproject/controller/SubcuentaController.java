package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.Subcuenta;
import com.cardealer.iotproject.service.SubcuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounting/subcuentas")
public class SubcuentaController {

    @Autowired
    private SubcuentaService subcuentaService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        List<Subcuenta> subcuentas = subcuentaService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Subcuentas obtenidas exitosamente", subcuentas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Integer id) {
        Subcuenta subcuenta = subcuentaService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Subcuenta obtenida exitosamente", subcuenta));
    }

    @GetMapping("/cuenta-maestra/{cuentaMaestraId}")
    public ResponseEntity<ApiResponse> getByCuentaMaestra(@PathVariable Integer cuentaMaestraId) {
        List<Subcuenta> subcuentas = subcuentaService.findByCuentaMaestra(cuentaMaestraId);
        return ResponseEntity.ok(ApiResponse.success("Subcuentas obtenidas exitosamente", subcuentas));
    }

    @GetMapping("/cuenta-maestra/codigo/{codigoMaestra}")
    public ResponseEntity<ApiResponse> getByCuentaMaestraCodigo(@PathVariable Integer codigoMaestra) {
        List<Subcuenta> subcuentas = subcuentaService.findByCuentaMaestraCodigo(codigoMaestra);
        return ResponseEntity.ok(ApiResponse.success("Subcuentas obtenidas exitosamente", subcuentas));
    }

    @PostMapping("/cuenta-maestra/{cuentaMaestraId}")
    public ResponseEntity<ApiResponse> create(@PathVariable Integer cuentaMaestraId, @RequestBody Subcuenta subcuenta) {
        Subcuenta nuevaSubcuenta = subcuentaService.create(subcuenta, cuentaMaestraId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subcuenta creada exitosamente", nuevaSubcuenta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody Subcuenta subcuenta) {
        Subcuenta subcuentaActualizada = subcuentaService.update(id, subcuenta);
        return ResponseEntity.ok(ApiResponse.success("Subcuenta actualizada exitosamente", subcuentaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        subcuentaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Subcuenta desactivada exitosamente", null));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse> activate(@PathVariable Integer id) {
        subcuentaService.activate(id);
        return ResponseEntity.ok(ApiResponse.success("Subcuenta activada exitosamente", null));
    }
}