package com.example.alertalote.repository;

import com.example.alertalote.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    List<Producto> findByNegocioIdNegocio(Long idNegocio);

    boolean existsByNegocioIdNegocio(Long idNegocio);
}
