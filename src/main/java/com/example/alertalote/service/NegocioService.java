package com.example.alertalote.service;

import com.example.alertalote.dto.NegocioRequest;
import com.example.alertalote.exception.ConflictoException;
import com.example.alertalote.exception.RecursoNoEncontradoException;
import com.example.alertalote.model.Negocio;
import com.example.alertalote.repository.NegocioRepository;
import com.example.alertalote.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NegocioService {

    private final NegocioRepository negocioRepository;
    private final ProductoRepository productoRepository;

    public NegocioService(NegocioRepository negocioRepository, ProductoRepository productoRepository) {
        this.negocioRepository = negocioRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Negocio> listar() {
        return negocioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Negocio obtener(Long id) {
        return negocioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un negocio con id " + id));
    }

    @Transactional
    public Negocio crear(NegocioRequest datos) {
        Negocio negocio = new Negocio();
        copiar(datos, negocio);
        return negocioRepository.save(negocio);
    }

    @Transactional
    public Negocio actualizar(Long id, NegocioRequest datos) {
        Negocio negocio = obtener(id);
        copiar(datos, negocio);
        return negocioRepository.save(negocio);
    }

    @Transactional
    public void eliminar(Long id) {
        Negocio negocio = obtener(id);
        if (productoRepository.existsByNegocioIdNegocio(id)) {
            throw new ConflictoException("No se puede eliminar el negocio porque tiene productos registrados");
        }
        negocioRepository.delete(negocio);
    }

    private void copiar(NegocioRequest datos, Negocio negocio) {
        negocio.setNombreTienda(datos.nombreTienda().trim());
        negocio.setTelefonoWhatsapp(datos.telefonoWhatsapp().replace(" ", ""));
    }
}
