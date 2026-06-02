// src/main/java/com/cardealer/iotproject/model/dto/DgiiArchivoDTO.java
package com.cardealer.iotproject.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DgiiArchivoDTO {
    private Long archivoId;
    private String tipoArchivo;
    private String periodo;
    private String generadoEn;
    private String generadoPor;
    private String descargadoEn;
    private String descargadoPor;

    public DgiiArchivoDTO() {}

    public DgiiArchivoDTO(DgiiArchivoDTOBuilder builder) {
        this.archivoId = builder.archivoId;
        this.tipoArchivo = builder.tipoArchivo;
        this.periodo = builder.periodo;
        this.generadoEn = builder.generadoEn;
        this.generadoPor = builder.generadoPor;
        this.descargadoEn = builder.descargadoEn;
        this.descargadoPor = builder.descargadoPor;
    }

    // Getters y Setters
    public Long getArchivoId() { return archivoId; }
    public void setArchivoId(Long archivoId) { this.archivoId = archivoId; }

    public String getTipoArchivo() { return tipoArchivo; }
    public void setTipoArchivo(String tipoArchivo) { this.tipoArchivo = tipoArchivo; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public String getGeneradoEn() { return generadoEn; }
    public void setGeneradoEn(String generadoEn) { this.generadoEn = generadoEn; }

    public String getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(String generadoPor) { this.generadoPor = generadoPor; }

    public String getDescargadoEn() { return descargadoEn; }
    public void setDescargadoEn(String descargadoEn) { this.descargadoEn = descargadoEn; }

    public String getDescargadoPor() { return descargadoPor; }
    public void setDescargadoPor(String descargadoPor) { this.descargadoPor = descargadoPor; }

    // Builder Pattern
    public static class DgiiArchivoDTOBuilder {
        private Long archivoId;
        private String tipoArchivo;
        private String periodo;
        private String generadoEn;
        private String generadoPor;
        private String descargadoEn;
        private String descargadoPor;

        public DgiiArchivoDTOBuilder archivoId(Long archivoId) {
            this.archivoId = archivoId;
            return this;
        }

        public DgiiArchivoDTOBuilder tipoArchivo(String tipoArchivo) {
            this.tipoArchivo = tipoArchivo;
            return this;
        }

        public DgiiArchivoDTOBuilder periodo(String periodo) {
            this.periodo = periodo;
            return this;
        }

        public DgiiArchivoDTOBuilder generadoEn(LocalDateTime generadoEn) {
            if (generadoEn != null) {
                this.generadoEn = generadoEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            } else {
                this.generadoEn = "";
            }
            return this;
        }

        public DgiiArchivoDTOBuilder generadoPor(String generadoPor) {
            this.generadoPor = generadoPor;
            return this;
        }

        public DgiiArchivoDTOBuilder descargadoEn(LocalDateTime descargadoEn) {
            if (descargadoEn != null) {
                this.descargadoEn = descargadoEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            } else {
                this.descargadoEn = "";
            }
            return this;
        }

        public DgiiArchivoDTOBuilder descargadoPor(String descargadoPor) {
            this.descargadoPor = descargadoPor;
            return this;
        }

        public DgiiArchivoDTO build() {
            return new DgiiArchivoDTO(this);
        }
    }
}