package com.example.alertalote.service;

import com.example.alertalote.model.Lote;
import com.example.alertalote.model.Producto;
import com.example.alertalote.model.TipoAlerta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertaServiceTest {

    @Test
    void agregaIndicativoDeColombiaACelularesDeDiezDigitos() {
        assertEquals("573001234567", AlertaService.normalizarTelefono("300 123 4567"));
        assertEquals("573001234567", AlertaService.normalizarTelefono("+57 300 123 4567"));
        assertEquals("5491122334455", AlertaService.normalizarTelefono("+54 9 11 2233 4455"));
    }

    @Test
    void redactaMensajesSegunElTipo() {
        Producto p = new Producto();
        p.setNombre("Yogur");
        Lote l = new Lote();
        l.setIdLote(4L);
        l.setProducto(p);
        l.setCantidad(12);

        assertTrue(AlertaService.redactar(l, TipoAlerta.PROXIMO_A_VENCER, 3).endsWith("vence en 3 días."));
        assertTrue(AlertaService.redactar(l, TipoAlerta.PROXIMO_A_VENCER, 1).endsWith("vence en 1 día."));
        assertTrue(AlertaService.redactar(l, TipoAlerta.PROXIMO_A_VENCER, 0).endsWith("vence HOY."));
        assertTrue(AlertaService.redactar(l, TipoAlerta.VENCIDO, -2).contains("venció hace 2 días"));
        assertTrue(AlertaService.redactar(l, TipoAlerta.VENCIDO, -2).startsWith("Yogur (lote 4, 12 unid., Estantería)"));
    }
}
