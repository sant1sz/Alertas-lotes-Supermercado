package com.example.alertalote.estructura;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Montículo binario mínimo (min-heap) implementado sobre un arreglo dinámico.
 * <p>
 * Se usa como cola de prioridad: el elemento "más pequeño" según el comparador
 * siempre está en la raíz. Insertar y extraer cuestan O(log n); ver el mínimo, O(1).
 * <p>
 * Para el nodo en la posición i: padre = (i - 1) / 2, hijo izquierdo = 2i + 1, hijo derecho = 2i + 2.
 */
public class MonticuloMinimo<T> {

    private final List<T> datos = new ArrayList<>();
    private final Comparator<? super T> comparador;

    public MonticuloMinimo(Comparator<? super T> comparador) {
        this.comparador = comparador;
    }

    /** Inserta un elemento y lo sube hasta su posición correcta. O(log n). */
    public void insertar(T elemento) {
        datos.add(elemento);
        subir(datos.size() - 1);
    }

    /** Devuelve el elemento de mayor prioridad sin quitarlo. O(1). */
    public T verMinimo() {
        if (estaVacio()) throw new NoSuchElementException("El montículo está vacío");
        return datos.get(0);
    }

    /** Quita y devuelve el elemento de mayor prioridad. O(log n). */
    public T extraerMinimo() {
        if (estaVacio()) throw new NoSuchElementException("El montículo está vacío");
        T minimo = datos.get(0);
        T ultimo = datos.remove(datos.size() - 1);
        if (!datos.isEmpty()) {
            datos.set(0, ultimo);
            bajar(0);
        }
        return minimo;
    }

    /** Vacía el montículo devolviendo los elementos ordenados de mayor a menor prioridad (heapsort). */
    public List<T> extraerTodosOrdenados() {
        List<T> ordenados = new ArrayList<>(datos.size());
        while (!estaVacio()) ordenados.add(extraerMinimo());
        return ordenados;
    }

    public int tamano() { return datos.size(); }

    public boolean estaVacio() { return datos.isEmpty(); }

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (comparador.compare(datos.get(i), datos.get(padre)) >= 0) break;
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajar(int i) {
        int n = datos.size();
        while (true) {
            int izq = 2 * i + 1;
            int der = izq + 1;
            int menor = i;
            if (izq < n && comparador.compare(datos.get(izq), datos.get(menor)) < 0) menor = izq;
            if (der < n && comparador.compare(datos.get(der), datos.get(menor)) < 0) menor = der;
            if (menor == i) return;
            intercambiar(i, menor);
            i = menor;
        }
    }

    private void intercambiar(int a, int b) {
        T tmp = datos.get(a);
        datos.set(a, datos.get(b));
        datos.set(b, tmp);
    }
}
