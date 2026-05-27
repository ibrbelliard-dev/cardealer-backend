package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.*;
import com.cardealer.iotproject.model.enums.VehicleStatus;
import com.cardealer.iotproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteContableService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private SalesRepRepository salesRepRepository;
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private CommissionRepository commissionRepository;

    /**
     * BALANCE GENERAL - Estado de situación financiera a una fecha específica
     */
    public Map<String, Object> getBalanceGeneral(LocalDate fecha) {
        Map<String, Object> balance = new HashMap<>();
        
        LocalDateTime fechaCorte = fecha.atTime(LocalTime.MAX);
        
        // ========== ACTIVO ==========
        List<Map<String, Object>> activos = new ArrayList<>();
        BigDecimal totalActivos = BigDecimal.ZERO;
        
        // 1.1 Caja y Bancos (Pagos recibidos hasta la fecha)
        BigDecimal cajaBancos = getCajaBancosHasta(fechaCorte);
        
        Map<String, Object> caja = new HashMap<>();
        caja.put("cuenta", "Caja y Bancos");
        caja.put("saldo", cajaBancos);
        caja.put("tipo", "ACTIVO_CORRIENTE");
        activos.add(caja);
        totalActivos = totalActivos.add(cajaBancos);
        
        // 1.2 Cuentas por Cobrar (Facturas pendientes hasta la fecha)
        BigDecimal cuentasPorCobrar = getCuentasPorCobrarHasta(fechaCorte);
        
        Map<String, Object> cuentasCobrar = new HashMap<>();
        cuentasCobrar.put("cuenta", "Cuentas por Cobrar");
        cuentasCobrar.put("saldo", cuentasPorCobrar);
        cuentasCobrar.put("tipo", "ACTIVO_CORRIENTE");
        activos.add(cuentasCobrar);
        totalActivos = totalActivos.add(cuentasPorCobrar);
        
        // 1.3 Inventario de Vehículos (Vehículos AVAILABLE)
        List<Vehicle> vehiculosInventario = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
        BigDecimal valorInventario = vehiculosInventario.stream()
                .filter(v -> v.getPurchasePrice() != null)
                .map(Vehicle::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> inventario = new HashMap<>();
        inventario.put("cuenta", "Inventario de Vehículos");
        inventario.put("saldo", valorInventario);
        inventario.put("tipo", "ACTIVO_CORRIENTE");
        activos.add(inventario);
        totalActivos = totalActivos.add(valorInventario);
        
        // 1.4 Activo Fijo (Vehículos vendidos - costo histórico)
        List<Vehicle> vehiculosVendidos = vehicleRepository.findByStatus(VehicleStatus.SOLD);
        BigDecimal activoFijo = vehiculosVendidos.stream()
                .filter(v -> v.getPurchasePrice() != null)
                .map(Vehicle::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> activoFijoMap = new HashMap<>();
        activoFijoMap.put("cuenta", "Activo Fijo (Vehículos Vendidos)");
        activoFijoMap.put("saldo", activoFijo);
        activoFijoMap.put("tipo", "ACTIVO_NO_CORRIENTE");
        activos.add(activoFijoMap);
        totalActivos = totalActivos.add(activoFijo);
        
        Map<String, Object> activo = new HashMap<>();
        activo.put("cuentas", activos);
        activo.put("total", totalActivos);
        balance.put("activo", activo);
        
        // ========== PASIVO ==========
        List<Map<String, Object>> pasivos = new ArrayList<>();
        BigDecimal totalPasivos = BigDecimal.ZERO;
        
        // 2.1 Comisiones por Pagar (PENDING hasta la fecha)
        BigDecimal comisionesPorPagar = getComisionesPendientesHasta(fechaCorte);
        
        Map<String, Object> comisiones = new HashMap<>();
        comisiones.put("cuenta", "Comisiones por Pagar");
        comisiones.put("saldo", comisionesPorPagar);
        comisiones.put("tipo", "PASIVO_CORRIENTE");
        pasivos.add(comisiones);
        totalPasivos = totalPasivos.add(comisionesPorPagar);
        
        // 2.2 ITBIS por Pagar (18% de las ventas totales)
        BigDecimal totalVentas = getTotalVentasHasta(fechaCorte);
        BigDecimal itbisPorPagar = totalVentas.multiply(new BigDecimal("0.18"));
        
        Map<String, Object> itbis = new HashMap<>();
        itbis.put("cuenta", "ITBIS por Pagar");
        itbis.put("saldo", itbisPorPagar);
        itbis.put("tipo", "PASIVO_CORRIENTE");
        pasivos.add(itbis);
        totalPasivos = totalPasivos.add(itbisPorPagar);
        
        // 2.3 Cuentas por Pagar - Proveedores (70% del inventario estimado)
        BigDecimal cuentasPorPagarProveedores = valorInventario.multiply(new BigDecimal("0.7"));
        
        Map<String, Object> proveedores = new HashMap<>();
        proveedores.put("cuenta", "Cuentas por Pagar - Proveedores");
        proveedores.put("saldo", cuentasPorPagarProveedores);
        proveedores.put("tipo", "PASIVO_CORRIENTE");
        pasivos.add(proveedores);
        totalPasivos = totalPasivos.add(cuentasPorPagarProveedores);
        
        Map<String, Object> pasivo = new HashMap<>();
        pasivo.put("cuentas", pasivos);
        pasivo.put("total", totalPasivos);
        balance.put("pasivo", pasivo);
        
        // ========== PATRIMONIO ==========
        List<Map<String, Object>> patrimonios = new ArrayList<>();
        BigDecimal totalPatrimonio = BigDecimal.ZERO;
        
        // 3.1 Capital Social (Activo - Pasivo)
        BigDecimal capitalSocial = totalActivos.subtract(totalPasivos);
        if (capitalSocial.compareTo(BigDecimal.ZERO) < 0) capitalSocial = BigDecimal.ZERO;
        
        Map<String, Object> capital = new HashMap<>();
        capital.put("cuenta", "Capital Social");
        capital.put("saldo", capitalSocial);
        capital.put("tipo", "PATRIMONIO");
        patrimonios.add(capital);
        totalPatrimonio = totalPatrimonio.add(capitalSocial);
        
        // 3.2 Resultado del Ejercicio (Utilidades Acumuladas)
        BigDecimal resultadoEjercicio = getUtilidadAcumuladaHasta(fechaCorte);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("cuenta", "Resultado del Ejercicio");
        resultado.put("saldo", resultadoEjercicio);
        resultado.put("tipo", "PATRIMONIO");
        patrimonios.add(resultado);
        totalPatrimonio = totalPatrimonio.add(resultadoEjercicio);
        
        Map<String, Object> patrimonio = new HashMap<>();
        patrimonio.put("cuentas", patrimonios);
        patrimonio.put("total", totalPatrimonio);
        balance.put("patrimonio", patrimonio);
        
        // Verificar cuadre contable
        BigDecimal totalPasivoPatrimonio = totalPasivos.add(totalPatrimonio);
        balance.put("totalPasivoPatrimonio", totalPasivoPatrimonio);
        balance.put("diferencia", totalActivos.subtract(totalPasivoPatrimonio).abs());
        balance.put("estaCuadrado", totalActivos.compareTo(totalPasivoPatrimonio) == 0);
        
        return balance;
    }

    /**
     * ESTADO DE RESULTADOS - Para un período específico
     */
    public Map<String, Object> getEstadoResultados(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> resultados = new HashMap<>();
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        // ========== INGRESOS ==========
        List<Map<String, Object>> detallesIngresos = new ArrayList<>();
        BigDecimal totalIngresos = BigDecimal.ZERO;
        
        // 1.1 Ventas de Vehículos (facturas PAID/COMPLETED/CONFIRMED en el período)
        List<Invoice> facturasPeriodo = invoiceRepository.findByStatusInAndInvoiceDateTimeBetween(
            List.of("PAID", "COMPLETED", "CONFIRMED"), inicio, fin);
        
        BigDecimal ventasVehiculos = facturasPeriodo.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> ventas = new HashMap<>();
        ventas.put("cuenta", "Venta de Vehículos");
        ventas.put("monto", ventasVehiculos);
        detallesIngresos.add(ventas);
        totalIngresos = totalIngresos.add(ventasVehiculos);
        
        // 1.2 Ingresos por Servicios (mantenimientos completados en el período)
        List<ServiceRequest> serviciosPeriodo = serviceRequestRepository.findByCompletedDateBetween(inicio, fin);
        BigDecimal ingresosServicios = serviciosPeriodo.stream()
                .filter(s -> s.getActualCost() != null)
                .map(ServiceRequest::getActualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> servicios = new HashMap<>();
        servicios.put("cuenta", "Ingresos por Servicios");
        servicios.put("monto", ingresosServicios);
        detallesIngresos.add(servicios);
        totalIngresos = totalIngresos.add(ingresosServicios);
        
        Map<String, Object> ingresos = new HashMap<>();
        ingresos.put("detalles", detallesIngresos);
        ingresos.put("total", totalIngresos);
        resultados.put("ingresos", ingresos);
        
        // ========== COSTOS DE VENTAS ==========
        List<Map<String, Object>> detallesCostos = new ArrayList<>();
        BigDecimal totalCostos = BigDecimal.ZERO;
        
        // 2.1 Costo de Vehículos Vendidos (purchase price de vehículos vendidos en el período)
        BigDecimal costoVehiculos = facturasPeriodo.stream()
                .filter(f -> f.getVehicle() != null && f.getVehicle().getPurchasePrice() != null)
                .map(f -> f.getVehicle().getPurchasePrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> costoVeh = new HashMap<>();
        costoVeh.put("cuenta", "Costo de Vehículos Vendidos");
        costoVeh.put("monto", costoVehiculos);
        detallesCostos.add(costoVeh);
        totalCostos = totalCostos.add(costoVehiculos);
        
        Map<String, Object> costos = new HashMap<>();
        costos.put("detalles", detallesCostos);
        costos.put("total", totalCostos);
        resultados.put("costos", costos);
        
        // Utilidad Bruta
        BigDecimal utilidadBruta = totalIngresos.subtract(totalCostos);
        resultados.put("utilidadBruta", utilidadBruta);
        resultados.put("utilidadBrutaPorcentaje", calcularPorcentaje(utilidadBruta, totalIngresos));
        
        // ========== GASTOS OPERATIVOS ==========
        List<Map<String, Object>> detallesGastos = new ArrayList<>();
        BigDecimal totalGastos = BigDecimal.ZERO;
        
        // 3.1 Comisiones por Ventas (generadas en el período)
        BigDecimal comisionesPeriodo = getComisionesGeneradasEnPeriodo(inicio, fin);
        
        Map<String, Object> comisionesGasto = new HashMap<>();
        comisionesGasto.put("cuenta", "Comisiones por Ventas");
        comisionesGasto.put("monto", comisionesPeriodo);
        detallesGastos.add(comisionesGasto);
        totalGastos = totalGastos.add(comisionesPeriodo);
        
        // 3.2 Costo de Mantenimiento (servicios en el período)
        BigDecimal costosMantenimiento = serviciosPeriodo.stream()
                .filter(s -> s.getActualCost() != null)
                .map(ServiceRequest::getActualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> mantenimiento = new HashMap<>();
        mantenimiento.put("cuenta", "Costos de Mantenimiento");
        mantenimiento.put("monto", costosMantenimiento);
        detallesGastos.add(mantenimiento);
        totalGastos = totalGastos.add(costosMantenimiento);
        
        // 3.3 Gastos Administrativos (estimado - 15% de los gastos operativos)
        BigDecimal gastosAdministrativos = totalGastos.multiply(new BigDecimal("0.15"));
        
        Map<String, Object> adminGastos = new HashMap<>();
        adminGastos.put("cuenta", "Gastos Administrativos");
        adminGastos.put("monto", gastosAdministrativos);
        detallesGastos.add(adminGastos);
        totalGastos = totalGastos.add(gastosAdministrativos);
        
        Map<String, Object> gastos = new HashMap<>();
        gastos.put("detalles", detallesGastos);
        gastos.put("total", totalGastos);
        resultados.put("gastos", gastos);
        
        // Utilidad Operativa
        BigDecimal utilidadOperativa = utilidadBruta.subtract(totalGastos);
        resultados.put("utilidadOperativa", utilidadOperativa);
        resultados.put("utilidadOperativaPorcentaje", calcularPorcentaje(utilidadOperativa, totalIngresos));
        
        // Utilidad Neta (antes de impuestos)
        resultados.put("utilidadNeta", utilidadOperativa);
        resultados.put("utilidadNetaPorcentaje", calcularPorcentaje(utilidadOperativa, totalIngresos));
        
        return resultados;
    }

    // ReporteContableService.java - Método getLibroMayor corregido

public List<Map<String, Object>> getLibroMayor(String cuentaCodigo, LocalDate fechaInicio, LocalDate fechaFin) {
    List<Map<String, Object>> movimientos = new ArrayList<>();
    
    LocalDateTime inicio = fechaInicio.atStartOfDay();
    LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
    
    BigDecimal saldoAcumulado = BigDecimal.ZERO;
    
    // Usar el método existente findPaidInvoicesBetween
    List<Invoice> facturasPeriodo = invoiceRepository.findPaidInvoicesBetween(inicio, fin);
    
    // Si no existe, usar findByStatus y filtrar manualmente
    // List<Invoice> facturasPeriodo = invoiceRepository.findByStatus("PAID");
    
    for (Invoice factura : facturasPeriodo) {
        Map<String, Object> movimiento = new HashMap<>();
        movimiento.put("fecha", factura.getInvoiceDateTime().toLocalDate().toString());
        movimiento.put("numeroAsiento", "FACT-" + factura.getEnNcf());
        movimiento.put("descripcion", "Venta - " + factura.getCustomerName());
        movimiento.put("debe", factura.getTotal());
        movimiento.put("haber", BigDecimal.ZERO);
        saldoAcumulado = saldoAcumulado.add(factura.getTotal());
        movimiento.put("saldo", saldoAcumulado);
        
        // Si no hay filtro de cuenta, mostrar todos
        if (cuentaCodigo == null || cuentaCodigo.isEmpty() || cuentaCodigo.equals("all")) {
            movimientos.add(movimiento);
        }
    }
    
    // Ordenar por fecha
    movimientos.sort(Comparator.comparing(m -> (String) m.get("fecha")));
    
    return movimientos;
}

    // ========== MÉTODOS AUXILIARES PARA CÁLCULOS REALES ==========

    private BigDecimal getCajaBancosHasta(LocalDateTime fechaCorte) {
        BigDecimal pagos = paymentRepository.getTotalPaymentsAmount();
        if (pagos == null) pagos = BigDecimal.ZERO;
        return pagos;
    }

    private BigDecimal getCuentasPorCobrarHasta(LocalDateTime fechaCorte) {
        List<Invoice> facturasPendientes = invoiceRepository.findByStatus("PENDING");
        List<Invoice> facturasParciales = invoiceRepository.findByStatus("PARTIALLY_PAID");
        
        BigDecimal cuentasPorCobrar = BigDecimal.ZERO;
        for (Invoice inv : facturasPendientes) {
            if (inv.getInvoiceDateTime().isBefore(fechaCorte)) {
                cuentasPorCobrar = cuentasPorCobrar.add(inv.getTotal());
            }
        }
        for (Invoice inv : facturasParciales) {
            if (inv.getInvoiceDateTime().isBefore(fechaCorte)) {
                BigDecimal pagado = paymentRepository.getTotalPaidByInvoiceId(inv.getId());
                if (pagado == null) pagado = BigDecimal.ZERO;
                cuentasPorCobrar = cuentasPorCobrar.add(inv.getTotal().subtract(pagado));
            }
        }
        return cuentasPorCobrar;
    }

    private BigDecimal getComisionesPendientesHasta(LocalDateTime fechaCorte) {
        List<Commission> comisiones = commissionRepository.findByStatus("PENDING");
        return comisiones.stream()
                .filter(c -> c.getCreatedAt().isBefore(fechaCorte))
                .map(Commission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalVentasHasta(LocalDateTime fechaCorte) {
        List<Invoice> facturas = invoiceRepository.findByStatusIn(List.of("PAID", "COMPLETED", "CONFIRMED"));
        return facturas.stream()
                .filter(f -> f.getInvoiceDateTime().isBefore(fechaCorte))
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getUtilidadAcumuladaHasta(LocalDateTime fechaCorte) {
        // Utilidad = Ventas totales - Costo de vehículos vendidos - Comisiones pagadas
        BigDecimal ventas = getTotalVentasHasta(fechaCorte);
        
        List<Vehicle> vehiculosVendidos = vehicleRepository.findByStatus(VehicleStatus.SOLD);
        BigDecimal costoVehiculos = vehiculosVendidos.stream()
                .filter(v -> v.getPurchasePrice() != null)
                .map(Vehicle::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal comisionesPagadas = commissionRepository.getTotalPaidAll();
        if (comisionesPagadas == null) comisionesPagadas = BigDecimal.ZERO;
        
        return ventas.subtract(costoVehiculos).subtract(comisionesPagadas);
    }

    private BigDecimal getComisionesGeneradasEnPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        List<Commission> comisiones = commissionRepository.findByCreatedAtBetween(inicio, fin);
        return comisiones.stream()
                .map(Commission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularPorcentaje(BigDecimal parte, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return parte.multiply(new BigDecimal("100")).divide(total, 1, RoundingMode.HALF_UP);
    }
}