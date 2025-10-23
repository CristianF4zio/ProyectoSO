package main.estructuras;

public class ColaSimple<T> {
    private Nodo<T> frente;
    private Nodo<T> final_;
    private int tamaño;

    public ColaSimple() {
        this.frente = null;
        this.final_ = null;
        this.tamaño = 0;
    }

    public void encolar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);

        if (estaVacia()) {
            frente = nuevoNodo;
            final_ = nuevoNodo;
        } else {
            final_.siguiente = nuevoNodo;
            final_ = nuevoNodo;
        }
        tamaño++;
    }

    public T desencolar() {
        if (estaVacia()) {
            throw new RuntimeException("La cola está vacía");
        }

        T elemento = frente.dato;
        frente = frente.siguiente;

        if (frente == null) {
            final_ = null;
        }

        tamaño--;
        return elemento;
    }

    public T verFrente() {
        if (estaVacia()) {
            throw new RuntimeException("La cola está vacía");
        }
        return frente.dato;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamaño() {
        return tamaño;
    }

    public void limpiar() {
        frente = null;
        final_ = null;
        tamaño = 0;
    }

    public boolean contiene(T elemento) {
        Nodo<T> actual = frente;
        while (actual != null) {
            if (actual.dato != null && actual.dato.equals(elemento)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public ListaSimple<T> aLista() {
        ListaSimple<T> lista = new ListaSimple<>();
        Nodo<T> actual = frente;
        while (actual != null) {
            lista.agregar(actual.dato);
            actual = actual.siguiente;
        }
        return lista;
    }

    // Métodos de compatibilidad con Queue
    public void add(T elemento) {
        encolar(elemento);
    }

    public boolean offer(T elemento) {
        encolar(elemento);
        return true;
    }

    public T remove() {
        return desencolar();
    }

    public T poll() {
        if (estaVacia()) {
            return null;
        }
        return desencolar();
    }

    public T element() {
        return verFrente();
    }

    public T peek() {
        if (estaVacia()) {
            return null;
        }
        return verFrente();
    }

    public boolean isEmpty() {
        return estaVacia();
    }

    public int size() {
        return tamaño();
    }

    public void clear() {
        limpiar();
    }

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }
}
