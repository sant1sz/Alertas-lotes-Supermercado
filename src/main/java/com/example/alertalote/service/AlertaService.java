package com.example.alertalote.service;

import com.example.alertalote.dto.MensajeWhatsapp;
import com.example.alertalote.estructura.ColaVencimientos;
import com.example.alertalote.exception.RecursoNoEncontradoException;
import com.example.alertalote.model.Alerta;
import com.example.alertalote.model.Lote;
import com.example.alertalote.model.Negocio;
import com.example.alertalote.model.TipoAlerta;
import com.example.alertalote.repository.AlertaRepository;
import com.example.alertalote.repository.LoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private final AlertaRepository alertaRepository;
    private final LoteRepository loteRepository;
    private final NegocioService negocioService;
    private final Clock reloj;
    private final int diasAnticipacion;

    public AlertaService(AlertaRepository alertaRepository, LoteRepository loteRepository,
                         NegocioService negocioService, Clock reloj,
                         @Value("${alertas.dias-anticipacion:7}") int diasAnticipacion) {
        this.alertaRepository = alertaRepository;
        this.loteRepository = loteRepository;
        this.negocioService = negocioService;
        this.reloj = reloj;
        this.diasAnticipacion = diasAnticipacion;
    }

    /** Revisión automática diaria (horario en alertas.cron). */
    @Scheduled(cron = "${alertas.cron}", zone = "${alertas.zona-horaria}")
    public void revisionProgramada() {
        List<Alerta> nuevas = generarAlertas();
        log.info("Revisión de vencimientos: {} alertas nuevas", nuevas.size());
    }

    /**
     * Recorre los lotes en riesgo del más urgente al menos urgente (cola de prioridad)
     * y crea una alerta por lote, sin repetir alertas pendientes.
     * Si un lote que estaba "próximo" ya venció, la alerta vieja se cierra y se crea una de VENCIDO.
     */
    @Transactional
    public List<Alerta> generarAlertas() {
        LocalDate hoy = LocalDate.now(reloj);
        cerrarAlertasSinUnidades();

        ColaVencimientos cola = new ColaVencimientos(loteRepository.findEnRiesgo(hoy.plusDays(diasAnticipacion)));
        List<Alerta> nuevas = new ArrayList<>();

        while (!cola.estaVacia()) {
            Lote lote = cola.siguiente();
            TipoAlerta tipo = lote.getFechaVencimiento().isBefore(hoy) ? TipoAlerta.VENCIDO : TipoAlerta.PROXIMO_A_VENCER;

            if (tipo == TipoAlerta.VENCIDO) {
                cerrarPendientes(lote.getIdLote(), TipoAlerta.PROXIMO_A_VENCER);
            }
            if (alertaRepository.existsByLoteIdLoteAndTipoAndAtendidaFalse(lote.getIdLote(), tipo)) {
                continue;
            }

            Alerta alerta = new Alerta();
            alerta.setLote(lote);
            alerta.setTipo(tipo);
            alerta.setFechaVencimiento(lote.getFechaVencimiento());
            alerta.setDiasRestantes(ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento()));
            alerta.setMensaje(redactar(lote, tipo, alerta.getDiasRestantes()));
            alerta.setFechaGeneracion(LocalDateTime.now(reloj));
            nuevas.add(alertaRepository.save(alerta));
        }
        return nuevas;
    }

    @Transactional(readOnly = true)
    public List<Alerta> pendientes(Long idNegocio) {
        if (idNegocio == null) return alertaRepository.findByAtendidaFalseOrderByFechaVencimientoAsc();
        negocioService.obtener(idNegocio);
        return alertaRepository.findByLoteProductoNegocioIdNegocioAndAtendidaFalseOrderByFechaVencimientoAsc(idNegocio);
    }

    @Transactional
    public Alerta marcarAtendida(Long idAlerta) {
        Alerta alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una alerta con id " + idAlerta));
        alerta.setAtendida(true);
        return alertaRepository.save(alerta);
    }

    /** Arma un solo mensaje con las alertas pendientes del negocio y el enlace para abrirlo en WhatsApp. */
    @Transactional(readOnly = true)
    public MensajeWhatsapp mensajeWhatsapp(Long idNegocio) {
        Negocio negocio = negocioService.obtener(idNegocio);
        List<Alerta> alertas = pendientes(idNegocio);

        StringBuilder texto = new StringBuilder("*AlertaLote - ").append(negocio.getNombreTienda()).append("*\n");
        if (alertas.isEmpty()) {
            texto.append("No hay productos vencidos ni próximos a vencer. ✅");
        } else {
            texto.append("Productos que necesitan atención:\n");
            for (Alerta a : alertas) {
                texto.append(a.getTipo() == TipoAlerta.VENCIDO ? "🔴 " : "🟡 ").append(a.getMensaje()).append('\n');
            }
        }

        String telefono = normalizarTelefono(negocio.getTelefonoWhatsapp());
        String enlace = "https://wa.me/" + telefono + "?text="
                + URLEncoder.encode(texto.toString().trim(), StandardCharsets.UTF_8).replace("+", "%20");
        return new MensajeWhatsapp(telefono, alertas.size(), texto.toString().trim(), enlace);
    }

    /** wa.me necesita el número con indicativo y sin "+". Un celular colombiano de 10 dígitos recibe el 57. */
    static String normalizarTelefono(String telefono) {
        String digitos = telefono.replaceAll("\\D", "");
        if (digitos.length() == 10 && digitos.startsWith("3")) {
            return "57" + digitos;
        }
        return digitos;
    }

    static String redactar(Lote lote, TipoAlerta tipo, long diasRestantes) {
        String nombre = lote.getProducto() != null ? lote.getProducto().getNombre() : "Producto sin nombre";
        String base = nombre + " (lote " + lote.getIdLote() + ", " + lote.getCantidad() + " unid., " + lote.getUbicacion() + ")";
        if (tipo == TipoAlerta.VENCIDO) {
            long dias = -diasRestantes;
            return base + ": venció hace " + dias + (dias == 1 ? " día" : " días") + ". Retíralo de la venta.";
        }
        if (diasRestantes == 0) return base + ": vence HOY.";
        return base + ": vence en " + diasRestantes + (diasRestantes == 1 ? " día" : " días") + ".";
    }

    private void cerrarPendientes(Long idLote, TipoAlerta tipo) {
        alertaRepository.findByLoteIdLoteAndTipoAndAtendidaFalse(idLote, tipo)
                .forEach(a -> a.setAtendida(true));
    }

    /** Si el lote ya no tiene unidades (se vendió o se retiró), su alerta deja de tener sentido. */
    private void cerrarAlertasSinUnidades() {
        alertaRepository.findByAtendidaFalseOrderByFechaVencimientoAsc().stream()
                .filter(a -> a.getLote().getCantidad() <= 0)
                .forEach(a -> a.setAtendida(true));
    }
}
