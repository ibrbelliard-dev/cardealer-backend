// src/main/java/com/cardealer/iotproject/model/entity/Permiso.java
package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "permisos")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String modulo;

    @Column(nullable = false, length = 100)
    private String recurso;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getNombre() { 
        return nombre; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public String getModulo() { 
        return modulo; 
    }
    
    public void setModulo(String modulo) { 
        this.modulo = modulo; 
    }

    public String getRecurso() { 
        return recurso; 
    }
    
    public void setRecurso(String recurso) { 
        this.recurso = recurso; 
    }

    public String getAccion() { 
        return accion; 
    }
    
    public void setAccion(String accion) { 
        this.accion = accion; 
    }

    public String getDescripcion() { 
        return descripcion; 
    }
    
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permiso permiso = (Permiso) o;
        return Objects.equals(id, permiso.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}