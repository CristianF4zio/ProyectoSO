package main.estructuras;

public class ListaSimple<T> {
    private Object[] elementos;
    private int tamaño;
    private int capacidad;

    public ListaSimple() {
        this.capacidad = 10;
        this.elementos = new Object[capacidad];
        this.tamaño = 0;
    }

    public ListaSimple(int capacidadInicial) {
        this.capacidad = capacidadInicial;
        this.elementos = new Object[capacidad];
        this.tamaño = 0;
    }

    public void agregar(T elemento) {
        if (tamaño >= capacidad) {
            expandirCapacidad();
        }
        elementos[tamaño] = elemento;
        tamaño++;
    }

    public void agregar(int indice, T elemento) {
        if (indice < 0 || indice > tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }

        if (tamaño >= capacidad) {
            expandirCapacidad();
        }

        for (int i = tamaño; i > indice; i--) {
            elementos[i] = elementos[i - 1];
        }
        elementos[indice] = elemento;
        tamaño++;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return (T) elementos[indice];
    }

    public T remover(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }

        T elemento = (T) elementos[indice];
        for (int i = indice; i < tamaño - 1; i++) {
            elementos[i] = elementos[i + 1];
        }
        tamaño--;
        return elemento;
    }

    public boolean remover(T elemento) {
        for (int i = 0; i < tamaño; i++) {
            if (elementos[i] != null && elementos[i].equals(elemento)) {
                remover(i);
                return true;
            }
        }
        return false;
    }

    public boolean contiene(T elemento) {
        for (int i = 0; i < tamaño; i++) {
            if (elementos[i] != null && elementos[i].equals(elemento)) {
                return true;
            }
        }
        return false;
    }

    public int tamaño() {
        return tamaño;
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }

    public void limpiar() {
        for (int i = 0; i < tamaño; i++) {
            elementos[i] = null;
        }
        tamaño = 0;
    }

    public int indiceDe(T elemento) {
        for (int i = 0; i < tamaño; i++) {
            if (elementos[i] != null && elementos[i].equals(elemento)) {
                return i;
            }
        }
        return -1;
    }

    private void expandirCapacidad() {
        capacidad *= 2;
        Object[] nuevoArray = new Object[capacidad];
        for (int i = 0; i < tamaño; i++) {
            nuevoArray[i] = elementos[i];
        }
        elementos = nuevoArray;
    }

    public T[] aArray(T[] array) {
        if (array.length < tamaño) {
            array = (T[]) new Object[tamaño];
        }
        for (int i = 0; i < tamaño; i++) {
            array[i] = (T) elementos[i];
        }
        return array;
    }

    public boolean isEmpty() {
        return estaVacia();
    }

    public int size() {
        return tamaño();
    }

    public void add(T elemento) {
        agregar(elemento);
    }

    public void add(int indice, T elemento) {
        agregar(indice, elemento);
    }

    public T get(int indice) {
        return obtener(indice);
    }

    public T remove(int indice) {
        return remover(indice);
    }

    public boolean remove(T elemento) {
        return remover(elemento);
    }

    public boolean contains(T elemento) {
        return contiene(elemento);
    }

    public void clear() {
        limpiar();
    }

    public int indexOf(T elemento) {
        return indiceDe(elemento);
    }
}
