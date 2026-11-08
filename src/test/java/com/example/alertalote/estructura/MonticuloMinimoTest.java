package com.example.alertalote.estructura;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MonticuloMinimoTest {

    @Test
    void extraeLosElementosEnOrden() {
        MonticuloMinimo<Integer> m = new MonticuloMinimo<>(Comparator.naturalOrder());
        for (int n : new int[]{9, 3, 7, 1, 8, 2, 5}) m.insertar(n);

        assertEquals(7, m.tamano());
        assertEquals(1, m.verMinimo());
        assertEquals(List.of(1, 2, 3, 5, 7, 8, 9), m.extraerTodosOrdenados());
        assertTrue(m.estaVacio());
    }

    @Test
    void ordenaIgualQueSortConDatosAleatorios() {
        Random r = new Random(42);
        MonticuloMinimo<Integer> m = new MonticuloMinimo<>(Comparator.naturalOrder());
        List<Integer> esperados = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            int n = r.nextInt(500);
            m.insertar(n);
            esperados.add(n);
        }
        esperados.sort(Comparator.naturalOrder());
        assertEquals(esperados, m.extraerTodosOrdenados());
    }

    @Test
    void fallaSiEstaVacio() {
        MonticuloMinimo<Integer> m = new MonticuloMinimo<>(Comparator.naturalOrder());
        assertThrows(NoSuchElementException.class, m::extraerMinimo);
        assertThrows(NoSuchElementException.class, m::verMinimo);
    }
}
