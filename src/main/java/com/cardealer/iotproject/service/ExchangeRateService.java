package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.ExchangeRate;
import com.cardealer.iotproject.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExchangeRateService {
    
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    
    public List<ExchangeRate> getAllRates() {
        return exchangeRateRepository.findAllByIsActiveTrue();
    }
    
    public ExchangeRate getRateByCurrency(String currencyCode) {
        return exchangeRateRepository.findByCurrencyCode(currencyCode.toUpperCase())
            .orElse(null);
    }
    
    public ExchangeRate saveRate(ExchangeRate rate) {
        rate.setUpdatedAt(LocalDateTime.now());
        return exchangeRateRepository.save(rate);
    }
    
    public ExchangeRate updateRate(Long id, Double newRate) {
        ExchangeRate rate = exchangeRateRepository.findById(id).orElse(null);
        if (rate != null) {
            rate.setRateToDOP(newRate);
            rate.setUpdatedAt(LocalDateTime.now());
            return exchangeRateRepository.save(rate);
        }
        return null;
    }
    
    public Double convertToDOP(Double amount, String fromCurrency) {
        ExchangeRate rate = getRateByCurrency(fromCurrency);
        if (rate != null && amount != null) {
            return amount * rate.getRateToDOP();
        }
        return amount;
    }
    
    public Double convertFromDOP(Double amountDOP, String toCurrency) {
        ExchangeRate rate = getRateByCurrency(toCurrency);
        if (rate != null && amountDOP != null) {
            return amountDOP / rate.getRateToDOP();
        }
        return amountDOP;
    }
    
    public void initializeDefaultRates() {
        if (exchangeRateRepository.count() == 0) {
            // USD
            ExchangeRate usd = new ExchangeRate();
            usd.setCurrencyCode("USD");
            usd.setCurrencyName("Dólar Americano");
            usd.setRateToDOP(62.0);
            usd.setSymbol("$");
            usd.setActive(true);
            usd.setUpdatedAt(LocalDateTime.now());
            exchangeRateRepository.save(usd);
            
            // EUR
            ExchangeRate eur = new ExchangeRate();
            eur.setCurrencyCode("EUR");
            eur.setCurrencyName("Euro");
            eur.setRateToDOP(67.0);
            eur.setSymbol("€");
            eur.setActive(true);
            eur.setUpdatedAt(LocalDateTime.now());
            exchangeRateRepository.save(eur);
            
            // GBP
            ExchangeRate gbp = new ExchangeRate();
            gbp.setCurrencyCode("GBP");
            gbp.setCurrencyName("Libra Esterlina");
            gbp.setRateToDOP(78.0);
            gbp.setSymbol("£");
            gbp.setActive(true);
            gbp.setUpdatedAt(LocalDateTime.now());
            exchangeRateRepository.save(gbp);
            
            // DOP
            ExchangeRate dop = new ExchangeRate();
            dop.setCurrencyCode("DOP");
            dop.setCurrencyName("Peso Dominicano");
            dop.setRateToDOP(1.0);
            dop.setSymbol("RD$");
            dop.setActive(true);
            dop.setUpdatedAt(LocalDateTime.now());
            exchangeRateRepository.save(dop);
        }
    }

    public void deleteRate(Long id) {
    exchangeRateRepository.deleteById(id);
}
}