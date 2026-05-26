package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.dto.CatalogoCompletoDTO;
import com.cardealer.iotproject.model.entity.CuentaMaestra;
import com.cardealer.iotproject.model.entity.Subcuenta;
import com.cardealer.iotproject.accounting.exception.ResourceNotFoundException;
import com.cardealer.iotproject.repository.CuentaMaestraRepository;
import com.cardealer.iotproject.repository.SubcuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CuentaMaestraService {

    @Autowired
    private CuentaMaestraRepository cuentaMaestraRepository;

    @Autowired
    private SubcuentaRepository subcuentaRepository;

    @Transactional(readOnly = true)
    public List<CuentaMaestra> findAll() {
        return cuentaMaestraRepository.findByActivoTrueOrderByCodigo();
    }

    @Transactional(readOnly = true)
    public CuentaMaestra findById(Integer id) {
        return cuentaMaestraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta maestra no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public CuentaMaestra findByCodigo(Integer codigo) {
        return cuentaMaestraRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta maestra no encontrada con código: " + codigo));
    }

    @Transactional(readOnly = true)
    public List<CuentaMaestra> findByTipoCuenta(String tipoCuenta) {
        return cuentaMaestraRepository.findActivasByTipoCuenta(tipoCuenta);
    }

    @Transactional
    public CuentaMaestra create(CuentaMaestra cuentaMaestra) {
        if (cuentaMaestraRepository.existsByCodigo(cuentaMaestra.getCodigo())) {
            throw new RuntimeException("Ya existe una cuenta con el código: " + cuentaMaestra.getCodigo());
        }
        cuentaMaestra.setNivelCatalogo(1);
        cuentaMaestra.setActivo(true);
        cuentaMaestra.setCreatedAt(LocalDateTime.now());
        cuentaMaestra.setUpdatedAt(LocalDateTime.now());
        return cuentaMaestraRepository.save(cuentaMaestra);
    }

    @Transactional
    public CuentaMaestra update(Integer id, CuentaMaestra cuentaMaestraDetails) {
        CuentaMaestra cuentaMaestra = findById(id);
        
        cuentaMaestra.setNombreCuenta(cuentaMaestraDetails.getNombreCuenta());
        cuentaMaestra.setTipoCuenta(cuentaMaestraDetails.getTipoCuenta());
        cuentaMaestra.setNaturaleza(cuentaMaestraDetails.getNaturaleza());
        cuentaMaestra.setRequiereNcf(cuentaMaestraDetails.getRequiereNcf());
        cuentaMaestra.setAfectaItbis(cuentaMaestraDetails.getAfectaItbis());
        cuentaMaestra.setUpdatedAt(LocalDateTime.now());
        
        return cuentaMaestraRepository.save(cuentaMaestra);
    }

    @Transactional
    public void delete(Integer id) {
        CuentaMaestra cuentaMaestra = findById(id);
        cuentaMaestra.setActivo(false);
        cuentaMaestraRepository.save(cuentaMaestra);
    }

    @Transactional
    public void activate(Integer id) {
        CuentaMaestra cuentaMaestra = findById(id);
        cuentaMaestra.setActivo(true);
        cuentaMaestraRepository.save(cuentaMaestra);
    }

    @Transactional(readOnly = true)
    public List<CatalogoCompletoDTO> getCatalogoCompleto() {
        List<CatalogoCompletoDTO> catalogo = new ArrayList<>();
        List<CuentaMaestra> cuentas = cuentaMaestraRepository.findByActivoTrueOrderByCodigo();
        
        for (CuentaMaestra cuenta : cuentas) {
            List<Subcuenta> subcuentas = subcuentaRepository.findByCuentaMaestra_CuentaIdAndActivoTrue(cuenta.getCuentaId());
            
            if (subcuentas.isEmpty()) {
                CatalogoCompletoDTO dto = new CatalogoCompletoDTO();
                dto.setCodigoMaestro(cuenta.getCodigo());
                dto.setCuentaMaestra(cuenta.getNombreCuenta());
                dto.setTipoCuenta(cuenta.getTipoCuenta());
                dto.setNaturaleza(cuenta.getNaturaleza());
                dto.setCodigoCompleto(cuenta.getCodigo().toString());
                catalogo.add(dto);
            } else {
                for (Subcuenta sub : subcuentas) {
                    CatalogoCompletoDTO dto = new CatalogoCompletoDTO();
                    dto.setCodigoMaestro(cuenta.getCodigo());
                    dto.setCuentaMaestra(cuenta.getNombreCuenta());
                    dto.setTipoCuenta(cuenta.getTipoCuenta());
                    dto.setNaturaleza(cuenta.getNaturaleza());
                    dto.setCodigoSubcuenta(sub.getCodigo());
                    dto.setSubcuenta(sub.getNombreSub());
                    dto.setDescripcion(sub.getDescripcion());
                    dto.setNcfTipoAsociado(sub.getNcfTipoAsociado());
                    dto.setRequiereRetencion(sub.getRequiereRetencion());
                    dto.setTasaRetencionItbis(sub.getTasaRetencionItbis());
                    dto.setTasaRetencionIsr(sub.getTasaRetencionIsr());
                    dto.setCodigoCompleto(cuenta.getCodigo() + "-" + sub.getCodigo());
                    catalogo.add(dto);
                }
            }
        }
        return catalogo;
    }
}