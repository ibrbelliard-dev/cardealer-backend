// src/main/java/com/cardealer/iotproject/service/AsientoContableService.java
package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.AsientoContable;
import com.cardealer.iotproject.model.entity.DetalleAsiento;
import com.cardealer.iotproject.model.entity.Invoice;      // ← AGREGAR
import com.cardealer.iotproject.model.entity.Vehicle;     // ← AGREGAR
import com.cardealer.iotproject.repository.AsientoContableRepository;
import com.cardealer.iotproject.repository.DetalleAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsientoContableService {

    @Autowired
    private AsientoContableRepository asientoRepository;
    
    @Autowired
    private DetalleAsientoRepository detalleAsientoRepository;

    @Transactional(readOnly = true)
    public List<AsientoContable> findAll() {
        return asientoRepository.findByActivoTrueOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public AsientoContable findById(Integer id) {
        return asientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado con id: " + id));
    }

    /**
     * Obtiene un asiento con sus detalles (para el controller)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAsientoWithDetalles(Integer id) {
        AsientoContable asiento = findById(id);
        List<DetalleAsiento> detalles = detalleAsientoRepository.findByAsiento_AsientoId(id);
        
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

    /**
     * Crea un asiento sin detalles (para compatibilidad)
     */
    @Transactional
    public AsientoContable create(AsientoContable asiento) {
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
        
        if (asiento.getEstado() == null) {
            asiento.setEstado("PENDIENTE");
        }
        asiento.setActivo(true);
        asiento.setCreatedAt(LocalDateTime.now());
        asiento.setUpdatedAt(LocalDateTime.now());
        
        return asientoRepository.save(asiento);
    }
    
    /**
     * Crea un asiento con sus detalles (sobrecarga del método)
     */
    @Transactional
    public AsientoContable create(AsientoContable asiento, List<DetalleAsiento> detalles) {
        AsientoContable saved = create(asiento);
        if (detalles != null) {
            for (DetalleAsiento detalle : detalles) {
                detalle.setAsiento(saved);
                detalleAsientoRepository.save(detalle);
            }
        }
        return saved;
    }

    @Transactional
    public AsientoContable update(Integer id, AsientoContable asientoDetails) {
        AsientoContable asiento = findById(id);
        
        if (asientoDetails.getFecha() != null) {
            asiento.setFecha(asientoDetails.getFecha());
        }
        if (asientoDetails.getDescripcion() != null) {
            asiento.setDescripcion(asientoDetails.getDescripcion());
        }
        if (asientoDetails.getTipoAsiento() != null) {
            asiento.setTipoAsiento(asientoDetails.getTipoAsiento());
        }
        if (asientoDetails.getTotalDebe() != null) {
            asiento.setTotalDebe(asientoDetails.getTotalDebe());
        }
        if (asientoDetails.getTotalHaber() != null) {
            asiento.setTotalHaber(asientoDetails.getTotalHaber());
        }
        asiento.setUpdatedAt(LocalDateTime.now());
        
        return asientoRepository.save(asiento);
    }

    @Transactional
    public AsientoContable aprobar(Integer id) {
        AsientoContable asiento = findById(id);
        asiento.setEstado("APROBADO");
        asiento.setUpdatedAt(LocalDateTime.now());
        return asientoRepository.save(asiento);
    }

    @Transactional
    public void delete(Integer id) {
        AsientoContable asiento = findById(id);
        asiento.setActivo(false);
        asiento.setUpdatedAt(LocalDateTime.now());
        asientoRepository.save(asiento);
    }
    
    /**
     * Elimina físicamente un asiento y sus detalles
     */
    @Transactional
    public void deletePermanently(Integer id) {
        detalleAsientoRepository.deleteByAsiento_AsientoId(id);
        asientoRepository.deleteById(id);
    }
    
    /**
     * Crea un asiento de compra para un vehículo
     */
    @Transactional
    public AsientoContable crearAsientoCompraVehiculo(Vehicle vehicle, Double precioCompra, String cuentaInventario, String cuentaProveedor) {
        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(LocalDate.now());
        asiento.setDescripcion("Compra de vehículo: " + vehicle.getMake().getMakeName() + " " + 
                              vehicle.getModel().getModelName() + " (" + vehicle.getModelYear() + ")");
        asiento.setTipoAsiento("COMPRA");
        asiento.setEstado("APROBADO");
        asiento.setTotalDebe(precioCompra);
        asiento.setTotalHaber(precioCompra);
        
        List<DetalleAsiento> detalles = new java.util.ArrayList<>();
        
        DetalleAsiento detalleInventario = new DetalleAsiento();
        detalleInventario.setCuentaCodigo(cuentaInventario);
        detalleInventario.setCuentaNombre("Inventario de Vehículos");
        detalleInventario.setDebe(precioCompra);
        detalleInventario.setHaber(0.0);
        detalles.add(detalleInventario);
        
        DetalleAsiento detalleProveedor = new DetalleAsiento();
        detalleProveedor.setCuentaCodigo(cuentaProveedor);
        detalleProveedor.setCuentaNombre("Cuentas por Pagar - Proveedores");
        detalleProveedor.setDebe(0.0);
        detalleProveedor.setHaber(precioCompra);
        detalles.add(detalleProveedor);
        
        return create(asiento, detalles);
    }
    
    /**
     * Crea un asiento de venta para un vehículo
     */
    @Transactional
    public AsientoContable crearAsientoVentaVehiculo(Invoice invoice, Double precioVenta, Double itbis, 
                                                      String cuentaCliente, String cuentaVenta, String cuentaItbis) {
        Double subtotal = precioVenta - itbis;
        
        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(invoice.getInvoiceDateTime().toLocalDate());
        asiento.setDescripcion("Venta de vehículo - Factura: " + invoice.getEnNcf() + 
                              " - Cliente: " + invoice.getCustomerName());
        asiento.setTipoAsiento("VENTA");
        asiento.setEstado("APROBADO");
        asiento.setTotalDebe(precioVenta);
        asiento.setTotalHaber(precioVenta);
        
        List<DetalleAsiento> detalles = new java.util.ArrayList<>();
        
        // Debe a Clientes
        DetalleAsiento detalleCliente = new DetalleAsiento();
        detalleCliente.setCuentaCodigo(cuentaCliente);
        detalleCliente.setCuentaNombre("Cuentas por Cobrar - Clientes");
        detalleCliente.setDebe(precioVenta);
        detalleCliente.setHaber(0.0);
        detalles.add(detalleCliente);
        
        // Haber a Ventas
        DetalleAsiento detalleVenta = new DetalleAsiento();
        detalleVenta.setCuentaCodigo(cuentaVenta);
        detalleVenta.setCuentaNombre("Ventas de Vehículos");
        detalleVenta.setDebe(0.0);
        detalleVenta.setHaber(subtotal);
        detalles.add(detalleVenta);
        
        // Haber a ITBIS por Pagar
        DetalleAsiento detalleItbis = new DetalleAsiento();
        detalleItbis.setCuentaCodigo(cuentaItbis);
        detalleItbis.setCuentaNombre("ITBIS por Pagar");
        detalleItbis.setDebe(0.0);
        detalleItbis.setHaber(itbis);
        detalles.add(detalleItbis);
        
        return create(asiento, detalles);
    }



    // src/main/java/com/cardealer/iotproject/service/AsientoContableService.java
// Agrega este método después del método update existente

/**
 * Actualiza un asiento completo con sus detalles
 */

// src/main/java/com/cardealer/iotproject/service/AsientoContableService.java
// Modifica el método updateWithDetails - ELIMINA la línea de cuentaId

/**
 * Actualiza un asiento completo con sus detalles
 */

// src/main/java/com/cardealer/iotproject/service/AsientoContableService.java
// Agrega este método en el service

/**
 * Actualiza un asiento completo con sus detalles
 */
@Transactional
public AsientoContable updateWithDetails(Integer id, Map<String, Object> request) {
    // Buscar el asiento existente
    AsientoContable asientoExistente = findById(id);
    
    // Verificar que el asiento no esté contabilizado
    if ("CONTABILIZADO".equals(asientoExistente.getEstado())) {
        throw new RuntimeException("No se puede modificar un asiento contabilizado");
    }
    
    // Actualizar campos del asiento
    if (request.get("fecha") != null) {
        asientoExistente.setFecha(LocalDate.parse((String) request.get("fecha")));
    }
    if (request.get("descripcion") != null) {
        asientoExistente.setDescripcion((String) request.get("descripcion"));
    }
    if (request.get("tipoAsiento") != null) {
        asientoExistente.setTipoAsiento((String) request.get("tipoAsiento"));
    }
    if (request.get("totalDebe") != null) {
        asientoExistente.setTotalDebe(((Number) request.get("totalDebe")).doubleValue());
    }
    if (request.get("totalHaber") != null) {
        asientoExistente.setTotalHaber(((Number) request.get("totalHaber")).doubleValue());
    }
    asientoExistente.setUpdatedAt(LocalDateTime.now());
    
    // Eliminar detalles antiguos
    detalleAsientoRepository.deleteByAsiento_AsientoId(id);
    
    // Crear nuevos detalles
    List<Map<String, Object>> detallesMap = (List<Map<String, Object>>) request.get("detalles");
    if (detallesMap != null && !detallesMap.isEmpty()) {
        for (Map<String, Object> detalleMap : detallesMap) {
            DetalleAsiento detalle = new DetalleAsiento();
            detalle.setAsiento(asientoExistente);
            detalle.setCuentaCodigo((String) detalleMap.get("cuentaCodigo"));
            detalle.setCuentaNombre((String) detalleMap.get("cuentaNombre"));
            detalle.setDebe(((Number) detalleMap.get("debe")).doubleValue());
            detalle.setHaber(((Number) detalleMap.get("haber")).doubleValue());
            
            detalleAsientoRepository.save(detalle);
        }
    }
    
    return asientoRepository.save(asientoExistente);
}

}