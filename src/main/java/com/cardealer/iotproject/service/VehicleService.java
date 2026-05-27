package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.dto.DashboardStats;
import com.cardealer.iotproject.model.dto.SaleRequest;
import com.cardealer.iotproject.model.dto.VehicleSearchCriteria;
import com.cardealer.iotproject.model.entity.*;
import com.cardealer.iotproject.model.enums.VehicleStatus;
import com.cardealer.iotproject.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Service
public class VehicleService {
    
    private static final Logger log = Logger.getLogger(VehicleService.class.getName());
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private MakeRepository makeRepository;
    
    @Autowired
    private ModelRepository modelRepository;
    
    @Autowired
    private NhtsaService nhtsaService;
    
    @Autowired
    private VehicleImageRepository vehicleImageRepository;
    
    @Autowired
    private VehicleRecallLinkRepository vehicleRecallLinkRepository;
    
    @Autowired
    private ComplaintRepository complaintRepository;



@Autowired
private AsientoContableService asientoContableService;

@Autowired
private DetalleAsientoRepository detalleAsientoRepository;

@Autowired
private ParametroContableRepository parametroContableRepository;

@Autowired
private CommissionRepository commissionRepository;

@Autowired
private SalesRepRepository salesRepRepository;



    
    // ==================== GET VEHICLE METHODS ====================
    
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + vehicleId));
        
        // Initialize lazy-loaded associations
        if (vehicle.getMake() != null) {
            Hibernate.initialize(vehicle.getMake());
        }
        if (vehicle.getModel() != null) {
            Hibernate.initialize(vehicle.getModel());
        }
        
        return vehicle;
    }
    
    @Transactional(readOnly = true)
    public Vehicle getVehicleByVin(String vin) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con VIN: " + vin));
        
        // Initialize lazy-loaded associations
        if (vehicle.getMake() != null) {
            Hibernate.initialize(vehicle.getMake());
        }
        if (vehicle.getModel() != null) {
            Hibernate.initialize(vehicle.getModel());
        }
        
        return vehicle;
    }
    
    // ==================== REGISTER VEHICLE ====================
    
    @Transactional
    public Vehicle registerVehicle(Vehicle vehicle) {
        log.info("Iniciando registro de vehículo");
        
        if (vehicle.getVin() != null && !vehicle.getVin().isEmpty()) {
            String vin = vehicle.getVin().toUpperCase().trim();
            vehicle.setVin(vin);
            
            vehicleRepository.findByVin(vin)
                .ifPresent(existing -> {
                    throw new RuntimeException("Ya existe un vehículo con VIN: " + vin);
                });
            
            autoPopulateFromVin(vehicle);
        }
        
        validateVehicle(vehicle);
        ensureMakeAndModelPersisted(vehicle);
        
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setIsActive(true);
        vehicle.setDateAdded(LocalDateTime.now());
        vehicle.setLastModified(LocalDateTime.now());
        
        if (vehicle.getPurchaseDate() == null) {
            vehicle.setPurchaseDate(LocalDate.now());
        }
        
        Vehicle saved = vehicleRepository.save(vehicle);




                // En VehicleService.java, dentro de registerVehicle, después de Vehicle saved = vehicleRepository.save(vehicle);
                // ========== REGISTRAR ASIENTO CONTABLE POR COMPRA DE VEHÍCULO ==========
                try {
                    // Obtener parámetros contables para las cuentas por defecto
                    ParametroContable cuentaInventario = parametroContableRepository.findByClave("cuentaInventarioDefecto")
                        .orElseThrow(() -> new RuntimeException("Cuenta de inventario no configurada"));
                    ParametroContable cuentaProveedores = parametroContableRepository.findByClave("cuentaProveedorDefecto")
                        .orElseThrow(() -> new RuntimeException("Cuenta de proveedores no configurada"));
                    
                    // Crear asiento contable
                    AsientoContable asiento = new AsientoContable();
                    asiento.setFecha(LocalDate.now());
                    asiento.setDescripcion("Compra de vehículo: " + saved.getMake().getMakeName() + " " + saved.getModel().getModelName() + " (" + saved.getModelYear() + ")");
                    asiento.setTipoAsiento("COMPRA");
                    asiento.setEstado("APROBADO");
                    
                    // Calcular totales
                    Double valorCompra = saved.getPurchasePrice().doubleValue();
                    asiento.setTotalDebe(valorCompra);
                    asiento.setTotalHaber(valorCompra);
                    
                    AsientoContable asientoGuardado = asientoContableService.create(asiento, null);
                    
                    // Crear detalles del asiento
                    List<DetalleAsiento> detalles = new ArrayList<>();
                    
                    // Detalle 1: Debe a Inventario
                    DetalleAsiento detalleDebe = new DetalleAsiento();
                    detalleDebe.setAsiento(asientoGuardado);
                    detalleDebe.setCuentaCodigo(cuentaInventario.getValor());
                    detalleDebe.setCuentaNombre("Inventario de Vehículos");
                    detalleDebe.setDebe(valorCompra);
                    detalleDebe.setHaber(0.0);
                    detalles.add(detalleDebe);
                    
                    // Detalle 2: Haber a Cuentas por Pagar - Proveedores
                    DetalleAsiento detalleHaber = new DetalleAsiento();
                    detalleHaber.setAsiento(asientoGuardado);
                    detalleHaber.setCuentaCodigo(cuentaProveedores.getValor());
                    detalleHaber.setCuentaNombre("Cuentas por Pagar - Proveedores");
                    detalleHaber.setDebe(0.0);
                    detalleHaber.setHaber(valorCompra);
                    detalles.add(detalleHaber);
                    
                    detalleAsientoRepository.saveAll(detalles);
                    
                    log.info("Asiento contable creado para compra de vehículo ID: " + saved.getVehicleId());
                } catch (Exception e) {
                    log.warning("Error al crear asiento contable para compra: " + e.getMessage());
                }





        log.info("Vehículo registrado exitosamente con ID: " + saved.getVehicleId());
        
        return saved;
    }
    
    private void autoPopulateFromVin(Vehicle vehicle) {
        String vin = vehicle.getVin();
        try {
            Map<String, Object> decodedData = nhtsaService.decodeVin(vin, vehicle.getModelYear());
            if (decodedData == null || decodedData.isEmpty()) return;
            
            if (vehicle.getMake() == null || vehicle.getMake().getMakeName() == null) {
                String makeName = getStringValue(decodedData.get("Make"));
                if (makeName != null) {
                    Make make = makeRepository.findByMakeName(makeName)
                        .orElseGet(() -> {
                            Make newMake = new Make();
                            newMake.setMakeName(makeName);
                            return makeRepository.save(newMake);
                        });
                    vehicle.setMake(make);
                }
            }
            
            if (vehicle.getModel() == null || vehicle.getModel().getModelName() == null) {
                String modelName = getStringValue(decodedData.get("Model"));
                if (modelName != null && vehicle.getMake() != null) {
                    Model model = modelRepository.findByMakeAndModelName(vehicle.getMake(), modelName)
                        .orElseGet(() -> {
                            Model newModel = new Model();
                            newModel.setMake(vehicle.getMake());
                            newModel.setModelName(modelName);
                            return modelRepository.save(newModel);
                        });
                    vehicle.setModel(model);
                }
            }
            
            if (vehicle.getModelYear() == null) {
                Integer year = safeParseInt(decodedData.get("ModelYear"));
                if (year != null) vehicle.setModelYear(year);
            }
            
            vehicle.setBodyClass(getStringValue(decodedData.get("BodyClass")));
            vehicle.setDriveType(getStringValue(decodedData.get("DriveType")));
            vehicle.setEngineCylinders(safeParseInt(decodedData.get("EngineCylinders")));
            vehicle.setEngineDisplacementL(safeParseBigDecimal(decodedData.get("DisplacementL")));
            vehicle.setFuelTypePrimary(getStringValue(decodedData.get("FuelTypePrimary")));
            vehicle.setTransmissionStyle(getStringValue(decodedData.get("TransmissionStyle")));
            vehicle.setDoors(safeParseInt(decodedData.get("Doors")));
            vehicle.setPlantCity(getStringValue(decodedData.get("PlantCity")));
            vehicle.setPlantCountry(getStringValue(decodedData.get("PlantCountry")));
            
        } catch (Exception e) {
            log.warning("Error al decodificar VIN: " + e.getMessage());
        }
    }
    
    private void validateVehicle(Vehicle vehicle) {
        if (vehicle.getMake() == null || vehicle.getMake().getMakeName() == null) {
            throw new RuntimeException("La marca del vehículo es requerida");
        }
        if (vehicle.getModel() == null || vehicle.getModel().getModelName() == null) {
            throw new RuntimeException("El modelo del vehículo es requerido");
        }
        if (vehicle.getModelYear() == null) {
            throw new RuntimeException("El año del vehículo es requerido");
        }
    }
    
    private void ensureMakeAndModelPersisted(Vehicle vehicle) {
        if (vehicle.getMake().getMakeId() == null) {
            String makeName = vehicle.getMake().getMakeName();
            Make existingMake = makeRepository.findByMakeName(makeName).orElse(null);
            if (existingMake != null) {
                vehicle.setMake(existingMake);
            } else {
                vehicle.setMake(makeRepository.save(vehicle.getMake()));
            }
        }
        
        if (vehicle.getModel().getModelId() == null) {
            String modelName = vehicle.getModel().getModelName();
            Model existingModel = modelRepository.findByMakeAndModelName(vehicle.getMake(), modelName).orElse(null);
            if (existingModel != null) {
                vehicle.setModel(existingModel);
            } else {
                vehicle.getModel().setMake(vehicle.getMake());
                vehicle.setModel(modelRepository.save(vehicle.getModel()));
            }
        }
    }
    
    // ==================== UPDATE VEHICLE ====================
    
   @Transactional
