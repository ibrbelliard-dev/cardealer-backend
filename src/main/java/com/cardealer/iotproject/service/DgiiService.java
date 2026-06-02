// src/main/java/com/cardealer/iotproject/service/DgiiService.java
package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.dto.DgiiArchivoDTO;
import com.cardealer.iotproject.model.entity.*;
import com.cardealer.iotproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DgiiService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private SalesRepRepository salesRepRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private DgiiArchivoRepository dgiiArchivoRepository;
    
    private static final String DGII_FOLDER = "./dgii_archivos/";
    
    /**
     * FORMATO 606 - Compras y gastos (NCF recibidos de proveedores)
     */
    public String generarFormato606(int year, int month) throws IOException {
        StringBuilder csv = new StringBuilder();
        
        csv.append("RNC_PROVEEDOR|TIPO_NCF|NCF|MONTO|ITBIS|FECHA|COMPROBANTE_TIPO|FECHA_REGISTRO\n");
        
        LocalDate inicio = LocalDate.of(year, month, 1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1);
        
        List<Vehicle> comprasMes = new ArrayList<>();
        try {
            comprasMes = vehicleRepository.findByPurchaseDateBetween(inicio, fin);
        } catch (Exception e) {
            // Si el método no existe, usar findAll y filtrar manualmente
            comprasMes = vehicleRepository.findAll().stream()
                    .filter(v -> v.getPurchaseDate() != null)
                    .filter(v -> !v.getPurchaseDate().isBefore(inicio) && !v.getPurchaseDate().isAfter(fin))
                    .toList();
        }
        
        for (Vehicle vehicle : comprasMes) {
            if (vehicle.getPurchasePrice() != null) {
                Double itbis = vehicle.getPurchasePrice().doubleValue() * 0.18;
                csv.append(String.format("101234567|01|C%010d|%.2f|%.2f|%s|COMPRA|%s\n",
                    vehicle.getVehicleId(),
                    vehicle.getPurchasePrice(),
                    itbis,
                    vehicle.getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
        }
        
        return csv.toString();
    }
    
    /**
     * FORMATO 607 - Ventas e ingresos (NCF emitidos a clientes)
     */
    public String generarFormato607(int year, int month) throws IOException {
        StringBuilder csv = new StringBuilder();
        
        csv.append("RNC_CLIENTE|TIPO_NCF|NCF|MONTO|ITBIS|FECHA|COMPROBANTE_TIPO|FECHA_REGISTRO\n");
        
        LocalDate inicio = LocalDate.of(year, month, 1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1);
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        
        List<Invoice> facturasMes = new ArrayList<>();
        try {
            facturasMes = invoiceRepository.findByInvoiceDateTimeBetween(inicioDateTime, finDateTime);
        } catch (Exception e) {
            // Si el método no existe, usar findAll y filtrar manualmente
            facturasMes = invoiceRepository.findAll().stream()
                    .filter(f -> f.getInvoiceDateTime() != null)
                    .filter(f -> !f.getInvoiceDateTime().isBefore(inicioDateTime) && 
                                 !f.getInvoiceDateTime().isAfter(finDateTime))
                    .toList();
        }
        
        for (Invoice invoice : facturasMes) {
            String rncCliente = invoice.getCustomerRnc() != null ? invoice.getCustomerRnc() : "000000000";
            String tipoNCF = invoice.getNcfType() != null ? invoice.getNcfType() : "01";
            String ncf = invoice.getEnNcf() != null ? invoice.getEnNcf() : "N/A";
            Double monto = invoice.getTotal() != null ? invoice.getTotal().doubleValue() : 0.0;
            Double itbis = invoice.getItbisAmount() != null ? invoice.getItbisAmount().doubleValue() : monto * 0.18;
            String fecha = invoice.getInvoiceDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            csv.append(String.format("%s|%s|%s|%.2f|%.2f|%s|VENTA|%s\n",
                rncCliente, tipoNCF, ncf, monto, itbis, fecha, 
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }
        
        return csv.toString();
    }
    
    /**
     * IR-17 - Retenciones de ISR e ITBIS
     */
    public String generarIr17(int year, int month) throws IOException {
        StringBuilder csv = new StringBuilder();
        
        csv.append("RNC_SUJ_RETENIDO|TIPO_RETENCION|MONTO_RETENIDO|PERIODO|TIPO_NCF|NCF|FECHA\n");
        
        LocalDate inicio = LocalDate.of(year, month, 1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1);
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        
        List<Invoice> facturasMes = new ArrayList<>();
        try {
            facturasMes = invoiceRepository.findByInvoiceDateTimeBetween(inicioDateTime, finDateTime);
        } catch (Exception e) {
            facturasMes = invoiceRepository.findAll().stream()
                    .filter(f -> f.getInvoiceDateTime() != null)
                    .filter(f -> !f.getInvoiceDateTime().isBefore(inicioDateTime) && 
                                 !f.getInvoiceDateTime().isAfter(finDateTime))
                    .toList();
        }
        
        for (Invoice invoice : facturasMes) {
            if (invoice.getItbisAmount() != null && invoice.getItbisAmount().doubleValue() > 0) {
                Double itbisRetenido = invoice.getItbisAmount().doubleValue() * 0.30;
                csv.append(String.format("%s|ITBIS|%.2f|%d-%02d|%s|%s|%s\n",
                    invoice.getCustomerRnc() != null ? invoice.getCustomerRnc() : "000000000",
                    itbisRetenido,
                    year, month,
                    invoice.getNcfType() != null ? invoice.getNcfType() : "01",
                    invoice.getEnNcf() != null ? invoice.getEnNcf() : "N/A",
                    invoice.getInvoiceDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
        }
        
        return csv.toString();
    }
    
    /**
     * IT-1 - Declaración mensual de ITBIS
     */
    public String generarIt1(int year, int month) {
        LocalDate inicio = LocalDate.of(year, month, 1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1);
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        
        List<Invoice> facturasMes = new ArrayList<>();
        try {
            facturasMes = invoiceRepository.findByInvoiceDateTimeBetween(inicioDateTime, finDateTime);
        } catch (Exception e) {
            facturasMes = invoiceRepository.findAll().stream()
                    .filter(f -> f.getInvoiceDateTime() != null)
                    .filter(f -> !f.getInvoiceDateTime().isBefore(inicioDateTime) && 
                                 !f.getInvoiceDateTime().isAfter(finDateTime))
                    .toList();
        }
        
        double totalDebitoFiscal = 0.0;
        double totalCreditoFiscal = 0.0;
        double totalVentas = 0.0;
        
        for (Invoice invoice : facturasMes) {
            if (invoice.getStatus() != null && 
                (invoice.getStatus().equals("PAID") || invoice.getStatus().equals("COMPLETED"))) {
                totalDebitoFiscal += invoice.getItbisAmount() != null ? invoice.getItbisAmount().doubleValue() : 0;
                totalVentas += invoice.getTotal() != null ? invoice.getTotal().doubleValue() : 0;
            }
        }
        
        // Calcular compras
        List<Vehicle> comprasMes = new ArrayList<>();
        try {
            comprasMes = vehicleRepository.findByPurchaseDateBetween(inicio, fin);
        } catch (Exception e) {
            comprasMes = vehicleRepository.findAll().stream()
                    .filter(v -> v.getPurchaseDate() != null)
                    .filter(v -> !v.getPurchaseDate().isBefore(inicio) && !v.getPurchaseDate().isAfter(fin))
                    .toList();
        }
        
        for (Vehicle vehicle : comprasMes) {
            if (vehicle.getPurchasePrice() != null) {
                totalCreditoFiscal += vehicle.getPurchasePrice().doubleValue() * 0.18;
            }
        }
        
        double itbisAPagar = totalDebitoFiscal - totalCreditoFiscal;
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== DECLARACIÓN IT-1 ===\n");
        reporte.append("Período: ").append(year).append("-").append(String.format("%02d", month)).append("\n");
        reporte.append("Total Ventas Gravadas: RD$").append(String.format("%,.2f", totalVentas)).append("\n");
        reporte.append("ITBIS Débito Fiscal: RD$").append(String.format("%,.2f", totalDebitoFiscal)).append("\n");
        reporte.append("Total Compras: RD$").append(String.format("%,.2f", totalCreditoFiscal / 0.18)).append("\n");
        reporte.append("ITBIS Crédito Fiscal: RD$").append(String.format("%,.2f", totalCreditoFiscal)).append("\n");
        reporte.append("ITBIS a Pagar: RD$").append(String.format("%,.2f", Math.max(itbisAPagar, 0))).append("\n");
        reporte.append("================================\n");
        
        return reporte.toString();
    }
    
    /**
     * Guarda el archivo y registra en BD
     */
    @Transactional
    public DgiiArchivo guardarArchivo(String tipoArchivo, String periodo, String contenido) throws IOException {
        Files.createDirectories(Paths.get(DGII_FOLDER));
        
        String filename = String.format("%s_%s_%d.txt", tipoArchivo, periodo, System.currentTimeMillis());
        Path filePath = Paths.get(DGII_FOLDER + filename);
        
        Files.writeString(filePath, contenido, StandardCharsets.UTF_8);
        
        DgiiArchivo archivo = new DgiiArchivo();
        archivo.setTipoArchivo(tipoArchivo);
        archivo.setPeriodo(periodo);
        archivo.setContenido(contenido);
        archivo.setRutaArchivo(filePath.toString());
        archivo.setGeneradoPor("SYSTEM");
        
        return dgiiArchivoRepository.save(archivo);
    }
    
    /**
     * Descarga un archivo generado previamente
     */
    public byte[] descargarArchivo(Long archivoId) throws IOException {
        DgiiArchivo archivo = dgiiArchivoRepository.findById(archivoId)
            .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
        
        Path filePath = Paths.get(archivo.getRutaArchivo());
        return Files.readAllBytes(filePath);
    }
    
    /**
     * Lista los archivos generados
     */
    public List<DgiiArchivo> listarArchivos(String tipoArchivo) {
        if (tipoArchivo != null && !tipoArchivo.isEmpty()) {
            return dgiiArchivoRepository.findByTipoArchivoOrderByPeriodoDesc(tipoArchivo);
        }
        return dgiiArchivoRepository.findAll();
    }

    
// Agregar este método al final de la clase
public List<DgiiArchivoDTO> listarArchivosDTO(String tipoArchivo) {
    List<DgiiArchivo> archivos;
    if (tipoArchivo != null && !tipoArchivo.isEmpty()) {
        archivos = dgiiArchivoRepository.findByTipoArchivoOrderByPeriodoDesc(tipoArchivo);
    } else {
        archivos = dgiiArchivoRepository.findAllByOrderByGeneradoEnDesc();
    }
    
    return archivos.stream()
        .map(a -> new DgiiArchivoDTO.DgiiArchivoDTOBuilder()
            .archivoId(a.getArchivoId())
            .tipoArchivo(a.getTipoArchivo())
            .periodo(a.getPeriodo())
            .generadoEn(a.getGeneradoEn())
            .generadoPor(a.getGeneradoPor())
            .descargadoEn(a.getDescargadoEn())
            .descargadoPor(a.getDescargadoPor())
            .build())
        .collect(java.util.stream.Collectors.toList());
}


/**
 * Elimina un archivo DGII (físico y registro en BD)
 */
@Transactional
public void eliminarArchivo(Long archivoId) throws IOException {
    DgiiArchivo archivo = dgiiArchivoRepository.findById(archivoId)
        .orElseThrow(() -> new RuntimeException("Archivo no encontrado con ID: " + archivoId));
    
    // Eliminar archivo físico
    if (archivo.getRutaArchivo() != null) {
        Path filePath = Paths.get(archivo.getRutaArchivo());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Error eliminando archivo físico: " + e.getMessage());
        }
    }
    
    // Eliminar registro de la base de datos
    dgiiArchivoRepository.delete(archivo);
}

/**
 * Elimina todos los archivos de un tipo específico
 */
@Transactional
public void eliminarArchivosPorTipo(String tipoArchivo) throws IOException {
    List<DgiiArchivo> archivos = dgiiArchivoRepository.findByTipoArchivoOrderByPeriodoDesc(tipoArchivo);
    
    for (DgiiArchivo archivo : archivos) {
        // Eliminar archivo físico
        if (archivo.getRutaArchivo() != null) {
            Path filePath = Paths.get(archivo.getRutaArchivo());
            Files.deleteIfExists(filePath);
        }
    }
    
    // Eliminar registros
    dgiiArchivoRepository.deleteByTipoArchivo(tipoArchivo);
}

/**
 * Elimina archivos por período
 */
@Transactional
public void eliminarArchivosPorPeriodo(String periodo) throws IOException {
    List<DgiiArchivo> archivos = dgiiArchivoRepository.findByPeriodo(periodo);
    
    for (DgiiArchivo archivo : archivos) {
        if (archivo.getRutaArchivo() != null) {
            Path filePath = Paths.get(archivo.getRutaArchivo());
            Files.deleteIfExists(filePath);
        }
    }
    
    dgiiArchivoRepository.deleteByPeriodo(periodo);
}



}