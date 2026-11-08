package com.example.alertalote.service;

import com.example.alertalote.dto.LoteRequest;
import com.example.alertalote.dto.ReporteVencimientos;
import com.example.alertalote.estructura.ColaVencimientos;
import com.example.alertalote.exception.RecursoNoEncontradoException;
import com.example.alertalote.exception.SolicitudInvalidaException;
import com.example.alertalote.model.Lote;
import com.example.alertalote.model.Negocio;
import com.example.alertalote.repository.AlertaRepository;
import com.example.alertalote.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final AlertaRepository alertaRepository;
    private final ProductoService productoService;
    private final NegocioService negocioService;
    private final Clock reloj;

    public LoteService(LoteRepository loteRepository, AlertaRepository alertaRepository,
                       ProductoService productoService, NegocioService negocioService, Clock reloj) {
        this.loteRepository = loteRepository;
        this.alertaRepository = alertaRepository;
        this.productoService = productoService;
        this.negocioService = negocioService;
        this.reloj = reloj;
    }

    @Transactional(readOnly = true)
    public List<Lote> listar(Long idNegocio) {
        if (idNegocio == null) return loteRepository.findAll();
        negocioService.obtener(idNegocio);
        return loteRepository.findByProductoNegocioIdNegocio(idNegocio);
    }

    @Transactional(readOnly = true)
    public List<Lote> listarPorProducto(String codigoBarras) {
        productoService.obtener(codigoBarras);
        return loteRepository.findByProductoCodigoBarras(codigoBarras);
    }

    @Transactional(readOnly = true)
    public Lote obtener(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un lote con id " + id));
    }

    @Transactional
    public Lote crear(LoteRequest datos) {
        Lote lote = new Lote();
        copiar(datos, lote);
        return loteRepository.save(lote);
    }

    @Transactional
    public Lote actualizar(Long id, LoteRequest datos) {
        Lote lote = obtener(id);
        copiar(datos, lote);
        return loteRepository.save(lote);
    }

    @Transactional
    public void eliminar(Long id) {
        Lote lote = obtener(id);
        alertaRepository.deleteByLoteId(id);
        loteRepository.delete(lote);
    }

    /** Descuenta unidades del lote (venta, merma o retiro). */
    @Transactional
    public Lote retirarUnidades(Long id, int cantidad) {
        Lote lote = obtener(id);
        if (cantidad > lote.getCantidad()) {
            throw new SolicitudInvalidaException("No puedes retirar " + cantidad
                    + " unidades: el lote solo tiene " + lote.getCantidad());
        }
        lote.setCantidad(lote.getCantidad() - cantidad);
        return loteRepository.save(lote);
    }

    /** Lotes con unidades que vencen entre hoy y dentro de {@code dias} días, del más urgente al menos urgente. */
    @Transactional(readOnly = true)
    public List<Lote> proximosAVencer(int dias, Long idNegocio) {
        validarDias(dias);
        LocalDate hoy = LocalDate.now(reloj);
        return filtrarPorNegocio(loteRepository.findProximosAVencer(hoy, hoy.plusDays(dias)), idNegocio);
    }

    /** Lotes con unidades cuya fecha ya pasó. */
    @Transactional(readOnly = true)
    public List<Lote> vencidos(Long idNegocio) {
        return filtrarPorNegocio(loteRepository.findVencidos(LocalDate.now(reloj)), idNegocio);
    }

    /** Reporte de un negocio ordenado con la cola de prioridad (montículo). */
    @Transactional(readOnly = true)
    public ReporteVencimientos reporte(Long idNegocio, int dias) {
        validarDias(dias);
        Negocio negocio = negocioService.obtener(idNegocio);
        LocalDate hoy = LocalDate.now(reloj);
        List<Lote> enRiesgo = filtrarPorNegocio(loteRepository.findEnRiesgo(hoy.plusDays(dias)), idNegocio);

        List<Lote> ordenados = new ColaVencimientos(enRiesgo).vaciarEnOrden();
        List<Lote> vencidos = ordenados.stream().filter(l -> l.getFechaVencimiento().isBefore(hoy)).toList();
        List<Lote> proximos = ordenados.stream().filter(l -> !l.getFechaVencimiento().isBefore(hoy)).toList();

        return new ReporteVencimientos(negocio.getIdNegocio(), negocio.getNombreTienda(), hoy, dias,
                vencidos, proximos, sumarUnidades(vencidos), sumarUnidades(proximos));
    }

    private void copiar(LoteRequest datos, Lote lote) {
        lote.setProducto(productoService.obtener(datos.codigoBarras().trim()));
        lote.setCantidad(datos.cantidad());
        lote.setFechaVencimiento(datos.fechaVencimiento());
        if (datos.ubicacion() != null && !datos.ubicacion().isBlank()) {
            lote.setUbicacion(datos.ubicacion().trim());
        }
    }

    private static List<Lote> filtrarPorNegocio(List<Lote> lotes, Long idNegocio) {
        if (idNegocio == null) return lotes;
        return lotes.stream()
                .filter(l -> l.getProducto() != null && l.getProducto().getNegocio() != null
                        && idNegocio.equals(l.getProducto().getNegocio().getIdNegocio()))
                .toList();
    }

    private static int sumarUnidades(List<Lote> lotes) {
        return lotes.stream().mapToInt(Lote::getCantidad).sum();
    }

    private static void validarDias(int dias) {
        if (dias < 0 || dias > 365) {
            throw new SolicitudInvalidaException("Los días deben estar entre 0 y 365");
        }
    }
}
