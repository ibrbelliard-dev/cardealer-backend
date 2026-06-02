// src/main/java/com/cardealer/iotproject/service/DashboardEjecutivoService.java
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardEjecutivoService {

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
    private ClientRepository clientRepository;

    /**
     * Obtiene todos los indicadores para el Dashboard Ejecutivo
     */
    public Map<String, Object> getDashboardEjecutivo() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 1. KPIs Principales del Mes Actual
        dashboard.put("kpisMensuales", getKpisMensuales());
        
        // 2. KPIs del Año
        dashboard.put("kpisAnuales", getKpisAnuales());
        
        // 3. Tendencia de Ventas (últimos 12 meses)
        dashboard.put("tendenciaVentas", getTendenciaVentas());
        
        // 4. Top 5 Vehículos Más Vendidos
        dashboard.put("topVehiculosVendidos", getTopVehiculosVendidos(5));
        
        // 5. Top 5 Vendedores
        dashboard.put("topVendedores", getTopVendedores(5));
        
        // 6. Top 5 Clientes
        dashboard.put("topClientes", getTopClientes(5));
        
        // 7. Distribución de Ventas por Marca
        dashboard.put("ventasPorMarca", getVentasPorMarca());
        
        // 8. Mapa de Calor de Ventas por Mes
        dashboard.put("mapaCalorVentas", getMapaCalorVentas());
        
        // 9. Proyección de Ventas
        dashboard.put("proyeccionVentas", getProyeccionVentas());
        
        // 10. Indicadores de Rentabilidad
        dashboard.put("rentabilidad", getIndicadoresRentabilidad());
        
        // 11. Salud del Negocio
        dashboard.put("saludNegocio", getSaludNegocio());
        
        // 12. Alertas y Notificaciones
        dashboard.put("alertas", getAlertas());
        
        return dashboard;
    }
    
    /**
     * KPIs del mes actual
     */
    private Map<String, Object> getKpisMensuales() {
        Map<String, Object> kpis = new HashMap<>();
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();
        
        // Ventas del mes
        List<Invoice> facturasMes = invoiceRepository.findPaidInvoicesBetween(startOfMonth, now);
        BigDecimal ventasMes = facturasMes.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Cantidad de vehículos vendidos
        long vehiculosVendidos = facturasMes.stream()
                .filter(f -> f.getVehicle() != null)
                .count();
        
        // Ticket promedio
        BigDecimal ticketPromedio = vehiculosVendidos > 0 ? 
            ventasMes.divide(BigDecimal.valueOf(vehiculosVendidos), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // Comisiones del mes
        BigDecimal comisionesMes = commissionRepository.getTotalBetweenDates(startOfMonth, now);
        
        // Nuevos clientes del mes
        long nuevosClientes = clientRepository.count();
        
        // Servicios realizados
        long serviciosMes = serviceRequestRepository.findByServiceDateBetween(startOfMonth, now).size();
        
        kpis.put("ventas", ventasMes);
        kpis.put("vehiculosVendidos", vehiculosVendidos);
        kpis.put("ticketPromedio", ticketPromedio);
        kpis.put("comisiones", comisionesMes != null ? comisionesMes : BigDecimal.ZERO);
        kpis.put("nuevosClientes", nuevosClientes);
        kpis.put("servicios", serviciosMes);
        
        // Comparativa con mes anterior
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusDays(1);
        List<Invoice> facturasMesAnterior = invoiceRepository.findPaidInvoicesBetween(startOfLastMonth, endOfLastMonth);
        BigDecimal ventasMesAnterior = facturasMesAnterior.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        double crecimiento = 0;
        if (ventasMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            crecimiento = ventasMes.subtract(ventasMesAnterior)
                .divide(ventasMesAnterior, 4, RoundingMode.HALF_UP)
                .doubleValue() * 100;
        }
        
        kpis.put("crecimientoMensual", Math.round(crecimiento * 100) / 100.0);
        
        return kpis;
    }
    
    /**
     * KPIs del año
     */
    private Map<String, Object> getKpisAnuales() {
        Map<String, Object> kpis = new HashMap<>();
        
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();
        
        // Ventas del año
        List<Invoice> facturasAnio = invoiceRepository.findPaidInvoicesBetween(startOfYear, now);
        BigDecimal ventasAnio = facturasAnio.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Vehículos vendidos en el año
        long vehiculosVendidos = facturasAnio.stream()
                .filter(f -> f.getVehicle() != null)
                .count();
        
        // Meta anual (ejemplo: 100 vehículos)
        long metaAnual = 100;
        double cumplimientoMeta = (double) vehiculosVendidos / metaAnual * 100;
        
        kpis.put("ventas", ventasAnio);
        kpis.put("vehiculosVendidos", vehiculosVendidos);
        kpis.put("metaAnual", metaAnual);
        kpis.put("cumplimientoMeta", Math.round(cumplimientoMeta * 100) / 100.0);
        
        return kpis;
    }
    
    /**
     * Tendencia de ventas últimos 12 meses
     */
    private List<Map<String, Object>> getTendenciaVentas() {
        List<Map<String, Object>> tendencia = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            LocalDateTime inicio = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime fin = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            
            List<Invoice> facturasMes = invoiceRepository.findPaidInvoicesBetween(inicio, fin);
            BigDecimal ventasMes = facturasMes.stream()
                    .map(Invoice::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            long vehiculosMes = facturasMes.stream()
                    .filter(f -> f.getVehicle() != null)
                    .count();
            
            Map<String, Object> mes = new HashMap<>();
            mes.put("mes", getNombreMes(yearMonth.getMonthValue()));
            mes.put("anio", yearMonth.getYear());
            mes.put("ventas", ventasMes);
            mes.put("vehiculos", vehiculosMes);
            mes.put("periodo", yearMonth.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            
            tendencia.add(mes);
        }
        
        return tendencia;
    }
    
    /**
 * Top N vehículos más vendidos
 */
private List<Map<String, Object>> getTopVehiculosVendidos(int limit) {
    List<Invoice> facturasPagadas = invoiceRepository.findByStatusIn(List.of("PAID", "COMPLETED", "CONFIRMED"));
    
    Map<String, BigDecimal> ventasPorVehiculo = new HashMap<>();
    
    for (Invoice factura : facturasPagadas) {
        if (factura.getVehicle() != null) {
            String key = factura.getVehicle().getMake().getMakeName() + " " + 
                         factura.getVehicle().getModel().getModelName() + " (" + 
                         factura.getVehicle().getModelYear() + ")";
            ventasPorVehiculo.put(key, ventasPorVehiculo.getOrDefault(key, BigDecimal.ZERO).add(factura.getTotal()));
        }
    }
    
    // CORREGIDO: Usar Map.Entry<String, BigDecimal> correctamente
    return ventasPorVehiculo.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))  // Orden descendente
            .limit(limit)
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("vehiculo", entry.getKey());
                item.put("ventas", entry.getValue());
                return item;
            })
            .collect(Collectors.toList());
}
    /**
 * Top N vendedores
 */
