package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.CuentaMaestra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaMaestraRepository extends JpaRepository<CuentaMaestra, Integer> {

    Optional<CuentaMaestra> findByCodigo(Integer codigo);

    List<CuentaMaestra> findByTipoCuenta(String tipoCuenta);

    List<CuentaMaestra> findByActivoTrue();

    List<CuentaMaestra> findByActivoTrueOrderByCodigo();

    @Query("SELECT c FROM CuentaMaestra c WHERE c.activo = true AND c.tipoCuenta = :tipoCuenta ORDER BY c.codigo")
    List<CuentaMaestra> findActivasByTipoCuenta(@Param("tipoCuenta") String tipoCuenta);

    boolean existsByCodigo(Integer codigo);
}