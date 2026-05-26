package com.cardealer.iotproject.model.dto;

import java.time.LocalDateTime;

public class CuentaMaestraDTO {

    private Integer cuentaId;
    private Integer codigo;
    private String nombreCuenta;
    private String tipoCuenta;
    private String naturaleza;
    private Integer nivelCatalogo;
    private Boolean requiereNcf;
    private Boolean afectaItbis;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CuentaMaestraDTO() {}

    public Integer getCuentaId() { return cuentaId; }
    public void setCuentaId(Integer cuentaId) { this.cuentaId = cuentaId; }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getNombreCuenta() { return nombreCuenta; }
    public void setNombreCuenta(String nombreCuenta) { this.nombreCuenta = nombreCuenta; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public String getNaturaleza() { return naturaleza; }
    public void setNaturaleza(String naturaleza) { this.naturaleza = naturaleza; }

    public Integer getNivelCatalogo() { return nivelCatalogo; }
    public void setNivelCatalogo(Integer nivelCatalogo) { this.nivelCatalogo = nivelCatalogo; }

    public Boolean getRequiereNcf() { return requiereNcf; }
    public void setRequiereNcf(Boolean requiereNcf) { this.requiereNcf = requiereNcf; }

    public Boolean getAfectaItbis() { return afectaItbis; }
    public void setAfectaItbis(Boolean afectaItbis) { this.afectaItbis = afectaItbis; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}