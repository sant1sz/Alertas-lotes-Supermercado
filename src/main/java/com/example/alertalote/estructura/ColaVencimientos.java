package com.example.alertalote.estructura;

import com.example.alertalote.model.Lote;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Cola de prioridad de lotes: sale primero el que vence antes.
 * Si dos lotes vencen el mismo día, sale primero el que tiene más unidades (más pérdida posible).
 */
public class ColaVencimientos {

    public static final Comparator<Lote> POR_URGENCIA = Comparator
            .comparing(Lote::getFechaVencimiento)
            .thenComparing(Lote::getCantidad, Comparator.reverseOrder());

    private final MonticuloMinimo<Lote> monticulo = new MonticuloMinimo<>(POR_URGENCIA);

    public ColaVencimientos() { }

    public ColaVencimientos(Collection<Lote> lotes) {
        lotes.forEach(this::agregar);
    }

    public void agregar(Lote lote) { monticulo.insertar(lote); }

    public Lote siguiente() { return monticulo.extraerMinimo(); }

    public Lote verSiguiente() { return monticulo.verMinimo(); }

    public boolean estaVacia() { return monticulo.estaVacio(); }

    public int tamano() { return monticulo.tamano(); }

    /** Devuelve todos los lotes del más urgente al menos urgente y deja la cola vacía. */
    public List<Lote> vaciarEnOrden() { return monticulo.extraerTodosOrdenados(); }
}
