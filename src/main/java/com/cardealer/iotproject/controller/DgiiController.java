// src/main/java/com/cardealer/iotproject/controller/DgiiController.java
package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.DgiiArchivo;
import com.cardealer.iotproject.service.DgiiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cardealer.iotproject.model.dto.DgiiArchivoDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dgii")
public class DgiiController {

    @Autowired
    private DgiiService dgiiService;

    /**
     * Generar Formato 606 (Compras y gastos)
     */
    @GetMapping("/formato-606")
    public ResponseEntity<ApiResponse> generarFormato606(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            String contenido = dgiiService.generarFormato606(year, month);
            String periodo = String.format("%d-%02d", year, month);
            DgiiArchivo archivo = dgiiService.guardarArchivo("606", periodo, contenido);
            return ResponseEntity.ok(ApiResponse.success("Formato 606 generado exitosamente", archivo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error generando formato 606: " + e.getMessage()));
        }
    }

    /**
     * Generar Formato 607 (Ventas e ingresos)
     */
    @GetMapping("/formato-607")
    public ResponseEntity<ApiResponse> generarFormato607(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            String contenido = dgiiService.generarFormato607(year, month);
            String periodo = String.format("%d-%02d", year, month);
            DgiiArchivo archivo = dgiiService.guardarArchivo("607", periodo, contenido);
            return ResponseEntity.ok(ApiResponse.success("Formato 607 generado exitosamente", archivo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error generando formato 607: " + e.getMessage()));
        }
    }

    /**
     * Generar IR-17 (Retenciones)
     */
    @GetMapping("/ir-17")
    public ResponseEntity<ApiResponse> generarIr17(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            String contenido = dgiiService.generarIr17(year, month);
            String periodo = String.format("%d-%02d", year, month);
            DgiiArchivo archivo = dgiiService.guardarArchivo("IR-17", periodo, contenido);
            return ResponseEntity.ok(ApiResponse.success("IR-17 generado exitosamente", archivo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error generando IR-17: " + e.getMessage()));
        }
    }

    /**
     * Generar IT-1 (Declaración ITBIS)
     */
    @GetMapping("/it-1")
    public ResponseEntity<ApiResponse> generarIt1(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            String contenido = dgiiService.generarIt1(year, month);
            String periodo = String.format("%d-%02d", year, month);
            DgiiArchivo archivo = dgiiService.guardarArchivo("IT-1", periodo, contenido);
            return ResponseEntity.ok(ApiResponse.success("IT-1 generado exitosamente", archivo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error generando IT-1: " + e.getMessage()));
        }
    }

    /**
     * Listar archivos DGII generados
     */
 
    @GetMapping("/archivos")
    public ResponseEntity<ApiResponse> listarArchivos(
            @RequestParam(required = false) String tipoArchivo) {
        List<DgiiArchivoDTO> archivos = dgiiService.listarArchivosDTO(tipoArchivo);
        return ResponseEntity.ok(ApiResponse.success("Archivos listados exitosamente", archivos));
    }
    /**
     * Descargar archivo DGII
     */
    @GetMapping("/descargar/{archivoId}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long archivoId) {
        try {
            byte[] contenido = dgiiService.descargarArchivo(archivoId);
            
            DgiiArchivo archivo = dgiiService.listarArchivos(null).stream()
                    .filter(a -> a.getArchivoId().equals(archivoId))
                    .findFirst()
                    .orElse(null);
            
            String filename = archivo != null ? 
                String.format("%s_%s.txt", archivo.getTipoArchivo(), archivo.getPeriodo()) : 
                "archivo_dgii.txt";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(contenido, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener resumen del período (para el dashboard)
     */
    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse> getResumenPeriodo(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            String formato606 = dgiiService.generarFormato606(year, month);
            String formato607 = dgiiService.generarFormato607(year, month);
            String ir17 = dgiiService.generarIr17(year, month);
            String it1 = dgiiService.generarIt1(year, month);
            
            String[] lineas606 = formato606.split("\n");
            String[] lineas607 = formato607.split("\n");
            
            java.util.Map<String, Object> resumen = new java.util.HashMap<>();
            resumen.put("periodo", String.format("%d-%02d", year, month));
            resumen.put("totalCompras", lineas606.length - 1);
            resumen.put("totalVentas", lineas607.length - 1);
            resumen.put("fechaGeneracion", LocalDate.now().toString());
            
            // Extraer totales del IT-1
            if (it1.contains("ITBIS a Pagar:")) {
                for (String linea : it1.split("\n")) {
                    if (linea.contains("ITBIS a Pagar:")) {
                        resumen.put("itbisAPagar", linea.replace("ITBIS a Pagar: RD$", "").trim());
                    }
                }
            }
            
            return ResponseEntity.ok(ApiResponse.success("Resumen del período obtenido", resumen));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo resumen: " + e.getMessage()));
        }
    }



    
/**
 * Eliminar un archivo específico
 */
@DeleteMapping("/eliminar/{archivoId}")
public ResponseEntity<ApiResponse> eliminarArchivo(@PathVariable Long archivoId) {
    try {
        dgiiService.eliminarArchivo(archivoId);
        return ResponseEntity.ok(ApiResponse.success("Archivo eliminado exitosamente", null));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error eliminando archivo: " + e.getMessage()));
    }
}

/**
 * Eliminar todos los archivos de un tipo
 */
@DeleteMapping("/eliminar-tipo/{tipoArchivo}")
public ResponseEntity<ApiResponse> eliminarArchivosPorTipo(@PathVariable String tipoArchivo) {
    try {
        dgiiService.eliminarArchivosPorTipo(tipoArchivo);
        return ResponseEntity.ok(ApiResponse.success("Archivos de tipo " + tipoArchivo + " eliminados exitosamente", null));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error eliminando archivos: " + e.getMessage()));
    }
}

/**
 * Eliminar archivos por período
 */
@DeleteMapping("/eliminar-periodo/{periodo}")
public ResponseEntity<ApiResponse> eliminarArchivosPorPeriodo(@PathVariable String periodo) {
    try {
        dgiiService.eliminarArchivosPorPeriodo(periodo);
        return ResponseEntity.ok(ApiResponse.success("Archivos del período " + periodo + " eliminados exitosamente", null));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error eliminando archivos: " + e.getMessage()));
    }
}
}