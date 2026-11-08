package com.example.alertalote.controller;

import com.example.alertalote.dto.ProductoActualizarRequest;
import com.example.alertalote.dto.ProductoRequest;
import com.example.alertalote.model.Lote;
import com.example.alertalote.model.Producto;
import com.example.alertalote.service.LoteService;
import com.example.alertalote.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final LoteService loteService;

    public ProductoController(ProductoService productoService, LoteService loteService) {
        this.productoService = productoService;
        this.loteService = loteService;
    }

    /** Todos los productos, o solo los de un negocio con ?negocio=1 */
    @GetMapping
    public List<Producto> listar(@RequestParam(name = "negocio", required = false) Long idNegocio) {
        return productoService.listar(idNegocio);
    }

    @GetMapping("/{codigoBarras}")
    public Producto obtener(@PathVariable String codigoBarras) {
        return productoService.obtener(codigoBarras);
    }

    @GetMapping("/{codigoBarras}/lotes")
    public List<Lote> lotesDelProducto(@PathVariable String codigoBarras) {
        return loteService.listarPorProducto(codigoBarras);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody ProductoRequest datos) {
        return productoService.crear(datos);
    }

    @PutMapping("/{codigoBarras}")
    public Producto actualizar(@PathVariable String codigoBarras,
                               @Valid @RequestBody ProductoActualizarRequest datos) {
        return productoService.actualizar(codigoBarras, datos);
    }

    @DeleteMapping("/{codigoBarras}")
    public ResponseEntity<Void> eliminar(@PathVariable String codigoBarras) {
        productoService.eliminar(codigoBarras);
        return ResponseEntity.noContent().build();
    }
}
