package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subcuentas")
public class Subcuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcuenta_id")
    private Integer subcuentaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_maestra_id", nullable = false)
    private CuentaMaestra cuentaMaestra;

    @Column(name = "codigo", nullable = false)
    private Integer codigo;

    @Column(name = "nombre_sub", nullable = false, length = 150)
    private String nombreSub;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "ncf_tipo_asociado", length = 2)
    private String ncfTipoAsociado;

    @Column(name = "requiere_retencion")
    private Boolean requiereRetencion = false;

    @Column(name = "tasa_retencion_itbis")
    private Double tasaRetencionItbis = 0.00;

    @Column(name = "tasa_retencion_isr")
    private Double tasaRetencionIsr = 0.00;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Subcuenta() {}

    public Subcuenta(CuentaMaestra cuentaMaestra, Integer codigo, String nombreSub) {
        this.cuentaMaestra = cuentaMaestra;
        this.codigo = codigo;
        this.nombreSub = nombreSub;
    }

    // Getters and Setters
    public Integer getSubcuentaId() { return subcuentaId; }
    public void setSubcuentaId(Integer subcuentaId) { this.subcuentaId = subcuentaId; }

    public CuentaMaestra getCuentaMaestra() { return cuentaMaestra; }
    public void setCuentaMaestra(CuentaMaestra cuentaMaestra) { this.cuentaMaestra = cuentaMaestra; }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getNombreSub() { return nombreSub; }
    public void setNombreSub(String nombreSub) { this.nombreSub = nombreSub; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNcfTipoAsociado() { return ncfTipoAsociado; }
    public void setNcfTipoAsociado(String ncfTipoAsociado) { this.ncfTipoAsociado = ncfTipoAsociado; }

    public Boolean getRequiereRetencion() { return requiereRetencion; }
    public void setRequiereRetencion(Boolean requiereRetencion) { this.requiereRetencion = requiereRetencion; }

    public Double getTasaRetencionItbis() { return tasaRetencionItbis; }
    public void setTasaRetencionItbis(Double tasaRetencionItbis) { this.tasaRetencionItbis = tasaRetencionItbis; }

    public Double getTasaRetencionIsr() { return tasaRetencionIsr; }
    public void setTasaRetencionIsr(Double tasaRetencionIsr) { this.tasaRetencionIsr = tasaRetencionIsr; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}