package com.example.alertalote.estructura;

import com.example.alertalote.model.Lote;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColaVencimientosTest {

    private static Lote lote(long id, String fecha, int cantidad) {
        Lote l = new Lote();
        l.setIdLote(id);
        l.setFechaVencimiento(LocalDate.parse(fecha));
        l.setCantidad(cantidad);
        return l;
    }

    @Test
    void saleSiempreElQueVenceAntes() {
        ColaVencimientos cola = new ColaVencimientos(List.of(
                lote(1, "2026-12-20", 5),
                lote(2, "2026-11-10", 5),
                lote(3, "2026-11-30", 5)));

        assertEquals(2L, cola.siguiente().getIdLote());
        assertEquals(3L, cola.siguiente().getIdLote());
        assertEquals(1L, cola.siguiente().getIdLote());
    }

    @Test
    void conLaMismaFechaSalePrimeroElDeMasUnidades() {
        ColaVencimientos cola = new ColaVencimientos(List.of(
                lote(1, "2026-11-15", 3),
                lote(2, "2026-11-15", 40),
                lote(3, "2026-11-15", 10)));

        List<Long> orden = cola.vaciarEnOrden().stream().map(Lote::getIdLote).toList();
        assertEquals(List.of(2L, 3L, 1L), orden);
    }
}
