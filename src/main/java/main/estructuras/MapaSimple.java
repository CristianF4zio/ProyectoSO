package main.estructuras;

public class MapaSimple<K, V> {
    private static final int CAPACIDAD_INICIAL = 16;
    private static final double FACTOR_CARGA = 0.75;

    private Entrada<K, V>[] tabla;
    private int tamaño;
    private int capacidad;

    public MapaSimple() {
        this.capacidad = CAPACIDAD_INICIAL;
        this.tabla = new Entrada[capacidad];
        this.tamaño = 0;
    }

    public MapaSimple(int capacidadInicial) {
        this.capacidad = capacidadInicial;
        this.tabla = new Entrada[capacidad];
        this.tamaño = 0;
    }

    public void poner(K clave, V valor) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser null");
        }

        if (tamaño >= capacidad * FACTOR_CARGA) {
            redimensionar();
        }

        int indice = hash(clave) % capacidad;
        Entrada<K, V> entrada = tabla[indice];

        while (entrada != null) {
            if (entrada.clave.equals(clave)) {
                entrada.valor = valor;
                return;
            }
            entrada = entrada.siguiente;
        }

        Entrada<K, V> nuevaEntrada = new Entrada<>(clave, valor);
        nuevaEntrada.siguiente = tabla[indice];
        tabla[indice] = nuevaEntrada;
        tamaño++;
    }

    public V obtener(K clave) {
        if (clave == null) {
            return null;
        }

        int indice = hash(clave) % capacidad;
        Entrada<K, V> entrada = tabla[indice];

        while (entrada != null) {
            if (entrada.clave.equals(clave)) {
                return entrada.valor;
            }
            entrada = entrada.siguiente;
        }

        return null;
    }

    public boolean contieneClave(K clave) {
        return obtener(clave) != null;
    }

    public V remover(K clave) {
        if (clave == null) {
            return null;
        }

        int indice = hash(clave) % capacidad;
        Entrada<K, V> entrada = tabla[indice];
        Entrada<K, V> anterior = null;

        while (entrada != null) {
            if (entrada.clave.equals(clave)) {
                if (anterior == null) {
                    tabla[indice] = entrada.siguiente;
                } else {
                    anterior.siguiente = entrada.siguiente;
                }
                tamaño--;
                return entrada.valor;
            }
            anterior = entrada;
            entrada = entrada.siguiente;
        }

        return null;
    }

    public int tamaño() {
        return tamaño;
    }

    public boolean estaVacio() {
        return tamaño == 0;
    }

    public void limpiar() {
        for (int i = 0; i < capacidad; i++) {
            tabla[i] = null;
        }
        tamaño = 0;
    }

    public ListaSimple<K> claves() {
        ListaSimple<K> listaClaves = new ListaSimple<>();
        for (int i = 0; i < capacidad; i++) {
            Entrada<K, V> entrada = tabla[i];
            while (entrada != null) {
                listaClaves.agregar(entrada.clave);
                entrada = entrada.siguiente;
            }
        }
        return listaClaves;
    }

    public ListaSimple<V> valores() {
        ListaSimple<V> listaValores = new ListaSimple<>();
        for (int i = 0; i < capacidad; i++) {
            Entrada<K, V> entrada = tabla[i];
            while (entrada != null) {
                listaValores.agregar(entrada.valor);
                entrada = entrada.siguiente;
            }
        }
        return listaValores;
    }

    private int hash(K clave) {
        return Math.abs(clave.hashCode());
    }

    private void redimensionar() {
        int nuevaCapacidad = capacidad * 2;
        Entrada<K, V>[] nuevaTabla = new Entrada[nuevaCapacidad];

        for (int i = 0; i < capacidad; i++) {
            Entrada<K, V> entrada = tabla[i];
            while (entrada != null) {
                Entrada<K, V> siguiente = entrada.siguiente;
                int nuevoIndice = hash(entrada.clave) % nuevaCapacidad;
                entrada.siguiente = nuevaTabla[nuevoIndice];
                nuevaTabla[nuevoIndice] = entrada;
                entrada = siguiente;
            }
        }

        tabla = nuevaTabla;
        capacidad = nuevaCapacidad;
    }

    // Métodos de compatibilidad con HashMap
    public void put(K clave, V valor) {
        poner(clave, valor);
    }

    public V get(K clave) {
        return obtener(clave);
    }

    public boolean containsKey(K clave) {
        return contieneClave(clave);
    }

    public V remove(K clave) {
        return remover(clave);
    }

    public int size() {
        return tamaño();
    }

    public boolean isEmpty() {
        return estaVacio();
    }

    public void clear() {
        limpiar();
    }

    public V getOrDefault(K clave, V valorPorDefecto) {
        V valor = obtener(clave);
        return valor != null ? valor : valorPorDefecto;
    }

    private static class Entrada<K, V> {
        K clave;
        V valor;
        Entrada<K, V> siguiente;

        Entrada(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }
}
