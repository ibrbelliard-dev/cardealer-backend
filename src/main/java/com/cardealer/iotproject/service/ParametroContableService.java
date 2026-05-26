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
        
        return descripciones.getOrDefault(clave, "Parámetro contable");
    }
}