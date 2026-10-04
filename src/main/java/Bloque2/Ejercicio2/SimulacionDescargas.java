package Bloque2.Ejercicio2;

import java.util.Random;

public class SimulacionDescargas {
    public static void main(String[] args) throws InterruptedException {
        Random generador = new Random();
        Thread[] tareasDescarga = new Thread[3];

        // Creación e inicio de las tareas de descarga
        for (int i = 0; i < 3; i++) {
            int numArchivo = i + 1;
            tareasDescarga[i] = new Thread(() -> {
                int tiempoProceso = 1000 + generador.nextInt(2000);
                System.out.println("Archivo " + numArchivo + " -> Comenzando descarga (" + tiempoProceso + " ms)");
                try {
                    Thread.sleep(tiempoProceso);
                } catch (InterruptedException ignored) {}
                System.out.println("Archivo " + numArchivo + " -> Descarga completada");
            });
            tareasDescarga[i].start();
        }

        // El hilo main espera a que termine cada uno de los hilos del array
        for (Thread hilo : tareasDescarga) {
            hilo.join();
        }

        System.out.println("Proceso global terminado: Todas las descargas han finalizado.");
    }
}
