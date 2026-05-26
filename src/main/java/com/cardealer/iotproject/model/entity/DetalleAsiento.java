// src/main/java/com/cardealer/iotproject/model/entity/DetalleAsiento.java
package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_asiento")
public class DetalleAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_id")
    private Integer detalleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asiento_id", nullable = false)
    private AsientoContable asiento;

    @Column(name = "cuenta_codigo", length = 50)
    private String cuentaCodigo;

    @Column(name = "cuenta_nombre", length = 150)
    private String cuentaNombre;

    @Column(name = "debe")
    private Double debe = 0.0;

    @Column(name = "haber")
    private Double haber = 0.0;

    // Getters y Setters
    public Integer getDetalleId() { return detalleId; }
    public void setDetalleId(Integer detalleId) { this.detalleId = detalleId; }

    public AsientoContable getAsiento() { return asiento; }
    public void setAsiento(AsientoContable asiento) { this.asiento = asiento; }

    public String getCuentaCodigo() { return cuentaCodigo; }
    public void setCuentaCodigo(String cuentaCodigo) { this.cuentaCodigo = cuentaCodigo; }

    public String getCuentaNombre() { return cuentaNombre; }
    public void setCuentaNombre(String cuentaNombre) { this.cuentaNombre = cuentaNombre; }

    
    public Double getDebe() { return debe; }
    public void setDebe(Double debe) { this.debe = debe; }

    public Double getHaber() { return haber; }
    public void setHaber(Double haber) { this.haber = haber; }
}