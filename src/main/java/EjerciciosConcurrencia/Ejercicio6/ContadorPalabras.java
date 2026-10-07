package EjerciciosConcurrencia.Ejercicio6;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ContadorPalabras {

    public static void main(String[] args) throws InterruptedException {

        String[] textos = {
                "java es un lenguaje de programación",
                "java permite trabajar con hilos",
                "los hilos permiten ejecutar tareas",
                "java tiene muchas herramientas para trabajar con hilos"
        };

        // Prueba con ConcurrentHashMap
        ConcurrentHashMap<String, Integer> mapaConcurrente = new ConcurrentHashMap<>();

        long inicio = System.nanoTime();

        Thread[] hilos = new Thread[textos.length];

        for (int i = 0; i < textos.length; i++) {

            final String texto = textos[i];

            hilos[i] = new Thread(() -> {

                String[] palabras = texto.split(" ");

                for (String palabra : palabras) {
                    mapaConcurrente.merge(palabra, 1, Integer::sum);
                }
            });

            hilos[i].start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        long fin = System.nanoTime();

        long tiempoConcurrente = fin - inicio;

        System.out.println("Resultado con ConcurrentHashMap:");
        System.out.println(mapaConcurrente);
        System.out.println("Tiempo: " + tiempoConcurrente + " ns");


        // Prueba con HashMap y synchronized
        HashMap<String, Integer> mapaNormal = new HashMap<>();

        inicio = System.nanoTime();

        Thread[] hilos2 = new Thread[textos.length];

        for (int i = 0; i < textos.length; i++) {

            final String texto = textos[i];

            hilos2[i] = new Thread(() -> {

                String[] palabras = texto.split(" ");

                for (String palabra : palabras) {

                    synchronized (mapaNormal) {

                        if (mapaNormal.containsKey(palabra)) {
                            mapaNormal.put(palabra, mapaNormal.get(palabra) + 1);
                        } else {
                            mapaNormal.put(palabra, 1);
                        }
                    }
                }
            });

            hilos2[i].start();
        }

        for (Thread hilo : hilos2) {
            hilo.join();
        }

        fin = System.nanoTime();

        long tiempoHashMap = fin - inicio;

        System.out.println();
        System.out.println("Resultado con HashMap:");
        System.out.println(mapaNormal);
        System.out.println("Tiempo: " + tiempoHashMap + " ns");


        // Comparación
        System.out.println();
        System.out.println("Comparación:");

        if (tiempoConcurrente < tiempoHashMap) {
            System.out.println("ConcurrentHashMap ha tardado menos.");
        } else if (tiempoConcurrente > tiempoHashMap) {
            System.out.println("HashMap con synchronized ha tardado menos.");
        } else {
            System.out.println("Los dos han tardado lo mismo.");
        }
    }
}
