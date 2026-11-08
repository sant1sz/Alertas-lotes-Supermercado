package com.example.alertalote.controller;

import com.example.alertalote.dto.NegocioRequest;
import com.example.alertalote.dto.ReporteVencimientos;
import com.example.alertalote.model.Negocio;
import com.example.alertalote.service.LoteService;
import com.example.alertalote.service.NegocioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    private final NegocioService negocioService;
    private final LoteService loteService;

    public NegocioController(NegocioService negocioService, LoteService loteService) {
        this.negocioService = negocioService;
        this.loteService = loteService;
    }

    @GetMapping
    public List<Negocio> listar() {
        return negocioService.listar();
    }

    @GetMapping("/{id}")
    public Negocio obtener(@PathVariable Long id) {
        return negocioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Negocio crear(@Valid @RequestBody NegocioRequest datos) {
        return negocioService.crear(datos);
    }

    @PutMapping("/{id}")
    public Negocio actualizar(@PathVariable Long id, @Valid @RequestBody NegocioRequest datos) {
        return negocioService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        negocioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Lotes vencidos y próximos a vencer del negocio, del más urgente al menos urgente. */
    @GetMapping("/{id}/reporte")
    public ReporteVencimientos reporte(@PathVariable Long id,
                                       @RequestParam(defaultValue = "7") int dias) {
        return loteService.reporte(id, dias);
    }
}
