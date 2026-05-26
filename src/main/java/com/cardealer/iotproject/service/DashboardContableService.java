// src/main/java/com/cardealer/iotproject/service/DashboardContableService.java
package com.cardealer.iotproject.service;

import com.cardealer.iotproject.repository.CuentaMaestraRepository;
import com.cardealer.iotproject.repository.SubcuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardContableService {

    @Autowired
    private CuentaMaestraRepository cuentaMaestraRepository;

    @Autowired
    private SubcuentaRepository subcuentaRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total de cuentas
        long totalCuentas = cuentaMaestraRepository.count();
        long cuentasActivas = cuentaMaestraRepository.findByActivoTrue().size();
        long totalSubcuentas = subcuentaRepository.count();
        long subcuentasActivas = subcuentaRepository.findByActivoTrue().size();

        stats.put("totalCuentas", totalCuentas + totalSubcuentas);
        stats.put("cuentasActivas", cuentasActivas + subcuentasActivas);
        stats.put("totalCuentasMaestras", totalCuentas);
        stats.put("totalSubcuentas", totalSubcuentas);


        // Datos de ejemplo para el dashboard (mientras se implementan los cálculos reales)
        stats.put("totalActivos", 8750000.0);
        stats.put("totalPasivos", 3250000.0);
        stats.put("patrimonioNeto", 5500000.0);

        // Distribución por tipo de cuenta
        List<Map<String, Object>> distribucionPorTipo = new ArrayList<>();
        distribucionPorTipo.add(crearDistribucion("ACTIVO", 8750000.0, "#1976d2"));
        distribucionPorTipo.add(crearDistribucion("PASIVO", 3250000.0, "#ed6c02"));
        distribucionPorTipo.add(crearDistribucion("PATRIMONIO", 5500000.0, "#2e7d32"));
        stats.put("distribucionPorTipo", distribucionPorTipo);

        // Movimientos mensuales (ejemplo)
        List<Map<String, Object>> movimientosMensuales = new ArrayList<>();
        movimientosMensuales.add(crearMovimiento("Enero", 1250000.0, 1200000.0));
        movimientosMensuales.add(crearMovimiento("Febrero", 1350000.0, 1300000.0));
        movimientosMensuales.add(crearMovimiento("Marzo", 1450000.0, 1400000.0));
        movimientosMensuales.add(crearMovimiento("Abril", 1550000.0, 1500000.0));
        movimientosMensuales.add(crearMovimiento("Mayo", 1650000.0, 1600000.0));
        stats.put("movimientosMensuales", movimientosMensuales);

        return stats;
    }

    
    private Map<String, Object> crearDistribucion(String name, double value, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        item.put("color", color);
        return item;
    }

    private Map<String, Object> crearMovimiento(String month, double debe, double haber) {
        Map<String, Object> item = new HashMap<>();
        item.put("month", month);
        item.put("debe", debe);
        item.put("haber", haber);
        return item;
    }
}