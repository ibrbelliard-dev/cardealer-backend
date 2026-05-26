package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.CuentaMaestra;
import com.cardealer.iotproject.model.entity.Subcuenta;
import com.cardealer.iotproject.accounting.exception.ResourceNotFoundException;
import com.cardealer.iotproject.repository.CuentaMaestraRepository;
import com.cardealer.iotproject.repository.SubcuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubcuentaService {

    @Autowired
    private SubcuentaRepository subcuentaRepository;

    @Autowired
    private CuentaMaestraRepository cuentaMaestraRepository;

    @Transactional(readOnly = true)
    public List<Subcuenta> findAll() {
        return subcuentaRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Subcuenta findById(Integer id) {
        return subcuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcuenta no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Subcuenta> findByCuentaMaestra(Integer cuentaMaestraId) {
        return subcuentaRepository.findByCuentaMaestra_CuentaIdAndActivoTrue(cuentaMaestraId);
    }

    @Transactional(readOnly = true)
    public List<Subcuenta> findByCuentaMaestraCodigo(Integer codigoMaestra) {
        return subcuentaRepository.findSubcuentasByCuentaMaestraCodigo(codigoMaestra);
    }

    @Transactional
    public Subcuenta create(Subcuenta subcuenta, Integer cuentaMaestraId) {
        CuentaMaestra cuentaMaestra = cuentaMaestraRepository.findById(cuentaMaestraId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta maestra no encontrada con id: " + cuentaMaestraId));
        
        if (subcuentaRepository.existsByCuentaMaestra_CodigoAndCodigo(cuentaMaestra.getCodigo(), subcuenta.getCodigo())) {
            throw new RuntimeException("Ya existe una subcuenta con el código " + subcuenta.getCodigo() + 
                    " para la cuenta maestra " + cuentaMaestra.getCodigo());
        }
        
        subcuenta.setCuentaMaestra(cuentaMaestra);
        subcuenta.setActivo(true);
        return subcuentaRepository.save(subcuenta);
    }

    @Transactional
    public Subcuenta update(Integer id, Subcuenta subcuentaDetails) {
        Subcuenta subcuenta = findById(id);
        
        subcuenta.setNombreSub(subcuentaDetails.getNombreSub());
        subcuenta.setDescripcion(subcuentaDetails.getDescripcion());
        subcuenta.setNcfTipoAsociado(subcuentaDetails.getNcfTipoAsociado());
        subcuenta.setRequiereRetencion(subcuentaDetails.getRequiereRetencion());
        subcuenta.setTasaRetencionItbis(subcuentaDetails.getTasaRetencionItbis());
        subcuenta.setTasaRetencionIsr(subcuentaDetails.getTasaRetencionIsr());
        
        return subcuentaRepository.save(subcuenta);
    }

    @Transactional
    public void delete(Integer id) {
        Subcuenta subcuenta = findById(id);
        subcuenta.setActivo(false);
        subcuentaRepository.save(subcuenta);
    }

    @Transactional
    public void activate(Integer id) {
        Subcuenta subcuenta = findById(id);
        subcuenta.setActivo(true);
        subcuentaRepository.save(subcuenta);
    }
}