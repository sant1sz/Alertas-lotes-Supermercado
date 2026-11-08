package com.example.alertalote.controller;

import com.example.alertalote.dto.LoteRequest;
import com.example.alertalote.dto.MovimientoRequest;
import com.example.alertalote.model.Lote;
import com.example.alertalote.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    /** Todos los lotes, o solo los de un negocio con ?negocio=1 */
    @GetMapping
    public List<Lote> listar(@RequestParam(name = "negocio", required = false) Long idNegocio) {
        return loteService.listar(idNegocio);
    }

    @GetMapping("/{id}")
    public Lote obtener(@PathVariable Long id) {
        return loteService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lote crear(@Valid @RequestBody LoteRequest datos) {
        return loteService.crear(datos);
    }

    @PutMapping("/{id}")
    public Lote actualizar(@PathVariable Long id, @Valid @RequestBody LoteRequest datos) {
        return loteService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        loteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Descuenta unidades del lote (venta, merma o retiro). */
    @PostMapping("/{id}/retiros")
    public Lote retirar(@PathVariable Long id, @Valid @RequestBody MovimientoRequest movimiento) {
        return loteService.retirarUnidades(id, movimiento.cantidad());
    }

    /** Lotes con unidades que vencen entre hoy y dentro de ?dias=N días. */
    @GetMapping("/proximos-a-vencer")
    public List<Lote> proximosAVencer(@RequestParam(defaultValue = "7") int dias,
                                      @RequestParam(name = "negocio", required = false) Long idNegocio) {
        return loteService.proximosAVencer(dias, idNegocio);
    }

    /** Lotes con unidades cuya fecha de vencimiento ya pasó. */
    @GetMapping("/vencidos")
    public List<Lote> vencidos(@RequestParam(name = "negocio", required = false) Long idNegocio) {
        return loteService.vencidos(idNegocio);
    }
}
