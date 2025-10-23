package main.estructuras;

import main.modelo.Proceso;

public class Ordenador {

    public static <T extends Comparable<T>> void ordenarBurbuja(ListaSimple<T> lista) {
        int n = lista.tamaño();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.obtener(j).compareTo(lista.obtener(j + 1)) > 0) {
                    intercambiar(lista, j, j + 1);
                }
            }
        }
    }

    public static <T extends Comparable<T>> void ordenarSeleccion(ListaSimple<T> lista) {
        int n = lista.tamaño();
        for (int i = 0; i < n - 1; i++) {
            int minimo = i;
            for (int j = i + 1; j < n; j++) {
                if (lista.obtener(j).compareTo(lista.obtener(minimo)) < 0) {
                    minimo = j;
                }
            }
            if (minimo != i) {
                intercambiar(lista, i, minimo);
            }
        }
    }

    public static <T extends Comparable<T>> void ordenarInsercion(ListaSimple<T> lista) {
        int n = lista.tamaño();
        for (int i = 1; i < n; i++) {
            T clave = lista.obtener(i);
            int j = i - 1;
            while (j >= 0 && lista.obtener(j).compareTo(clave) > 0) {
                lista.agregar(j + 1, lista.obtener(j));
                lista.remover(j + 2);
                j--;
            }
            lista.agregar(j + 1, clave);
            lista.remover(i + 1);
        }
    }

    public static void ordenarPorInstrucciones(ListaSimple<Proceso> lista) {
        ordenarBurbuja(lista, (p1, p2) -> {
            return Integer.compare(p1.getNumInstrucciones(), p2.getNumInstrucciones());
        });
    }

    public static void ordenarPorPrioridad(ListaSimple<Proceso> lista) {
        ordenarBurbuja(lista, (p1, p2) -> {
            return Integer.compare(p1.getPrioridad(), p2.getPrioridad());
        });
    }

    public static void ordenarPorTiempoLlegada(ListaSimple<Proceso> lista) {
        ordenarBurbuja(lista, (p1, p2) -> {
            return Integer.compare(p1.getId(), p2.getId());
        });
    }

    public static <T> void ordenarBurbuja(ListaSimple<T> lista, Comparador<T> comparador) {
        int n = lista.tamaño();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (comparador.comparar(lista.obtener(j), lista.obtener(j + 1)) > 0) {
                    intercambiar(lista, j, j + 1);
                }
            }
        }
    }

    public static <T> void ordenarSeleccion(ListaSimple<T> lista, Comparador<T> comparador) {
        int n = lista.tamaño();
        for (int i = 0; i < n - 1; i++) {
            int minimo = i;
            for (int j = i + 1; j < n; j++) {
                if (comparador.comparar(lista.obtener(j), lista.obtener(minimo)) < 0) {
                    minimo = j;
                }
            }
            if (minimo != i) {
                intercambiar(lista, i, minimo);
            }
        }
    }

    public static <T> void ordenarInsercion(ListaSimple<T> lista, Comparador<T> comparador) {
        int n = lista.tamaño();
        for (int i = 1; i < n; i++) {
            T clave = lista.obtener(i);
            int j = i - 1;
            while (j >= 0 && comparador.comparar(lista.obtener(j), clave) > 0) {
                lista.agregar(j + 1, lista.obtener(j));
                lista.remover(j + 2);
                j--;
            }
            lista.agregar(j + 1, clave);
            lista.remover(i + 1);
        }
    }

    public static <T> void ordenarRapido(ListaSimple<T> lista, Comparador<T> comparador) {
        if (lista.tamaño() <= 1) {
            return;
        }
        ordenarRapidoRecursivo(lista, 0, lista.tamaño() - 1, comparador);
    }

    private static <T> void ordenarRapidoRecursivo(ListaSimple<T> lista, int bajo, int alto, Comparador<T> comparador) {
        if (bajo < alto) {
            int indicePivote = particionar(lista, bajo, alto, comparador);
            ordenarRapidoRecursivo(lista, bajo, indicePivote - 1, comparador);
            ordenarRapidoRecursivo(lista, indicePivote + 1, alto, comparador);
        }
    }

    private static <T> int particionar(ListaSimple<T> lista, int bajo, int alto, Comparador<T> comparador) {
        T pivote = lista.obtener(alto);
        int i = bajo - 1;

        for (int j = bajo; j < alto; j++) {
            if (comparador.comparar(lista.obtener(j), pivote) <= 0) {
                i++;
                intercambiar(lista, i, j);
            }
        }

        intercambiar(lista, i + 1, alto);
        return i + 1;
    }

    private static <T> void intercambiar(ListaSimple<T> lista, int i, int j) {
        T temp = lista.obtener(i);
        lista.agregar(i, lista.obtener(j));
        lista.remover(i + 1);
        lista.agregar(j, temp);
        lista.remover(j + 1);
    }

    public static <T> void revertir(ListaSimple<T> lista) {
        int n = lista.tamaño();
        for (int i = 0; i < n / 2; i++) {
            intercambiar(lista, i, n - 1 - i);
        }
    }

    public interface Comparador<T> {
        int comparar(T a, T b);
    }
}