public Vehicle updateVehicle(Long vehicleId, Vehicle vehicleDetails) {
    Vehicle vehicle = getVehicleById(vehicleId);
    
    // Handle Make update
    if (vehicleDetails.getMake() != null && vehicleDetails.getMake().getMakeName() != null) {
        String makeName = vehicleDetails.getMake().getMakeName();
        Make make = makeRepository.findByMakeName(makeName)
            .orElseGet(() -> {
                Make newMake = new Make();
                newMake.setMakeName(makeName);
                newMake.setMakeDisplayName(makeName);
                return makeRepository.save(newMake);
            });
        vehicle.setMake(make);
    }
    
    // Handle Model update
    if (vehicleDetails.getModel() != null && vehicleDetails.getModel().getModelName() != null) {
        String modelName = vehicleDetails.getModel().getModelName();
        Model model = modelRepository.findByMakeAndModelName(vehicle.getMake(), modelName)
            .orElseGet(() -> {
                Model newModel = new Model();
                newModel.setMake(vehicle.getMake());
                newModel.setModelName(modelName);
                return modelRepository.save(newModel);
            });
        vehicle.setModel(model);
    }
    
    // Update other fields including status
    if (vehicleDetails.getModelYear() != null) {
        vehicle.setModelYear(vehicleDetails.getModelYear());
    }
    if (vehicleDetails.getColor() != null) {
        vehicle.setColor(vehicleDetails.getColor());
    }
    if (vehicleDetails.getInteriorColor() != null) {
        vehicle.setInteriorColor(vehicleDetails.getInteriorColor());
    }
    if (vehicleDetails.getMileage() != null) {
        vehicle.setMileage(vehicleDetails.getMileage());
    }
    if (vehicleDetails.getPurchasePrice() != null) {
        vehicle.setPurchasePrice(vehicleDetails.getPurchasePrice());
    }
    if (vehicleDetails.getSellingPrice() != null) {
        vehicle.setSellingPrice(vehicleDetails.getSellingPrice());
    }
    if (vehicleDetails.getCondition() != null) {
        vehicle.setCondition(vehicleDetails.getCondition());
    }
    if (vehicleDetails.getStatus() != null) {  // Add status update
        vehicle.setStatus(vehicleDetails.getStatus());
    }
    if (vehicleDetails.getNotes() != null) {
        vehicle.setNotes(vehicleDetails.getNotes());
    }
    if (vehicleDetails.getBodyClass() != null) {
        vehicle.setBodyClass(vehicleDetails.getBodyClass());
    }
    if (vehicleDetails.getDriveType() != null) {
        vehicle.setDriveType(vehicleDetails.getDriveType());
    }
    if (vehicleDetails.getEngineCylinders() != null) {
        vehicle.setEngineCylinders(vehicleDetails.getEngineCylinders());
    }
    if (vehicleDetails.getEngineDisplacementL() != null) {
        vehicle.setEngineDisplacementL(vehicleDetails.getEngineDisplacementL());
    }
    if (vehicleDetails.getFuelTypePrimary() != null) {
        vehicle.setFuelTypePrimary(vehicleDetails.getFuelTypePrimary());
    }
    if (vehicleDetails.getTransmissionStyle() != null) {
        vehicle.setTransmissionStyle(vehicleDetails.getTransmissionStyle());
    }
    if (vehicleDetails.getDoors() != null) {
        vehicle.setDoors(vehicleDetails.getDoors());
    }
    if (vehicleDetails.getPlantCity() != null) {
        vehicle.setPlantCity(vehicleDetails.getPlantCity());
    }
    if (vehicleDetails.getPlantCountry() != null) {
        vehicle.setPlantCountry(vehicleDetails.getPlantCountry());
    }
    
    vehicle.setLastModified(LocalDateTime.now());
    
    Vehicle saved = vehicleRepository.save(vehicle);
    log.info("Vehículo ID: " + vehicleId + " actualizado. Nuevo estado: " + saved.getStatus());
    
    return saved;
}
    
    // ==================== SELL VEHICLE ====================

  @Transactional
