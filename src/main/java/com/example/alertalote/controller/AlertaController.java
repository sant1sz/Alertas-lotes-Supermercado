package com.example.alertalote.controller;

import com.example.alertalote.dto.MensajeWhatsapp;
import com.example.alertalote.model.Alerta;
import com.example.alertalote.service.AlertaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    /** Alertas sin atender, de la más urgente a la menos urgente. Filtra por negocio con ?negocio=1 */
    @GetMapping
    public List<Alerta> pendientes(@RequestParam(name = "negocio", required = false) Long idNegocio) {
        return alertaService.pendientes(idNegocio);
    }

    /** Ejecuta la revisión ahora mismo (además de la automática diaria) y devuelve las alertas nuevas. */
    @PostMapping("/generar")
    public List<Alerta> generar() {
        return alertaService.generarAlertas();
    }

    @PatchMapping("/{id}/atendida")
    public Alerta marcarAtendida(@PathVariable Long id) {
        return alertaService.marcarAtendida(id);
    }

    /** Mensaje con las alertas pendientes del negocio y enlace wa.me para enviarlo por WhatsApp. */
    @GetMapping("/whatsapp/{idNegocio}")
    public MensajeWhatsapp whatsapp(@PathVariable Long idNegocio) {
        return alertaService.mensajeWhatsapp(idNegocio);
    }
}
