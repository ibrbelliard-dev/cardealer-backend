package com.cardealer.iotproject.model.dto;

import java.time.LocalDateTime;

public class SubcuentaDTO {

    private Integer subcuentaId;
    private Integer cuentaMaestraId;
    private Integer codigoMaestra;
    private String nombreCuentaMaestra;
    private Integer codigo;
    private String nombreSub;
    private String descripcion;
    private String ncfTipoAsociado;
    private Boolean requiereRetencion;
    private Double tasaRetencionItbis;
    private Double tasaRetencionIsr;
    private Boolean activo;
    private LocalDateTime createdAt;

    public SubcuentaDTO() {}

    public Integer getSubcuentaId() { return subcuentaId; }
    public void setSubcuentaId(Integer subcuentaId) { this.subcuentaId = subcuentaId; }

    public Integer getCuentaMaestraId() { return cuentaMaestraId; }
    public void setCuentaMaestraId(Integer cuentaMaestraId) { this.cuentaMaestraId = cuentaMaestraId; }

    public Integer getCodigoMaestra() { return codigoMaestra; }
    public void setCodigoMaestra(Integer codigoMaestra) { this.codigoMaestra = codigoMaestra; }

    public String getNombreCuentaMaestra() { return nombreCuentaMaestra; }
    public void setNombreCuentaMaestra(String nombreCuentaMaestra) { this.nombreCuentaMaestra = nombreCuentaMaestra; }

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