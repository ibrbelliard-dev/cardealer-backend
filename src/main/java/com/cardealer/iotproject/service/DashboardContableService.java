package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.*;
import com.cardealer.iotproject.model.enums.VehicleStatus;
import com.cardealer.iotproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardContableService {

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
    
    @Autowired
    private CuentaMaestraRepository cuentaMaestraRepository;
    
    @Autowired
    private SubcuentaRepository subcuentaRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // ========== 1. TOTAL DE CUENTAS CONTABLES ==========
        long totalCuentasMaestras = cuentaMaestraRepository.count();
        long totalSubcuentas = subcuentaRepository.count();
        stats.put("totalCuentas", totalCuentasMaestras + totalSubcuentas);
        stats.put("cuentasActivas", cuentaMaestraRepository.countByActivoTrue() + subcuentaRepository.countByActivoTrue());
        
        // ========== 2. VENTAS REALES ==========
        BigDecimal totalVentas = invoiceRepository.getTotalRevenue();
        if (totalVentas == null) totalVentas = BigDecimal.ZERO;
        stats.put("totalVentas", totalVentas);
        
        long vehiculosVendidos = vehicleRepository.countSoldVehicles();
        stats.put("vehiculosVendidos", vehiculosVendidos);
        
        // ========== 3. COMISIONES REALES ==========
        BigDecimal comisionesPendientes = commissionRepository.getTotalPendingAll();
        BigDecimal comisionesPagadas = commissionRepository.getTotalPaidAll();
        stats.put("comisionesPendientes", comisionesPendientes != null ? comisionesPendientes : BigDecimal.ZERO);
        stats.put("comisionesPagadas", comisionesPagadas != null ? comisionesPagadas : BigDecimal.ZERO);
        
        // ========== 4. INVENTARIO REAL ==========
        BigDecimal valorInventario = vehicleRepository.getTotalInventoryValue();
        long vehiculosInventario = vehicleRepository.countAvailableVehicles();
        stats.put("valorInventario", valorInventario != null ? valorInventario : BigDecimal.ZERO);
        stats.put("vehiculosInventario", vehiculosInventario);
        
        // ========== 5. INGRESOS POR SERVICIOS ==========
        List<ServiceRequest> serviciosCompletados = serviceRequestRepository.findByStatus("COMPLETED");
        BigDecimal ingresosServicios = serviciosCompletados.stream()
                .filter(s -> s.getActualCost() != null)
                .map(ServiceRequest::getActualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("ingresosServicios", ingresosServicios);
        
        // ========== 6. COSTO DE MANTENIMIENTO ==========
        stats.put("costoMantenimiento", ingresosServicios); // El costo es igual al ingreso para servicios
        
        // ========== 7. CAJA Y BANCOS (Pagos recibidos) ==========
        BigDecimal cajaBancos = paymentRepository.getTotalPaymentsAmount();
        if (cajaBancos == null) cajaBancos = BigDecimal.ZERO;
        stats.put("cajaBancos", cajaBancos);
        
        // ========== 8. CUENTAS POR COBRAR (Facturas pendientes) ==========
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
        stats.put("cuentasPorCobrar", cuentasPorCobrar);
        
        // ========== 9. ITBIS POR PAGAR ==========
        BigDecimal itbisPorPagar = totalVentas.multiply(new BigDecimal("0.18"));
        stats.put("itbisPorPagar", itbisPorPagar);
        
        // ========== 10. TOTALES FINANCIEROS ==========
        BigDecimal totalActivos = valorInventario.add(cajaBancos).add(cuentasPorCobrar);
        BigDecimal totalPasivos = comisionesPendientes.add(itbisPorPagar);
        BigDecimal patrimonioNeto = totalActivos.subtract(totalPasivos);
        
        stats.put("totalActivos", totalActivos);
        stats.put("totalPasivos", totalPasivos);
        stats.put("patrimonioNeto", patrimonioNeto);
        
        // ========== 11. DISTRIBUCIÓN POR TIPO DE CUENTA ==========
        List<Map<String, Object>> distribucionPorTipo = new ArrayList<>();
        distribucionPorTipo.add(crearDistribucion("ACTIVO", totalActivos.doubleValue(), "#1976d2"));
        distribucionPorTipo.add(crearDistribucion("PASIVO", totalPasivos.doubleValue(), "#ed6c02"));
        distribucionPorTipo.add(crearDistribucion("PATRIMONIO", patrimonioNeto.doubleValue(), "#2e7d32"));
        stats.put("distribucionPorTipo", distribucionPorTipo);
        
        // ========== 12. MOVIMIENTOS MENSUALES ==========
        stats.put("movimientosMensuales", getMovimientosMensuales());
        
        // ========== 13. VENTAS POR VENDEDOR ==========
        stats.put("ventasPorVendedor", getVentasPorVendedor());
        
        // ========== 14. MARCAS MÁS VENDIDAS ==========
        stats.put("marcasMasVendidas", getMarcasMasVendidas());
        
        return stats;
    }
    
    private List<Map<String, Object>> getMovimientosMensuales() {
        List<Map<String, Object>> movimientos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 5; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            LocalDateTime inicio = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime fin = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            
            List<Invoice> facturasMes = invoiceRepository.findPaidInvoicesBetween(inicio, fin);
            BigDecimal ventasMes = facturasMes.stream()
                    .map(Invoice::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> mes = new HashMap<>();
            mes.put("month", getNombreMes(yearMonth.getMonthValue()));
            mes.put("ventas", ventasMes);
            mes.put("debe", ventasMes);
            mes.put("haber", BigDecimal.ZERO);
            movimientos.add(mes);
        }
        
        return movimientos;
    }
    
    private List<Map<String, Object>> getVentasPorVendedor() {
        List<Map<String, Object>> ventasVendedores = new ArrayList<>();
        List<SalesRep> vendedores = salesRepRepository.findByStatus(1);
        List<Invoice> facturasPagadas = invoiceRepository.findByStatusIn(List.of("PAID", "COMPLETED", "CONFIRMED"));
        
        for (SalesRep vendedor : vendedores) {
            BigDecimal ventas = facturasPagadas.stream()
                    .filter(f -> f.getSalesRep() != null && f.getSalesRep().getId().equals(vendedor.getId()))
                    .map(Invoice::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (ventas.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("nombre", vendedor.getFullName());
                item.put("ventas", ventas);
                item.put("comision", ventas.multiply(vendedor.getCommissionPercentage()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                ventasVendedores.add(item);
            }
        }
        
        return ventasVendedores;
    }
    
    private List<Map<String, Object>> getMarcasMasVendidas() {
        List<Map<String, Object>> marcas = new ArrayList<>();
        List<Invoice> facturasPagadas = invoiceRepository.findByStatusIn(List.of("PAID", "COMPLETED", "CONFIRMED"));
        
        Map<String, BigDecimal> ventasPorMarca = new HashMap<>();
        
        for (Invoice factura : facturasPagadas) {
            if (factura.getVehicle() != null && factura.getVehicle().getMake() != null) {
                String nombreMarca = factura.getVehicle().getMake().getMakeName();
                ventasPorMarca.merge(nombreMarca, factura.getTotal(), BigDecimal::add);
            }
        }
        
        ventasPorMarca.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("marca", entry.getKey());
                    item.put("ventas", entry.getValue());
                    marcas.add(item);
                });
        
        return marcas;
    }
    
    private String getNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }
    
    private Map<String, Object> crearDistribucion(String name, double value, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", Math.max(value, 0));
        item.put("color", color);
        return item;
    }
}