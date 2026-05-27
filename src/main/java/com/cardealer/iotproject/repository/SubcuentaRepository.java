package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Subcuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SubcuentaRepository extends JpaRepository<Subcuenta, Integer> {

    List<Subcuenta> findByCuentaMaestra_CuentaId(Integer cuentaMaestraId);

    List<Subcuenta> findByCuentaMaestra_CuentaIdAndActivoTrue(Integer cuentaMaestraId);

    List<Subcuenta> findByActivoTrue();

    Optional<Subcuenta> findByCuentaMaestra_CodigoAndCodigo(Integer cuentaMaestraCodigo, Integer subcuentaCodigo);

    @Query("SELECT s FROM Subcuenta s WHERE s.cuentaMaestra.codigo = :codigoMaestra AND s.activo = true ORDER BY s.codigo")
    List<Subcuenta> findSubcuentasByCuentaMaestraCodigo(@Param("codigoMaestra") Integer codigoMaestra);

    boolean existsByCuentaMaestra_CodigoAndCodigo(Integer cuentaMaestraCodigo, Integer codigo);

    long countByActivoTrue();
}