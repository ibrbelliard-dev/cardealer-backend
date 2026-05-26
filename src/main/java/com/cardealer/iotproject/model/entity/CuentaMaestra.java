package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas_maestras")
public class CuentaMaestra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id")
    private Integer cuentaId;

    @Column(name = "codigo", nullable = false, unique = true)
    private Integer codigo;

    @Column(name = "nombre_cuenta", nullable = false, length = 150)
    private String nombreCuenta;

    @Column(name = "tipo_cuenta", nullable = false)
    private String tipoCuenta;

    @Column(name = "naturaleza", nullable = false)
    private String naturaleza;

    @Column(name = "nivel_catalogo")
    private Integer nivelCatalogo = 1;

    @Column(name = "requiere_ncf")
    private Boolean requiereNcf = false;

    @Column(name = "afecta_itbis")
    private Boolean afectaItbis = false;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CuentaMaestra() {}

    public CuentaMaestra(Integer codigo, String nombreCuenta, String tipoCuenta, String naturaleza) {
        this.codigo = codigo;
        this.nombreCuenta = nombreCuenta;
        this.tipoCuenta = tipoCuenta;
        this.naturaleza = naturaleza;
    }

    // Getters and Setters
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