private List<Map<String, Object>> getTopVendedores(int limit) {
    List<SalesRep> vendedores = salesRepRepository.findByStatus(1);
    List<Invoice> facturasPagadas = invoiceRepository.findByStatusIn(List.of("PAID", "COMPLETED", "CONFIRMED"));
    
    Map<Long, BigDecimal> ventasPorVendedor = new HashMap<>();
    
    for (SalesRep vendedor : vendedores) {
        BigDecimal ventas = facturasPagadas.stream()
                .filter(f -> f.getSalesRep() != null && f.getSalesRep().getId().equals(vendedor.getId()))
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ventasPorVendedor.put(vendedor.getId(), ventas);
    }
    
    // CORREGIDO
    return ventasPorVendedor.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(limit)
            .map(entry -> {
                SalesRep rep = salesRepRepository.findById(entry.getKey()).orElse(null);
                Map<String, Object> item = new HashMap<>();
                item.put("nombre", rep != null ? rep.getFullName() : "Desconocido");
                item.put("ventas", entry.getValue());
                item.put("comisiones", entry.getValue()
                    .multiply(rep != null ? rep.getCommissionPercentage() : BigDecimal.ZERO)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                return item;
            })
            .collect(Collectors.toList());
}

/**
 * Top N clientes
 */
private List<Map<String, Object>> getTopClientes(int limit) {
    List<Invoice> facturasPagadas = invoiceRepository.findByStatusIn(List.of("PAID", "COMPLETED", "CONFIRMED"));
    
    Map<String, BigDecimal> comprasPorCliente = new HashMap<>();
    
    for (Invoice factura : facturasPagadas) {
        String cliente = factura.getCustomerName();
        comprasPorCliente.put(cliente, comprasPorCliente.getOrDefault(cliente, BigDecimal.ZERO).add(factura.getTotal()));
    }
    
    // CORREGIDO
    return comprasPorCliente.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(limit)
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("cliente", entry.getKey());
                item.put("compras", entry.getValue());
                return item;
            })
            .collect(Collectors.toList());
}
    
    /**
     * Distribución de ventas por marca
     */
    private List<Map<String, Object>> getVentasPorMarca() {
        List<Invoice> facturasPagadas = invoiceRepository.findByStatusIn(List.of("PAID", "COMPLETED", "CONFIRMED"));
        
        Map<String, BigDecimal> ventasPorMarca = new HashMap<>();
        
        for (Invoice factura : facturasPagadas) {
            if (factura.getVehicle() != null) {
                String marca = factura.getVehicle().getMake().getMakeName();
                ventasPorMarca.put(marca, ventasPorMarca.getOrDefault(marca, BigDecimal.ZERO).add(factura.getTotal()));
            }
        }
        
        return ventasPorMarca.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("marca", entry.getKey());
                    item.put("ventas", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Mapa de calor de ventas (mes vs marca)
     */
    private List<Map<String, Object>> getMapaCalorVentas() {
        List<Map<String, Object>> mapa = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        List<String> meses = Arrays.asList("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic");
        
        for (String mes : meses) {
            Map<String, Object> punto = new HashMap<>();
            punto.put("mes", mes);
            punto.put("ventas", 0);
            punto.put("intensidad", 0);
            mapa.add(punto);
        }
        
        return mapa;
    }
    
    /**
     * Proyección de ventas
     */
    private Map<String, Object> getProyeccionVentas() {
        Map<String, Object> proyeccion = new HashMap<>();
        
        List<Invoice> facturasUltimos3Meses = invoiceRepository.findPaidInvoicesBetween(
            LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        
        BigDecimal ventasUltimoMes = getKpisMensuales().containsKey("ventas") ? 
            (BigDecimal) getKpisMensuales().get("ventas") : BigDecimal.ZERO;
        
        // Crecimiento promedio mensual
        Map<YearMonth, BigDecimal> ventasPorMes = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            YearMonth ym = YearMonth.from(LocalDateTime.now().minusMonths(i));
            // Calcular ventas del mes...
        }
        
        BigDecimal crecimientoEstimado = new BigDecimal("0.05"); // 5% mensual
        BigDecimal ventasProyectadas = ventasUltimoMes.multiply(BigDecimal.ONE.add(crecimientoEstimado));
        
        proyeccion.put("ventasProyectadasMesSiguiente", ventasProyectadas);
        proyeccion.put("crecimientoEstimado", crecimientoEstimado.multiply(new BigDecimal("100")));
        proyeccion.put("metaTrimestre", ventasUltimoMes.multiply(new BigDecimal("3")).multiply(BigDecimal.ONE.add(crecimientoEstimado)));
        
        return proyeccion;
    }
    
    /**
     * Indicadores de rentabilidad
     */
    private Map<String, Object> getIndicadoresRentabilidad() {
        Map<String, Object> rentabilidad = new HashMap<>();
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();
        
        List<Invoice> facturasMes = invoiceRepository.findPaidInvoicesBetween(startOfMonth, now);
        BigDecimal ventasMes = facturasMes.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal costoVehiculos = facturasMes.stream()
                .filter(f -> f.getVehicle() != null && f.getVehicle().getPurchasePrice() != null)
                .map(f -> f.getVehicle().getPurchasePrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal comisionesMes = commissionRepository.getTotalBetweenDates(startOfMonth, now);
        if (comisionesMes == null) comisionesMes = BigDecimal.ZERO;
        
        BigDecimal utilidadBruta = ventasMes.subtract(costoVehiculos);
        BigDecimal utilidadOperativa = utilidadBruta.subtract(comisionesMes);
        
        double margenBruto = ventasMes.compareTo(BigDecimal.ZERO) > 0 ?
            utilidadBruta.divide(ventasMes, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;
        double margenOperativo = ventasMes.compareTo(BigDecimal.ZERO) > 0 ?
            utilidadOperativa.divide(ventasMes, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;
        
        rentabilidad.put("utilidadBruta", utilidadBruta);
        rentabilidad.put("utilidadOperativa", utilidadOperativa);
        rentabilidad.put("margenBruto", Math.round(margenBruto * 100) / 100.0);
        rentabilidad.put("margenOperativo", Math.round(margenOperativo * 100) / 100.0);
        rentabilidad.put("roi", calcularROI());
        
        return rentabilidad;
    }
    
    /**
     * Calcula el ROI (Return on Investment)
     */
    private double calcularROI() {
        BigDecimal inversionTotal = vehicleRepository.getTotalInventoryValue();
        if (inversionTotal.compareTo(BigDecimal.ZERO) == 0) return 0;
        
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0);
        List<Invoice> facturasAnio = invoiceRepository.findPaidInvoicesBetween(startOfYear, LocalDateTime.now());
        BigDecimal ventasAnio = facturasAnio.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return ventasAnio.divide(inversionTotal, 4, RoundingMode.HALF_UP).doubleValue() * 100;
    }
    
    /**
     * Indicadores de salud del negocio
     */
    private Map<String, Object> getSaludNegocio() {
        Map<String, Object> salud = new HashMap<>();
        
        long totalVehiculos = vehicleRepository.count();
        long vehiculosVendidos = vehicleRepository.countSoldVehicles();
        long vehiculosInventario = vehicleRepository.countAvailableVehicles();
        
        double rotacionInventario = totalVehiculos > 0 ? 
            (double) vehiculosVendidos / totalVehiculos * 100 : 0;
        
        // Días de inventario (estimado)
        double diasInventario = 365;
        if (vehiculosVendidos > 0) {
            diasInventario = (double) vehiculosInventario / vehiculosVendidos * 365;
        }
        
        // Morosidad (cuentas por cobrar antiguas)
        List<Invoice> facturasPendientes = invoiceRepository.findByStatus("PENDING");
        BigDecimal montoMoroso = facturasPendientes.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        double indiceMorosidad = 0;
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1);
        List<Invoice> facturasAnio = invoiceRepository.findPaidInvoicesBetween(startOfYear, LocalDateTime.now());
        BigDecimal ventasAnio = facturasAnio.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (ventasAnio.compareTo(BigDecimal.ZERO) > 0) {
            indiceMorosidad = montoMoroso.divide(ventasAnio, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        }
        
        salud.put("rotacionInventario", Math.round(rotacionInventario * 100) / 100.0);
        salud.put("diasInventario", Math.round(diasInventario));
        salud.put("indiceMorosidad", Math.round(indiceMorosidad * 100) / 100.0);
        salud.put("montoMoroso", montoMoroso);
        
        // Score de salud (0-100)
        double score = 100;
        if (rotacionInventario < 50) score -= 20;
        if (diasInventario > 180) score -= 20;
        if (indiceMorosidad > 10) score -= 20;
        
        salud.put("score", Math.max(0, Math.min(100, Math.round(score))));
        
        String nivel;
        if (score >= 80) nivel = "EXCELENTE";
        else if (score >= 60) nivel = "BUENO";
        else if (score >= 40) nivel = "REGULAR";
        else nivel = "CRÍTICO";
        
        salud.put("nivel", nivel);
        
        return salud;
    }
    
    /**
     * Alertas y notificaciones
     */
    private List<Map<String, Object>> getAlertas() {
        List<Map<String, Object>> alertas = new ArrayList<>();
        
        // 1. Inventario bajo
        long vehiculosInventario = vehicleRepository.countAvailableVehicles();
        if (vehiculosInventario < 10) {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "WARNING");
            alerta.put("mensaje", "Inventario bajo: Solo " + vehiculosInventario + " vehículos disponibles");
            alerta.put("prioridad", "ALTA");
            alertas.add(alerta);
        }
        
        // 2. Comisiones pendientes
        BigDecimal comisionesPendientes = commissionRepository.getTotalPendingAll();
        if (comisionesPendientes != null && comisionesPendientes.compareTo(BigDecimal.ZERO) > 0) {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "INFO");
            alerta.put("mensaje", "Comisiones pendientes de pago: RD$ " + comisionesPendientes);
            alerta.put("prioridad", "MEDIA");
            alertas.add(alerta);
        }
        
        // 3. Facturas vencidas
        List<Invoice> facturasPendientes = invoiceRepository.findByStatus("PENDING");
        long facturasVencidas = facturasPendientes.stream()
                .filter(f -> f.getInvoiceDateTime().toLocalDate().isBefore(LocalDate.now().minusDays(30)))
                .count();
        
        if (facturasVencidas > 0) {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "WARNING");
            alerta.put("mensaje", facturasVencidas + " facturas vencidas más de 30 días");
            alerta.put("prioridad", "ALTA");
            alertas.add(alerta);
        }
        
        // 4. Vehículos en servicio por más de 15 días
        List<ServiceRequest> serviciosLargos = serviceRequestRepository.findByStatus("IN_PROGRESS");
        long serviciosAntiguos = serviciosLargos.stream()
                .filter(s -> s.getServiceDate() != null && 
                            s.getServiceDate().toLocalDate().isBefore(LocalDate.now().minusDays(15)))
                .count();
        
        if (serviciosAntiguos > 0) {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "INFO");
            alerta.put("mensaje", serviciosAntiguos + " vehículos en servicio por más de 15 días");
            alerta.put("prioridad", "MEDIA");
            alertas.add(alerta);
        }
        
        // 5. Próximos vencimientos DGII (día 20 de cada mes)
        int diaActual = LocalDate.now().getDayOfMonth();
        if (diaActual >= 18 && diaActual <= 20) {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "URGENTE");
            alerta.put("mensaje", "Vence declaración IT-1 el día 20 de este mes");
            alerta.put("prioridad", "ALTA");
            alertas.add(alerta);
        }
        
        return alertas;
    }
    
    private String getNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }
}