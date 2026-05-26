// src/main/java/com/cardealer/iotproject/service/AsientoContableService.java
package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.AsientoContable;
import com.cardealer.iotproject.model.entity.DetalleAsiento;
import com.cardealer.iotproject.accounting.exception.ResourceNotFoundException;
import com.cardealer.iotproject.repository.AsientoContableRepository;
import com.cardealer.iotproject.repository.DetalleAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsientoContableService {

    @Autowired
    private AsientoContableRepository asientoRepository;

    @Autowired
    private DetalleAsientoRepository detalleRepository;

    @Transactional(readOnly = true)
    public List<AsientoContable> findAll() {
        return asientoRepository.findByActivoTrueOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public AsientoContable findById(Integer id) {
        return asientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAsientoWithDetalles(Integer id) {
        AsientoContable asiento = findById(id);
        List<DetalleAsiento> detalles = detalleRepository.findByAsiento_AsientoId(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("asientoId", asiento.getAsientoId());
        result.put("numeroAsiento", asiento.getNumeroAsiento());
        result.put("fecha", asiento.getFecha());
        result.put("descripcion", asiento.getDescripcion());
        result.put("tipoAsiento", asiento.getTipoAsiento());
        result.put("estado", asiento.getEstado());
        result.put("totalDebe", asiento.getTotalDebe());
        result.put("totalHaber", asiento.getTotalHaber());
        result.put("detalles", detalles);
        
        return result;
    }

    @Transactional
    public AsientoContable create(AsientoContable asiento, List<DetalleAsiento> detalles) {
        // Generar número de asiento
        String lastNumero = asientoRepository.findLastNumeroAsiento();
        int nextNum = 1;
        if (lastNumero != null && lastNumero.startsWith("AS-")) {
            try {
                nextNum = Integer.parseInt(lastNumero.substring(3)) + 1;
            } catch (NumberFormatException e) {
                nextNum = 1;
            }
        }
        asiento.setNumeroAsiento(String.format("AS-%04d", nextNum));
        asiento.setEstado("PENDIENTE");
        asiento.setActivo(true);
        
        AsientoContable saved = asientoRepository.save(asiento);
        
        for (DetalleAsiento detalle : detalles) {
            detalle.setAsiento(saved);
            detalleRepository.save(detalle);
        }
        
        return saved;
    }

    @Transactional
    public AsientoContable aprobar(Integer id) {
        AsientoContable asiento = findById(id);
        asiento.setEstado("APROBADO");
        return asientoRepository.save(asiento);
    }

    
    @Transactional
    public void delete(Integer id) {
        AsientoContable asiento = findById(id);
        asiento.setActivo(false);
        asientoRepository.save(asiento);
    }
}