public Vehicle sellVehicle(Long vehicleId, SaleRequest saleRequest) {
    Vehicle vehicle = getVehicleById(vehicleId);
    
    if (vehicle.getStatus() == VehicleStatus.SOLD) {
        throw new RuntimeException("El vehículo ya está vendido");
    }
    
    vehicle.setSellingPrice(saleRequest.getSellingPrice());
    vehicle.setStatus(VehicleStatus.SOLD);
    vehicle.setLastModified(LocalDateTime.now());
    
    String saleNotes = String.format("Vendido a: %s", saleRequest.getCustomerName());
    if (saleRequest.getCustomerPhone() != null && !saleRequest.getCustomerPhone().isEmpty()) {
        saleNotes += String.format(" | Teléfono: %s", saleRequest.getCustomerPhone());
    }
    if (saleRequest.getCustomerEmail() != null && !saleRequest.getCustomerEmail().isEmpty()) {
        saleNotes += String.format(" | Email: %s", saleRequest.getCustomerEmail());
    }
    if (saleRequest.getSalespersonName() != null && !saleRequest.getSalespersonName().isEmpty()) {
        saleNotes += String.format(" | Vendedor: %s", saleRequest.getSalespersonName());
    }
    if (saleRequest.getNotes() != null && !saleRequest.getNotes().isEmpty()) {
        saleNotes += String.format(" | Notas: %s", saleRequest.getNotes());
    }
    
    vehicle.setNotes(vehicle.getNotes() != null ? vehicle.getNotes() + "\n" + saleNotes : saleNotes);
    
    Vehicle saved = vehicleRepository.save(vehicle);
    
    // ========== REGISTRAR ASIENTO CONTABLE POR VENTA ==========
    try {
        // Obtener parámetros contables
        ParametroContable cuentaVentas = parametroContableRepository.findByClave("cuentaVentaDefecto")
            .orElseThrow(() -> new RuntimeException("Cuenta de ventas no configurada"));
        ParametroContable cuentaCosto = parametroContableRepository.findByClave("cuentaCostoDefecto")
            .orElseThrow(() -> new RuntimeException("Cuenta de costo no configurada"));
        ParametroContable cuentaItbis = parametroContableRepository.findByClave("cuentaItbisPorPagarDefecto")
            .orElseThrow(() -> new RuntimeException("Cuenta de ITBIS no configurada"));
        ParametroContable cuentaCliente = parametroContableRepository.findByClave("cuentaClienteDefecto")
            .orElseThrow(() -> new RuntimeException("Cuenta de clientes no configurada"));
        
        Double precioVenta = saved.getSellingPrice().doubleValue();
        Double costoVehiculo = saved.getPurchasePrice() != null ? saved.getPurchasePrice().doubleValue() : 0.0;
        Double itbis = precioVenta * 0.18;
        Double totalFactura = precioVenta + itbis;
        
        // Crear asiento contable
        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(LocalDate.now());
        asiento.setDescripcion("Venta de vehículo: " + saved.getMake().getMakeName() + " " + saved.getModel().getModelName() + 
                               " (" + saved.getModelYear() + ") - Cliente: " + saleRequest.getCustomerName());
        asiento.setTipoAsiento("VENTA");
        asiento.setEstado("APROBADO");
        asiento.setTotalDebe(totalFactura);
        asiento.setTotalHaber(totalFactura);
        
        AsientoContable asientoGuardado = asientoContableService.create(asiento, null);
        
        List<DetalleAsiento> detalles = new ArrayList<>();
        
        // Detalle 1: Debe a Clientes (Cuentas por Cobrar)
        DetalleAsiento detalleCliente = new DetalleAsiento();
        detalleCliente.setAsiento(asientoGuardado);
        detalleCliente.setCuentaCodigo(cuentaCliente.getValor());
        detalleCliente.setCuentaNombre("Cuentas por Cobrar - Clientes");
        detalleCliente.setDebe(totalFactura);
        detalleCliente.setHaber(0.0);
        detalles.add(detalleCliente);
        
        // Detalle 2: Haber a Ventas
        DetalleAsiento detalleVenta = new DetalleAsiento();
        detalleVenta.setAsiento(asientoGuardado);
        detalleVenta.setCuentaCodigo(cuentaVentas.getValor());
        detalleVenta.setCuentaNombre("Ventas de Vehículos");
        detalleVenta.setDebe(0.0);
        detalleVenta.setHaber(precioVenta);
        detalles.add(detalleVenta);
        
        // Detalle 3: Haber a ITBIS por Pagar
        DetalleAsiento detalleItbis = new DetalleAsiento();
        detalleItbis.setAsiento(asientoGuardado);
        detalleItbis.setCuentaCodigo(cuentaItbis.getValor());
        detalleItbis.setCuentaNombre("ITBIS por Pagar");
        detalleItbis.setDebe(0.0);
        detalleItbis.setHaber(itbis);
        detalles.add(detalleItbis);
        
        detalleAsientoRepository.saveAll(detalles);
        
        // Registrar costo de venta (asiento separado)
        if (costoVehiculo > 0) {
            AsientoContable asientoCosto = new AsientoContable();
            asientoCosto.setFecha(LocalDate.now());
            asientoCosto.setDescripcion("Costo de venta - Vehículo ID: " + saved.getVehicleId());
            asientoCosto.setTipoAsiento("COSTO");
            asientoCosto.setEstado("APROBADO");
            asientoCosto.setTotalDebe(costoVehiculo);
            asientoCosto.setTotalHaber(costoVehiculo);
            
            AsientoContable asientoCostoGuardado = asientoContableService.create(asientoCosto, null);
            
            List<DetalleAsiento> detallesCosto = new ArrayList<>();
            
            // Debe a Costo de Ventas
            DetalleAsiento detalleCostoDebe = new DetalleAsiento();
            detalleCostoDebe.setAsiento(asientoCostoGuardado);
            detalleCostoDebe.setCuentaCodigo(cuentaCosto.getValor());
            detalleCostoDebe.setCuentaNombre("Costo de Vehículos Vendidos");
            detalleCostoDebe.setDebe(costoVehiculo);
            detalleCostoDebe.setHaber(0.0);
            detallesCosto.add(detalleCostoDebe);
            
            // Haber a Inventario
            ParametroContable cuentaInventario = parametroContableRepository.findByClave("cuentaInventarioDefecto")
                .orElseThrow(() -> new RuntimeException("Cuenta de inventario no configurada"));
            DetalleAsiento detalleInventario = new DetalleAsiento();
            detalleInventario.setAsiento(asientoCostoGuardado);
            detalleInventario.setCuentaCodigo(cuentaInventario.getValor());
            detalleInventario.setCuentaNombre("Inventario de Vehículos");
            detalleInventario.setDebe(0.0);
            detalleInventario.setHaber(costoVehiculo);
            detallesCosto.add(detalleInventario);
            
            detalleAsientoRepository.saveAll(detallesCosto);
        }
        
        // Crear comisión para el vendedor
        crearComision(saleRequest, saved);
        
        log.info("Asientos contables creados para venta de vehículo ID: " + saved.getVehicleId());
    } catch (Exception e) {
        log.warning("Error al crear asientos contables para venta: " + e.getMessage());
    }
    
    return saved;
}

