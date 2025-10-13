package simuladorPlanificacion.metricas;

/**
 * Implementación personalizada de lista enlazada para evitar usar Collections de librería
 */
public class ListaEnlazada<T> {
    
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamaño;
    
    /**
     * Nodo interno de la lista enlazada
     */
    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;
        
        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }
    
    /**
     * Constructor de la lista enlazada
     */
    public ListaEnlazada() {
        this.cabeza = null;
        this.cola = null;
        this.tamaño = 0;
    }
    
    /**
     * Agrega un elemento al final de la lista
     * 
     * @param elemento Elemento a agregar
     */
    public void agregar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        
        if (cabeza == null) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            cola.siguiente = nuevoNodo;
            cola = nuevoNodo;
        }
        tamaño++;
    }
    
    /**
     * Obtiene el tamaño de la lista
     * 
     * @return Número de elementos
     */
    public int getTamaño() {
        return tamaño;
    }
    
    /**
     * Verifica si la lista está vacía
     * 
     * @return true si está vacía
     */
    public boolean estaVacia() {
        return cabeza == null;
    }
    
    /**
     * Convierte la lista a array
     * 
     * @return Array con los elementos de la lista
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        if (estaVacia()) {
            return (T[]) new Object[0];
        }
        
        Object[] array = new Object[tamaño];
        Nodo<T> actual = cabeza;
        int indice = 0;
        
        while (actual != null) {
            array[indice++] = actual.dato;
            actual = actual.siguiente;
        }
        
        return (T[]) array;
    }
    
    /**
     * Limpia la lista
     */
    public void limpiar() {
        cabeza = null;
        cola = null;
        tamaño = 0;
    }
    
    /**
     * Obtiene el primer elemento sin removerlo
     * 
     * @return Primer elemento o null si está vacía
     */
    public T obtenerPrimero() {
        if (cabeza == null) {
            return null;
        }
        return cabeza.dato;
    }
    
    /**
     * Obtiene el último elemento sin removerlo
     * 
     * @return Último elemento o null si está vacía
     */
    public T obtenerUltimo() {
        if (cola == null) {
            return null;
        }
        return cola.dato;
    }
}
