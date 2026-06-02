// src/main/java/com/cardealer/iotproject/model/entity/DgiiArchivo.java
package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dgii_archivos", indexes = {
    @Index(name = "idx_tipo_periodo", columnList = "tipo_archivo, periodo"),
    @Index(name = "idx_generado_en", columnList = "generado_en")
})
public class DgiiArchivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archivo_id")
    private Long archivoId;

    @Column(name = "tipo_archivo", nullable = false, length = 10)
    private String tipoArchivo; // "606", "607", "IR-17", "IT-1", "IR-2", "ACT"

    @Column(name = "periodo", nullable = false, length = 7)
    private String periodo; // "YYYY-MM" para mensuales, "YYYY" para anuales

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido; // Contenido del archivo en formato CSV/TXT

    @Column(name = "ruta_archivo", length = 500)
    private String rutaArchivo;

    @Column(name = "generado_en")
    private LocalDateTime generadoEn;

    @Column(name = "generado_por", length = 100)
    private String generadoPor;

    @Column(name = "descargado_en")
    private LocalDateTime descargadoEn;

    @Column(name = "descargado_por", length = 100)
    private String descargadoPor;

    @PrePersist
    protected void onCreate() {
        generadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getArchivoId() { return archivoId; }
    public void setArchivoId(Long archivoId) { this.archivoId = archivoId; }

    public String getTipoArchivo() { return tipoArchivo; }
    public void setTipoArchivo(String tipoArchivo) { this.tipoArchivo = tipoArchivo; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public LocalDateTime getGeneradoEn() { return generadoEn; }
    public void setGeneradoEn(LocalDateTime generadoEn) { this.generadoEn = generadoEn; }

    public String getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(String generadoPor) { this.generadoPor = generadoPor; }

    public LocalDateTime getDescargadoEn() { return descargadoEn; }
    public void setDescargadoEn(LocalDateTime descargadoEn) { this.descargadoEn = descargadoEn; }

    public String getDescargadoPor() { return descargadoPor; }
    public void setDescargadoPor(String descargadoPor) { this.descargadoPor = descargadoPor; }
}