// Método auxiliar para crear comisión usando salespersonId
private void crearComision(SaleRequest saleRequest, Vehicle vehicle) {
    try {
        // Validar que tengamos información del vendedor
        if (saleRequest.getSalespersonId() == null || saleRequest.getSalespersonId().isEmpty()) {
            log.info("No hay salespersonId, no se creará comisión");
            return;
        }
        
        // Convertir String a Long
        Long salesRepId;
        try {
            salesRepId = Long.parseLong(saleRequest.getSalespersonId());
        } catch (NumberFormatException e) {
            log.warning("salespersonId no es un número válido: " + saleRequest.getSalespersonId());
            return;
        }
        
        // Buscar el vendedor
        SalesRep salesRep = salesRepRepository.findById(salesRepId)
            .orElseThrow(() -> new RuntimeException("Vendedor no encontrado con ID: " + salesRepId));
        
        // Calcular comisión
        BigDecimal commissionAmount = vehicle.getSellingPrice()
            .multiply(salesRep.getCommissionPercentage())
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // Crear la comisión
        Commission commission = new Commission();
        commission.setSalesRepId(salesRepId);
        commission.setVehicleId(vehicle.getVehicleId());
        commission.setSalePrice(vehicle.getSellingPrice());
        commission.setCommissionPercentage(salesRep.getCommissionPercentage());
        commission.setCommissionAmount(commissionAmount);
        commission.setStatus("PENDING");
        
        commissionRepository.save(commission);
        
        log.info("Comisión creada para vendedor ID: " + salesRepId + 
                 " (" + salesRep.getFirstName() + " " + salesRep.getLastName() + 
                 ") por monto: " + commissionAmount);
        
    } catch (Exception e) {
        log.warning("Error al crear comisión: " + e.getMessage());
    }
}  
    
    // ==================== SEARCH VEHICLES ====================
    
    @Transactional(readOnly = true)
    public Page<Vehicle> searchVehicles(VehicleSearchCriteria criteria, Pageable pageable) {
        Page<Vehicle> vehicles = vehicleRepository.searchVehicles(
            criteria.getMake(),
            criteria.getModel(),
            criteria.getYearMin(),
            criteria.getYearMax(),
            criteria.getStatus(),
            pageable
        );
        
        // Initialize lazy-loaded associations to avoid serialization issues
        vehicles.getContent().forEach(vehicle -> {
            if (vehicle.getMake() != null) {
                Hibernate.initialize(vehicle.getMake());
            }
            if (vehicle.getModel() != null) {
                Hibernate.initialize(vehicle.getModel());
            }
        });
        
        return vehicles;
    }
    
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByStatus(VehicleStatus status) {
        List<Vehicle> vehicles = vehicleRepository.findByStatus(status);
        
        // Initialize lazy-loaded associations
        vehicles.forEach(vehicle -> {
            if (vehicle.getMake() != null) {
                Hibernate.initialize(vehicle.getMake());
            }
            if (vehicle.getModel() != null) {
                Hibernate.initialize(vehicle.getModel());
            }
        });
        
        return vehicles;
    }
    
    @Transactional(readOnly = true)
    public List<Vehicle> getAvailableVehicles() {
        return getVehiclesByStatus(VehicleStatus.AVAILABLE);
    }
    
    // ==================== DELETE VEHICLE METHODS ====================
    
    @Transactional(readOnly = true)
    public Map<String, Object> getDeleteInfo(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        Map<String, Object> info = new HashMap<>();
        List<Map<String, Object>> dependencies = new ArrayList<>();
        
        // Check for images
        long imageCount = vehicleImageRepository.countByVehicle_VehicleId(vehicleId);
        if (imageCount > 0) {
            Map<String, Object> dep = new HashMap<>();
            dep.put("type", "images");
            dep.put("count", imageCount);
            dep.put("message", imageCount + " imagen(es) asociada(s)");
            dep.put("table", "vehicle_images");
            dependencies.add(dep);
        }
        
        // Check for recall links
        long recallCount = vehicleRecallLinkRepository.countByVehicleId(vehicleId);
        if (recallCount > 0) {
            Map<String, Object> dep = new HashMap<>();
            dep.put("type", "recalls");
            dep.put("count", recallCount);
            dep.put("message", recallCount + " recall(es) asociado(s)");
            dep.put("table", "vehicle_recall_links");
            dependencies.add(dep);
        }
        
        // Check for complaints
        long complaintCount = complaintRepository.countByVehicle_VehicleId(vehicleId);
        if (complaintCount > 0) {
            Map<String, Object> dep = new HashMap<>();
            dep.put("type", "complaints");
            dep.put("count", complaintCount);
            dep.put("message", complaintCount + " queja(s) asociada(s)");
            dep.put("table", "nhtsa_complaints");
            dependencies.add(dep);
        }
        
        info.put("dependencies", dependencies);
        info.put("hasDependencies", !dependencies.isEmpty());
        info.put("vehicleInfo", Map.of(
            "id", vehicle.getVehicleId(),
            "name", vehicle.getMake().getMakeName() + " " + vehicle.getModel().getModelName(),
            "year", vehicle.getModelYear(),
            "vin", vehicle.getVin() != null ? vehicle.getVin() : "N/A",
            "status", vehicle.getStatus() != null ? vehicle.getStatus().toString() : "UNKNOWN"
        ));
        
        return info;
    }
    
    @Transactional
    public void permanentlyDeleteVehicle(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        log.info("Iniciando eliminación permanente del vehículo ID: " + vehicleId);
        
        // 1. Delete associated images and their physical files
        List<VehicleImage> images = vehicleImageRepository.findByVehicle_VehicleIdOrderByIsPrimaryDescSortOrderAsc(vehicleId);
        if (images != null && !images.isEmpty()) {
            for (VehicleImage image : images) {
                try {
                    Path imagePath = Paths.get("./uploads/vehicles/" + vehicleId + "/" + image.getFilename());
                    Files.deleteIfExists(imagePath);
                    Path thumbnailPath = Paths.get("./uploads/vehicles/" + vehicleId + "/thumb_" + image.getFilename());
                    Files.deleteIfExists(thumbnailPath);
                    log.info("Archivo de imagen eliminado: " + image.getFilename());
                } catch (IOException e) {
                    log.warning("Error al eliminar archivo de imagen: " + e.getMessage());
                }
            }
            vehicleImageRepository.deleteByVehicle_VehicleId(vehicleId);
            log.info("Eliminados " + images.size() + " registros de imágenes");
        }
        
        // 2. Delete recall links
        List<VehicleRecallLink> recallLinks = vehicleRecallLinkRepository.findByVehicleId(vehicleId);
        if (recallLinks != null && !recallLinks.isEmpty()) {
            vehicleRecallLinkRepository.deleteAll(recallLinks);
            log.info("Eliminados " + recallLinks.size() + " registros de enlaces de recalls");
        }
        
        // 3. Delete complaints
        List<Complaint> complaints = complaintRepository.findByVehicle_VehicleId(vehicleId);
        if (complaints != null && !complaints.isEmpty()) {
            complaintRepository.deleteAll(complaints);
            log.info("Eliminados " + complaints.size() + " registros de quejas");
        }
        
        // 4. Finally, delete the vehicle itself
        vehicleRepository.delete(vehicle);
        log.info("Vehículo ID: " + vehicleId + " eliminado permanentemente");
    }
    
    @Transactional
    public void softDeleteVehicle(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        vehicle.setIsActive(false);
        vehicle.setLastModified(LocalDateTime.now());
        vehicleRepository.save(vehicle);
        log.info("Eliminación suave del vehículo ID: " + vehicleId);
    }
    
    // ==================== HELPER METHODS ====================
    
    private Make ensureMakePersisted(Make make) {
        if (make.getMakeId() != null) {
            return makeRepository.findById(make.getMakeId())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + make.getMakeId()));
        }
        
        String makeName = make.getMakeName();
        if (makeName == null || makeName.isEmpty()) {
            throw new RuntimeException("El nombre de la marca es requerido");
        }
        
        return makeRepository.findByMakeName(makeName)
            .orElseGet(() -> {
                Make newMake = new Make();
                newMake.setMakeName(makeName);
                return makeRepository.save(newMake);
            });
    }
    
    private Model ensureModelPersisted(Model model, Make make) {
        if (model.getModelId() != null) {
            return modelRepository.findById(model.getModelId())
                .orElseThrow(() -> new RuntimeException("Modelo no encontrado con ID: " + model.getModelId()));
        }
        
        String modelName = model.getModelName();
        if (modelName == null || modelName.isEmpty()) {
            throw new RuntimeException("El nombre del modelo es requerido");
        }
        
        if (make == null) {
            throw new RuntimeException("Se requiere la marca para guardar el modelo");
        }
        
        return modelRepository.findByMakeAndModelName(make, modelName)
            .orElseGet(() -> {
                Model newModel = new Model();
                newModel.setMake(make);
                newModel.setModelName(modelName);
                return modelRepository.save(newModel);
            });
    }
    
    private String getStringValue(Object value) {
        if (value == null) return null;
        String str = value.toString();
        if (str.trim().isEmpty()) return null;
        return str;
    }
    
    private Integer safeParseInt(Object value) {
        if (value == null) return null;
        String str = value.toString().trim();
        if (str.isEmpty()) return null;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private BigDecimal safeParseBigDecimal(Object value) {
        if (value == null) return null;
        String str = value.toString().trim();
        if (str.isEmpty()) return null;
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    // ==================== DASHBOARD STATS ====================
    
    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        List<Object[]> inventoryStats = vehicleRepository.getInventoryStats();
        List<Object[]> monthlySalesStats = vehicleRepository.getMonthlySalesStats();
        
        long totalVehicles = 0;
        BigDecimal totalInventoryValue = BigDecimal.ZERO;
        BigDecimal averagePrice = BigDecimal.ZERO;
        
        if (!inventoryStats.isEmpty()) {
            Object[] stats = inventoryStats.get(0);
            totalVehicles = (Long) stats[0];
            totalInventoryValue = (BigDecimal) stats[1];
            averagePrice = (BigDecimal) stats[2];
        }
        
        long soldVehiclesThisMonth = 0;
        BigDecimal monthlyRevenue = BigDecimal.ZERO;
        
        if (!monthlySalesStats.isEmpty()) {
            Object[] salesStats = monthlySalesStats.get(0);
            soldVehiclesThisMonth = (Long) salesStats[0];
            monthlyRevenue = (BigDecimal) salesStats[1];
        }
        
        long vehiclesInService = vehicleRepository.countVehiclesInService();
        long availableVehicles = vehicleRepository.countAvailableVehicles();
        long soldVehicles = vehicleRepository.countSoldVehicles();
        
        return DashboardStats.builder()
            .totalVehicles(totalVehicles)
            .availableVehicles(availableVehicles)
            .soldVehiclesThisMonth(soldVehiclesThisMonth)
            .vehiclesInService(vehiclesInService)
            .totalInventoryValue(totalInventoryValue)
            .averageVehiclePrice(averagePrice)
            .monthlyRevenue(monthlyRevenue)
            .soldVehicles(soldVehicles)
            .build();
    }
    
    // ==================== PROFIT CALCULATIONS ====================
    
    @Transactional(readOnly = true)
    public BigDecimal calculateProfit(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        
        if (vehicle.getStatus() != VehicleStatus.SOLD) {
            throw new RuntimeException("El vehículo no está vendido aún");
        }
        
        if (vehicle.getPurchasePrice() == null || vehicle.getSellingPrice() == null) {
            return BigDecimal.ZERO;
        }
        
        return vehicle.getSellingPrice().subtract(vehicle.getPurchasePrice());
    }
    
    @Transactional(readOnly = true)
    public Double calculateProfitMargin(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        
        if (vehicle.getStatus() != VehicleStatus.SOLD) {
            throw new RuntimeException("El vehículo no está vendido aún");
        }
        
        if (vehicle.getPurchasePrice() == null || vehicle.getSellingPrice() == null || 
            vehicle.getPurchasePrice().compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        BigDecimal profit = vehicle.getSellingPrice().subtract(vehicle.getPurchasePrice());
        BigDecimal margin = profit.divide(vehicle.getPurchasePrice(), 4, RoundingMode.HALF_UP);
        
        return margin.doubleValue() * 100;
    }

    public List<Vehicle> getAllVehicles() {
    return vehicleRepository.findAll();
}

}