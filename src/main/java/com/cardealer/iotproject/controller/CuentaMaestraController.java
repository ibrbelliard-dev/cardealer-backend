package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.CatalogoCompletoDTO;
import com.cardealer.iotproject.model.entity.CuentaMaestra;
import com.cardealer.iotproject.service.CuentaMaestraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounting/cuentas-maestras")
public class CuentaMaestraController {

    @Autowired
    private CuentaMaestraService cuentaMaestraService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        List<CuentaMaestra> cuentas = cuentaMaestraService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Cuentas obtenidas exitosamente", cuentas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Integer id) {
        CuentaMaestra cuenta = cuentaMaestraService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Cuenta obtenida exitosamente", cuenta));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ApiResponse> getByCodigo(@PathVariable Integer codigo) {
        CuentaMaestra cuenta = cuentaMaestraService.findByCodigo(codigo);
        return ResponseEntity.ok(ApiResponse.success("Cuenta obtenida exitosamente", cuenta));
    }

    @GetMapping("/tipo/{tipoCuenta}")
    public ResponseEntity<ApiResponse> getByTipoCuenta(@PathVariable String tipoCuenta) {
        List<CuentaMaestra> cuentas = cuentaMaestraService.findByTipoCuenta(tipoCuenta);
        return ResponseEntity.ok(ApiResponse.success("Cuentas obtenidas exitosamente", cuentas));
    }

    @GetMapping("/catalogo-completo")
    public ResponseEntity<ApiResponse> getCatalogoCompleto() {
        List<CatalogoCompletoDTO> catalogo = cuentaMaestraService.getCatalogoCompleto();
        return ResponseEntity.ok(ApiResponse.success("Catálogo completo obtenido exitosamente", catalogo));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody CuentaMaestra cuentaMaestra) {
        CuentaMaestra nuevaCuenta = cuentaMaestraService.create(cuentaMaestra);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cuenta creada exitosamente", nuevaCuenta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody CuentaMaestra cuentaMaestra) {
        CuentaMaestra cuentaActualizada = cuentaMaestraService.update(id, cuentaMaestra);
        return ResponseEntity.ok(ApiResponse.success("Cuenta actualizada exitosamente", cuentaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        cuentaMaestraService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Cuenta desactivada exitosamente", null));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse> activate(@PathVariable Integer id) {
        cuentaMaestraService.activate(id);
        return ResponseEntity.ok(ApiResponse.success("Cuenta activada exitosamente", null));
    }
}