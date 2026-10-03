package Bloque1.Ejercicio4Hilos;

import java.time.LocalTime;

public class RelojDaemon {
    public static void main(String[] args) throws InterruptedException {
        // Se crea el hilo secundario con la tarea en segundo plano
        Thread hiloReloj = new Thread(() -> {
            while (true) {
                System.out.println("Hora del sistema: " + LocalTime.now());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        // Marcamos el hilo como daemon antes de ponerlo en marcha
        hiloReloj.setDaemon(true);
        hiloReloj.start();

        // Pausamos el hilo principal para permitir ver varias impresiones
        Thread.sleep(3500);
        System.out.println("El hilo main ha terminado. Finaliza la aplicación.");
    }
}
