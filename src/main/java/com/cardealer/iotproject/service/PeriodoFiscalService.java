// src/main/java/com/cardealer/iotproject/service/PeriodoFiscalService.java
package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.PeriodoFiscal;
import com.cardealer.iotproject.accounting.exception.ResourceNotFoundException;
import com.cardealer.iotproject.repository.PeriodoFiscalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PeriodoFiscalService {

    @Autowired
    private PeriodoFiscalRepository periodoFiscalRepository;

    @Transactional(readOnly = true)
    public List<PeriodoFiscal> findAll() {
        return periodoFiscalRepository.findByActivoTrueOrderByAnioDesc();
    }

    @Transactional(readOnly = true)
    public PeriodoFiscal findById(Integer id) {
        return periodoFiscalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Período fiscal no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public PeriodoFiscal findPeriodoAbierto() {
        return periodoFiscalRepository.findPeriodoAbierto()
                .orElseThrow(() -> new ResourceNotFoundException("No hay un período fiscal abierto"));
    }

    @Transactional
    public PeriodoFiscal create(PeriodoFiscal periodoFiscal) {
        System.out.println("📝 Creando período: " + periodoFiscal);
        
        // Verificar si ya existe un período para ese año
        if (periodoFiscalRepository.existsByAnioAndActivoTrue(periodoFiscal.getAnio())) {
            throw new RuntimeException("Ya existe un período fiscal activo para el año " + periodoFiscal.getAnio());
        }

        // Si el nuevo período se crea como ABIERTO, cerrar cualquier otro período abierto
        if ("ABIERTO".equals(periodoFiscal.getEstado())) {
            cerrarPeriodoAbierto();
        }

        periodoFiscal.setActivo(true);
        PeriodoFiscal saved = periodoFiscalRepository.save(periodoFiscal);
        System.out.println("✅ Período creado: " + saved);
        return saved;
    }

    @Transactional
    public PeriodoFiscal update(Integer id, PeriodoFiscal periodoFiscalDetails) {
        System.out.println("📝 Actualizando período ID: " + id);
        PeriodoFiscal periodoFiscal = findById(id);

        periodoFiscal.setAnio(periodoFiscalDetails.getAnio());
        periodoFiscal.setNombre(periodoFiscalDetails.getNombre());
        periodoFiscal.setFechaInicio(periodoFiscalDetails.getFechaInicio());
        periodoFiscal.setFechaFin(periodoFiscalDetails.getFechaFin());
        
        // Si se está cambiando a ABIERTO, cerrar otros períodos abiertos
        if ("ABIERTO".equals(periodoFiscalDetails.getEstado()) && !"ABIERTO".equals(periodoFiscal.getEstado())) {
            cerrarPeriodoAbierto();
        }
        
        periodoFiscal.setEstado(periodoFiscalDetails.getEstado());
        periodoFiscal.setUpdatedAt(LocalDateTime.now());

        PeriodoFiscal updated = periodoFiscalRepository.save(periodoFiscal);
        System.out.println("✅ Período actualizado: " + updated);
        return updated;
    }

    @Transactional
    public void cerrarPeriodo(Integer id) {
        System.out.println("🔒 Cerrando período ID: " + id);
        PeriodoFiscal periodoFiscal = findById(id);
        periodoFiscal.setEstado("CERRADO");
        periodoFiscal.setUpdatedAt(LocalDateTime.now());
        periodoFiscalRepository.save(periodoFiscal);
        System.out.println("✅ Período cerrado: " + periodoFiscal);
    }

    @Transactional
    public void activarPeriodo(Integer id) {
        System.out.println("✅ Activando período ID: " + id);
        // Cerrar cualquier período abierto actual
        cerrarPeriodoAbierto();
        
        PeriodoFiscal periodoFiscal = findById(id);
        periodoFiscal.setEstado("ABIERTO");
        periodoFiscal.setUpdatedAt(LocalDateTime.now());
        periodoFiscalRepository.save(periodoFiscal);
        System.out.println("✅ Período activado: " + periodoFiscal);
    }

    @Transactional
    public void delete(Integer id) {
        System.out.println("🗑️ Eliminando (soft delete) período ID: " + id);
        PeriodoFiscal periodoFiscal = findById(id);
        periodoFiscal.setActivo(false);
        periodoFiscal.setUpdatedAt(LocalDateTime.now());
        periodoFiscalRepository.save(periodoFiscal);
        System.out.println("✅ Período desactivado: " + periodoFiscal);
    }

    
    private void cerrarPeriodoAbierto() {
        periodoFiscalRepository.findPeriodoAbierto().ifPresent(periodoAbierto -> {
            System.out.println("🔒 Cerrando período abierto: " + periodoAbierto.getNombre());
            periodoAbierto.setEstado("CERRADO");
            periodoAbierto.setUpdatedAt(LocalDateTime.now());
            periodoFiscalRepository.save(periodoAbierto);
        });
    }
}