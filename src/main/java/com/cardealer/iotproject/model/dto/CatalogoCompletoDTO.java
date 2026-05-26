package com.cardealer.iotproject.model.dto;

public class CatalogoCompletoDTO {

    private Integer codigoMaestro;
    private String cuentaMaestra;
    private String tipoCuenta;
    private String naturaleza;
    private Integer codigoSubcuenta;
    private String subcuenta;
    private String descripcion;
    private String ncfTipoAsociado;
    private Boolean requiereRetencion;
    private Double tasaRetencionItbis;
    private Double tasaRetencionIsr;
    private String codigoCompleto;

    public CatalogoCompletoDTO() {}

    public Integer getCodigoMaestro() { return codigoMaestro; }
    public void setCodigoMaestro(Integer codigoMaestro) { this.codigoMaestro = codigoMaestro; }

    public String getCuentaMaestra() { return cuentaMaestra; }
    public void setCuentaMaestra(String cuentaMaestra) { this.cuentaMaestra = cuentaMaestra; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public String getNaturaleza() { return naturaleza; }
    public void setNaturaleza(String naturaleza) { this.naturaleza = naturaleza; }

    public Integer getCodigoSubcuenta() { return codigoSubcuenta; }
    public void setCodigoSubcuenta(Integer codigoSubcuenta) { this.codigoSubcuenta = codigoSubcuenta; }

    public String getSubcuenta() { return subcuenta; }
    public void setSubcuenta(String subcuenta) { this.subcuenta = subcuenta; }

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

    public String getCodigoCompleto() { return codigoCompleto; }
    public void setCodigoCompleto(String codigoCompleto) { this.codigoCompleto = codigoCompleto; }
}