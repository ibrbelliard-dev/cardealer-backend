package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.ExchangeRate;
import com.cardealer.iotproject.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exchange-rates")
public class ExchangeRateController {
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    @GetMapping
    @Operation(summary = "Get all exchange rates")
    public ResponseEntity<ApiResponse> getAllRates() {
        List<ExchangeRate> rates = exchangeRateService.getAllRates();
        return ResponseEntity.ok(ApiResponse.success("Tasas de cambio recuperadas", rates));
    }
    
    @GetMapping("/{currencyCode}")
    @Operation(summary = "Get exchange rate by currency code")
    public ResponseEntity<ApiResponse> getRateByCurrency(@PathVariable String currencyCode) {
        ExchangeRate rate = exchangeRateService.getRateByCurrency(currencyCode);
        if (rate != null) {
            return ResponseEntity.ok(ApiResponse.success("Tasa recuperada", rate));
        }
        return ResponseEntity.ok(ApiResponse.success("No encontrada", null));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update exchange rate")
    public ResponseEntity<ApiResponse> updateRate(@PathVariable Long id, @RequestBody Map<String, Double> request) {
        Double newRate = request.get("rate");
        ExchangeRate updated = exchangeRateService.updateRate(id, newRate);
        if (updated != null) {
            return ResponseEntity.ok(ApiResponse.success("Tasa actualizada", updated));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Error al actualizar tasa"));
    }
    
    @PostMapping("/initialize")
    @Operation(summary = "Initialize default exchange rates")
    public ResponseEntity<ApiResponse> initializeRates() {
        exchangeRateService.initializeDefaultRates();
        return ResponseEntity.ok(ApiResponse.success("Tasas inicializadas", null));
    }
    
    @PostMapping
    @Operation(summary = "Create a new exchange rate")
    public ResponseEntity<ApiResponse> createRate(@RequestBody ExchangeRate rate) {
        try {
            ExchangeRate saved = exchangeRateService.saveRate(rate);
            return ResponseEntity.ok(ApiResponse.success("Moneda agregada exitosamente", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error al agregar moneda: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exchange rate")
    public ResponseEntity<ApiResponse> deleteRate(@PathVariable Long id) {
        try {
            // Necesitas agregar este método en el servicio
            exchangeRateService.deleteRate(id);
            return ResponseEntity.ok(ApiResponse.success("Moneda eliminada exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error al eliminar moneda: " + e.getMessage()));
        }
    }
    
    @PostMapping("/convert")
    @Operation(summary = "Convert currency")
    public ResponseEntity<ApiResponse> convertCurrency(@RequestBody Map<String, Object> request) {
        Double amount = Double.valueOf(request.get("amount").toString());
        String fromCurrency = request.get("fromCurrency").toString();
        String toCurrency = request.get("toCurrency").toString();
        
        Double result;
        if ("DOP".equals(toCurrency)) {
            result = exchangeRateService.convertToDOP(amount, fromCurrency);
        } else if ("DOP".equals(fromCurrency)) {
            result = exchangeRateService.convertFromDOP(amount, toCurrency);
        } else {
            Double inDOP = exchangeRateService.convertToDOP(amount, fromCurrency);
            result = exchangeRateService.convertFromDOP(inDOP, toCurrency);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("originalAmount", amount);
        response.put("originalCurrency", fromCurrency);
        response.put("convertedAmount", result);
        response.put("targetCurrency", toCurrency);
        
        return ResponseEntity.ok(ApiResponse.success("Conversión completada", response));
    }
}