package com.example.alertalote.service;

import com.example.alertalote.dto.ProductoActualizarRequest;
import com.example.alertalote.dto.ProductoRequest;
import com.example.alertalote.exception.ConflictoException;
import com.example.alertalote.exception.RecursoNoEncontradoException;
import com.example.alertalote.model.Producto;
import com.example.alertalote.repository.LoteRepository;
import com.example.alertalote.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final NegocioService negocioService;

    public ProductoService(ProductoRepository productoRepository, LoteRepository loteRepository,
                           NegocioService negocioService) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.negocioService = negocioService;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar(Long idNegocio) {
        if (idNegocio == null) return productoRepository.findAll();
        negocioService.obtener(idNegocio);
        return productoRepository.findByNegocioIdNegocio(idNegocio);
    }

    @Transactional(readOnly = true)
    public Producto obtener(String codigoBarras) {
        return productoRepository.findById(codigoBarras)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un producto con código de barras " + codigoBarras));
    }

    @Transactional
    public Producto crear(ProductoRequest datos) {
        String codigo = datos.codigoBarras().trim();
        if (productoRepository.existsById(codigo)) {
            throw new ConflictoException("Ya existe un producto con código de barras " + codigo);
        }
        Producto producto = new Producto();
        producto.setCodigoBarras(codigo);
        producto.setNombre(datos.nombre().trim());
        producto.setCategoria(datos.categoria());
        producto.setNegocio(negocioService.obtener(datos.idNegocio()));
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(String codigoBarras, ProductoActualizarRequest datos) {
        Producto producto = obtener(codigoBarras);
        producto.setNombre(datos.nombre().trim());
        producto.setCategoria(datos.categoria());
        producto.setNegocio(negocioService.obtener(datos.idNegocio()));
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(String codigoBarras) {
        Producto producto = obtener(codigoBarras);
        if (loteRepository.existsByProductoCodigoBarras(codigoBarras)) {
            throw new ConflictoException("No se puede eliminar el producto porque tiene lotes registrados");
        }
        productoRepository.delete(producto);
    }
}
