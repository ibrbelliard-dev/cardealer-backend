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
     * Balance General
     */
    public Map<String, Object> getBalanceGeneral(LocalDate fecha) {
        Map<String, Object> balance = new HashMap<>();
        
        // ACTIVO
        List<Map<String, Object>> activos = new ArrayList<>();
        BigDecimal totalActivos = BigDecimal.ZERO;
        
        BigDecimal cajaBancos = paymentRepository.getTotalPaymentsAmount();
        if (cajaBancos == null) cajaBancos = BigDecimal.ZERO;
        
        Map<String, Object> caja = new HashMap<>();
        caja.put("cuenta", "Caja y Bancos");
        caja.put("saldo", cajaBancos);
        activos.add(caja);
        totalActivos = totalActivos.add(cajaBancos);
        
        // Cuentas por Cobrar
        List<Invoice> facturasPendientes = invoiceRepository.findByStatus("PENDING");
        List<Invoice> facturasParciales = invoiceRepository.findByStatus("PARTIALLY_PAID");
        
        BigDecimal cuentasPorCobrar = BigDecimal.ZERO;
        for (Invoice inv : facturasPendientes) {
            cuentasPorCobrar = cuentasPorCobrar.add(inv.getTotal());
        }
        for (Invoice inv : facturasParciales) {
            BigDecimal pagado = paymentRepository.getTotalPaidByInvoiceId(inv.getId());
            if (pagado == null) pagado = BigDecimal.ZERO;
            cuentasPorCobrar = cuentasPorCobrar.add(inv.getTotal().subtract(pagado));
        }
        
        Map<String, Object> cuentasCobrarMap = new HashMap<>();
        cuentasCobrarMap.put("cuenta", "Cuentas por Cobrar");
        cuentasCobrarMap.put("saldo", cuentasPorCobrar);
        activos.add(cuentasCobrarMap);
        totalActivos = totalActivos.add(cuentasPorCobrar);
        
        // Inventario
        List<Vehicle> vehiculosInventario = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
        BigDecimal valorInventario = vehiculosInventario.stream()
                .filter(v -> v.getPurchasePrice() != null)
                .map(Vehicle::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> inventario = new HashMap<>();
        inventario.put("cuenta", "Inventario de Vehículos");
        inventario.put("saldo", valorInventario);
        activos.add(inventario);
        totalActivos = totalActivos.add(valorInventario);
        
        // Activo Fijo
        List<Vehicle> vehiculosVendidos = vehicleRepository.findByStatus(VehicleStatus.SOLD);
        BigDecimal activoFijo = vehiculosVendidos.stream()
                .filter(v -> v.getPurchasePrice() != null)
                .map(Vehicle::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> activoFijoMap = new HashMap<>();
        activoFijoMap.put("cuenta", "Activo Fijo");
        activoFijoMap.put("saldo", activoFijo);
        activos.add(activoFijoMap);
        totalActivos = totalActivos.add(activoFijo);
        
        Map<String, Object> activo = new HashMap<>();
        activo.put("cuentas", activos);
        activo.put("total", totalActivos);
        balance.put("activo", activo);
        
        // PASIVO
        List<Map<String, Object>> pasivos = new ArrayList<>();
        BigDecimal totalPasivos = BigDecimal.ZERO;
        
        BigDecimal comisionesPorPagar = commissionRepository.getTotalPendingAll();
        if (comisionesPorPagar == null) comisionesPorPagar = BigDecimal.ZERO;
        
        Map<String, Object> comisiones = new HashMap<>();
        comisiones.put("cuenta", "Comisiones por Pagar");
        comisiones.put("saldo", comisionesPorPagar);
        pasivos.add(comisiones);
        totalPasivos = totalPasivos.add(comisionesPorPagar);
        
        // ITBIS por Pagar
        BigDecimal totalVentas = invoiceRepository.getTotalRevenue();
        BigDecimal itbisPorPagar = totalVentas.multiply(new BigDecimal("0.18"));
        
        Map<String, Object> itbis = new HashMap<>();
        itbis.put("cuenta", "ITBIS por Pagar");
        itbis.put("saldo", itbisPorPagar);
        pasivos.add(itbis);
        totalPasivos = totalPasivos.add(itbisPorPagar);
        
        Map<String, Object> pasivo = new HashMap<>();
        pasivo.put("cuentas", pasivos);
        pasivo.put("total", totalPasivos);
        balance.put("pasivo", pasivo);
        
        // PATRIMONIO
        List<Map<String, Object>> patrimonios = new ArrayList<>();
        BigDecimal totalPatrimonio = BigDecimal.ZERO;
        
        BigDecimal capitalSocial = totalActivos.subtract(totalPasivos);
        if (capitalSocial.compareTo(BigDecimal.ZERO) < 0) capitalSocial = BigDecimal.ZERO;
        
        Map<String, Object> capital = new HashMap<>();
        capital.put("cuenta", "Capital Social");
        capital.put("saldo", capitalSocial);
        patrimonios.add(capital);
        totalPatrimonio = totalPatrimonio.add(capitalSocial);
        
        Map<String, Object> patrimonio = new HashMap<>();
        patrimonio.put("cuentas", patrimonios);
        patrimonio.put("total", totalPatrimonio);
        balance.put("patrimonio", patrimonio);
        
        BigDecimal totalPasivoPatrimonio = totalPasivos.add(totalPatrimonio);
        balance.put("totalPasivoPatrimonio", totalPasivoPatrimonio);
        balance.put("diferencia", totalActivos.subtract(totalPasivoPatrimonio).abs());
        balance.put("estaCuadrado", totalActivos.compareTo(totalPasivoPatrimonio) == 0);
        
        return balance;
    }

    /**
     * Estado de Resultados
     */
    public Map<String, Object> getEstadoResultados(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> resultados = new HashMap<>();
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        // INGRESOS
        List<Invoice> facturasPagadas = invoiceRepository.findPaidInvoicesBetween(inicio, fin);
        BigDecimal ventasVehiculos = facturasPagadas.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // COSTOS
        BigDecimal costoVehiculos = facturasPagadas.stream()
                .filter(f -> f.getVehicle() != null && f.getVehicle().getPurchasePrice() != null)
                .map(f -> f.getVehicle().getPurchasePrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // GASTOS
        BigDecimal comisionesPeriodo = commissionRepository.getTotalPaidAll();
        if (comisionesPeriodo == null) comisionesPeriodo = BigDecimal.ZERO;
        
        BigDecimal utilidadBruta = ventasVehiculos.subtract(costoVehiculos);
        BigDecimal utilidadOperativa = utilidadBruta.subtract(comisionesPeriodo);
        
        Map<String, Object> ingresos = new HashMap<>();
        ingresos.put("total", ventasVehiculos);
        resultados.put("ingresos", ingresos);
        
        Map<String, Object> costos = new HashMap<>();
        costos.put("total", costoVehiculos);
        resultados.put("costos", costos);
        
        Map<String, Object> gastos = new HashMap<>();
        gastos.put("total", comisionesPeriodo);
        resultados.put("gastos", gastos);
        
        resultados.put("utilidadBruta", utilidadBruta);
        resultados.put("utilidadOperativa", utilidadOperativa);
        resultados.put("utilidadNeta", utilidadOperativa);
        
        return resultados;
    }

    /**
     * Libro Mayor
     */
    public List<Map<String, Object>> getLibroMayor(String cuentaCodigo, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Map<String, Object>> movimientos = new ArrayList<>();
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        BigDecimal saldoAcumulado = BigDecimal.ZERO;
        
        // Movimientos de Ventas
        List<Invoice> facturasPeriodo = invoiceRepository.findPaidInvoicesBetween(inicio, fin);
        
        for (Invoice factura : facturasPeriodo) {
            Map<String, Object> movimiento = new HashMap<>();
            movimiento.put("fecha", factura.getInvoiceDateTime().toLocalDate().toString());
            movimiento.put("numeroAsiento", "FACT-" + factura.getEnNcf());
            movimiento.put("descripcion", "Venta - " + factura.getCustomerName());
            movimiento.put("debe", factura.getTotal());
            movimiento.put("haber", BigDecimal.ZERO);
            saldoAcumulado = saldoAcumulado.add(factura.getTotal());
            movimiento.put("saldo", saldoAcumulado);
            
            if (cuentaCodigo == null || cuentaCodigo.isEmpty() || 
                cuentaCodigo.contains("VENTA") || cuentaCodigo.contains("INGRESO") ||
                cuentaCodigo.equals("all")) {
                movimientos.add(movimiento);
            }
        }
        
        // Movimientos de Comisiones
        List<Commission> comisionesPeriodo = commissionRepository.findByStatusAndPaymentDateBetween("PAID", inicio, fin);
        
        for (Commission comision : comisionesPeriodo) {
            Map<String, Object> movimiento = new HashMap<>();
            movimiento.put("fecha", comision.getPaymentDate() != null ? 
                           comision.getPaymentDate().toLocalDate().toString() : fechaFin.toString());
            movimiento.put("numeroAsiento", "COM-" + comision.getId());
            
            Optional<SalesRep> rep = salesRepRepository.findById(comision.getSalesRepId());
            String nombreVendedor = rep.map(s -> s.getFirstName() + " " + s.getLastName()).orElse("Vendedor");
            movimiento.put("descripcion", "Comisión pagada a " + nombreVendedor);
            
            movimiento.put("debe", BigDecimal.ZERO);
            movimiento.put("haber", comision.getCommissionAmount());
            saldoAcumulado = saldoAcumulado.subtract(comision.getCommissionAmount());
            movimiento.put("saldo", saldoAcumulado);
            
            if (cuentaCodigo == null || cuentaCodigo.isEmpty() || 
                cuentaCodigo.contains("COMISION") || cuentaCodigo.contains("GASTO") ||
                cuentaCodigo.equals("all")) {
                movimientos.add(movimiento);
            }
        }
        
        // Ordenar por fecha
        movimientos.sort(Comparator.comparing(m -> (String) m.get("fecha")));
        
        return movimientos;
    }

    /**
     * Dashboard Stats
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        BigDecimal totalVentas = invoiceRepository.getTotalRevenue();
        stats.put("totalVentas", totalVentas);
        
        long vehiculosVendidos = vehicleRepository.countSoldVehicles();
        stats.put("vehiculosVendidos", vehiculosVendidos);
        
        BigDecimal comisionesPendientes = commissionRepository.getTotalPendingAll();
        BigDecimal comisionesPagadas = commissionRepository.getTotalPaidAll();
        stats.put("comisionesPendientes", comisionesPendientes != null ? comisionesPendientes : BigDecimal.ZERO);
        stats.put("comisionesPagadas", comisionesPagadas != null ? comisionesPagadas : BigDecimal.ZERO);
        
        BigDecimal valorInventario = vehicleRepository.getTotalInventoryValue();
        long vehiculosInventario = vehicleRepository.countAvailableVehicles();
        stats.put("valorInventario", valorInventario);
        stats.put("vehiculosInventario", vehiculosInventario);
        
        BigDecimal totalActivos = valorInventario.add(BigDecimal.ZERO);
        BigDecimal totalPasivos = comisionesPendientes;
        BigDecimal patrimonioNeto = totalActivos.subtract(totalPasivos);
        
        stats.put("totalActivos", totalActivos);
        stats.put("totalPasivos", totalPasivos);
        stats.put("patrimonioNeto", patrimonioNeto);
        
        // Movimientos mensuales
        List<Map<String, Object>> movimientos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDateTime startOfMonth = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            
            List<Invoice> facturasMes = invoiceRepository.findPaidInvoicesBetween(startOfMonth, endOfMonth);
            BigDecimal ventasMes = facturasMes.stream()
                    .map(Invoice::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> mes = new HashMap<>();
            mes.put("month", getNombreMes(startOfMonth.getMonthValue()));
            mes.put("ventas", ventasMes);
            mes.put("debe", ventasMes);
            mes.put("haber", BigDecimal.ZERO);
            movimientos.add(mes);
        }
        
        stats.put("movimientosMensuales", movimientos);
        
        return stats;
    }

    private String getNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }
}