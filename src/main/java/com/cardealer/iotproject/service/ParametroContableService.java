// src/main/java/com/cardealer/iotproject/service/ParametroContableService.java
package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.ParametroContable;
import com.cardealer.iotproject.repository.ParametroContableRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ParametroContableService {

    @Autowired
    private ParametroContableRepository parametroContableRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public Map<String, Object> getAllParametros() {
        Map<String, Object> parametros = new HashMap<>();
        List<ParametroContable> lista = parametroContableRepository.findAll();
        
        for (ParametroContable param : lista) {
            String valor = param.getValor();
            String tipo = param.getTipo();
            
            try {
                switch (tipo) {
                    case "NUMBER":
                        parametros.put(param.getClave(), Double.parseDouble(valor));
                        break;
                    case "BOOLEAN":
                        parametros.put(param.getClave(), Boolean.parseBoolean(valor));
                        break;
                    case "INTEGER":
                        parametros.put(param.getClave(), Integer.parseInt(valor));
                        break;
                    default:
                        parametros.put(param.getClave(), valor);
                }
            } catch (Exception e) {
                parametros.put(param.getClave(), valor);
            }
        }
        
        return parametros;
    }

    @Transactional
    public void saveAllParametros(Map<String, Object> parametros) {
        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            String clave = entry.getKey();
            Object valor = entry.getValue();
            
            ParametroContable param = parametroContableRepository.findByClave(clave)
                .orElse(new ParametroContable());
            
            param.setClave(clave);
            param.setValor(valor != null ? valor.toString() : "");
            
            // Inferir tipo
            if (valor instanceof Number) {
                if (valor instanceof Double || valor instanceof Float) {
                    param.setTipo("NUMBER");
                } else {
                    param.setTipo("INTEGER");
                }
            } else if (valor instanceof Boolean) {
                param.setTipo("BOOLEAN");
            } else {
                param.setTipo("STRING");
            }
            
            param.setDescripcion(getDescripcion(clave));
            param.setActivo(true);
            
            parametroContableRepository.save(param);
        }
    }

    /**
     * Obtiene el próximo número de NCF para un tipo específico
     */
    @Transactional
    public String getNextNcfNumber(String tipoNCF) {
        String clave = "ncf_" + tipoNCF + "_actual";
        Optional<ParametroContable> paramOpt = parametroContableRepository.findByClave(clave);
        
        long currentNumber = 1;
        if (paramOpt.isPresent()) {
            try {
                currentNumber = Long.parseLong(paramOpt.get().getValor()) + 1;
            } catch (NumberFormatException e) {
                currentNumber = 1;
            }
        }
        
        // Guardar el nuevo número
        ParametroContable param = paramOpt.orElse(new ParametroContable());
        param.setClave(clave);
        param.setValor(String.valueOf(currentNumber));
        param.setTipo("INTEGER");
        param.setDescripcion("Número actual para NCF tipo " + tipoNCF);
        param.setActivo(true);
        parametroContableRepository.save(param);
        
        // Formatear NCF según el tipo
        return formatNcfNumber(tipoNCF, currentNumber);
    }
    
    /**
     * Formatea el número NCF según el tipo
     * Tipos NCF:
     * 01 - Crédito Fiscal
     * 02 - Consumo
     * 03 - Régimen Especial
     * 04 - Gubernamental
     * 11 - Compras (no contribuyentes)
     * 12 - Registro Único de Ingresos
     * 13 - Gastos Menores
     * 14 - Compras de Activos Fijos
     * 15 - Comprobante para Contribuyentes Especiales
     * 16 - Exportaciones
     * 17 - Pagos al Exterior
     */
    private String formatNcfNumber(String tipoNCF, long number) {
        // Prefijos según tipo de NCF
        Map<String, String> prefijos = new HashMap<>();
        prefijos.put("01", "B01");  // Crédito Fiscal
        prefijos.put("02", "B02");  // Consumo
        prefijos.put("03", "B03");  // Régimen Especial
        prefijos.put("04", "B04");  // Gubernamental
        prefijos.put("11", "B11");  // Compras
        prefijos.put("12", "B12");  // Registro Único
        prefijos.put("13", "B13");  // Gastos Menores
        prefijos.put("14", "B14");  // Activos Fijos
        prefijos.put("15", "B15");  // Contribuyentes Especiales
        prefijos.put("16", "B16");  // Exportaciones
        prefijos.put("17", "B17");  // Pagos al Exterior
        
        String prefijo = prefijos.getOrDefault(tipoNCF, "B" + tipoNCF);
        
        // Formato: Prefijo + 10 dígitos (ej: B010000000001)
        return prefijo + String.format("%010d", number);
    }
    
    /**
     * Resetea la secuencia NCF para un tipo específico
     */
    @Transactional
    public void resetNcfSequence(String tipoNCF, long newNumber) {
        String clave = "ncf_" + tipoNCF + "_actual";
        ParametroContable param = parametroContableRepository.findByClave(clave)
            .orElse(new ParametroContable());
        
        param.setClave(clave);
        param.setValor(String.valueOf(newNumber - 1)); // Se incrementará al obtener el próximo
        param.setTipo("INTEGER");
        param.setDescripcion("Número actual para NCF tipo " + tipoNCF);
        param.setActivo(true);
        parametroContableRepository.save(param);
    }
    
    /**
     * Obtiene todas las secuencias NCF actuales
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getNcfSequences() {
        Map<String, Object> sequences = new HashMap<>();
        String[] tiposNCF = {"01", "02", "03", "04", "11", "12", "13", "14", "15", "16", "17"};
        
        for (String tipo : tiposNCF) {
            String clave = "ncf_" + tipo + "_actual";
            Optional<ParametroContable> param = parametroContableRepository.findByClave(clave);
            
            long currentNumber = 1;
            if (param.isPresent()) {
                try {
                    currentNumber = Long.parseLong(param.get().getValor());
                } catch (NumberFormatException e) {
                    currentNumber = 1;
                }
            }
            
            sequences.put(tipo, formatNcfNumber(tipo, currentNumber));
        }
        
        return sequences;
    }

    private String getDescripcion(String clave) {
        Map<String, String> descripciones = new HashMap<>();
        descripciones.put("itbisTasa", "Tasa de ITBIS (%)");
        descripciones.put("isrTasa", "Tasa de ISR (%)");
        descripciones.put("isrRetencion", "Retención de ISR (%)");
        descripciones.put("itbisRetencion", "Retención de ITBIS (%)");
        descripciones.put("diasCierreMensual", "Días para cierre mensual");
        descripciones.put("permitirAsientosEnPeriodoCerrado", "Permitir asientos en períodos cerrados");
        descripciones.put("asientoAutomaticoVentas", "Asiento automático en ventas");
        descripciones.put("asientoAutomaticoCompras", "Asiento automático en compras");
        descripciones.put("asientoAutomaticoPagos", "Asiento automático en pagos");
        descripciones.put("numeracionAutomatica", "Numeración automática de asientos");
        descripciones.put("prefijoAsiento", "Prefijo para asientos contables");
        descripciones.put("formatoReporte", "Formato por defecto de reportes");
        descripciones.put("incluirSubcuentasEnReportes", "Incluir subcuentas en reportes");
        descripciones.put("redondearDecimales", "Decimales en reportes");
        descripciones.put("usarNCF", "Usar NCF (Comprobantes Fiscales)");
        descripciones.put("ncfSerieActual", "Serie NCF actual");
        descripciones.put("tssAfpTasaEmpleado", "AFP - Tasa empleado (%)");
        descripciones.put("tssAfpTasaEmpleador", "AFP - Tasa empleador (%)");
        descripciones.put("tssSfsTasaEmpleado", "SFS - Tasa empleado (%)");
        descripciones.put("tssSfsTasaEmpleador", "SFS - Tasa empleador (%)");
        descripciones.put("tssInfotepTasa", "INFOTEP - Tasa (%)");
        
        // Nuevas descripciones para NCF
        descripciones.put("ncf_01_actual", "NCF Crédito Fiscal - Número actual");
        descripciones.put("ncf_02_actual", "NCF Consumo - Número actual");
        descripciones.put("ncf_03_actual", "NCF Régimen Especial - Número actual");
        descripciones.put("ncf_04_actual", "NCF Gubernamental - Número actual");
        descripciones.put("ncf_11_actual", "NCF Compras - Número actual");
        descripciones.put("ncf_12_actual", "NCF Registro Único - Número actual");
        descripciones.put("ncf_13_actual", "NCF Gastos Menores - Número actual");
        descripciones.put("ncf_14_actual", "NCF Activos Fijos - Número actual");
        descripciones.put("ncf_15_actual", "NCF Contribuyentes Especiales - Número actual");
        descripciones.put("ncf_16_actual", "NCF Exportaciones - Número actual");
        descripciones.put("ncf_17_actual", "NCF Pagos al Exterior - Número actual");
        
        return descripciones.getOrDefault(clave, "Parámetro contable");
    }
}