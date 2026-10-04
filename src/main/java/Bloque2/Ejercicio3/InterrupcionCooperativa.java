package Bloque2.Ejercicio3;

public class InterrupcionCooperativa {
    public static void main(String[] args) throws InterruptedException {
        // Se crea el hilo secundario con la condición de parada por interrupción
        Thread hiloTarea = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // Simula una tarea continua en segundo plano
            }
            System.out.println("Solicitud de interrupción recibida. Liberando recursos y finalizando.");
        });

        hiloTarea.start();

        // El hilo principal espera 2 segundos antes de mandar la señal
        Thread.sleep(2000);

        // Envía el flag de interrupción al hilo
        hiloTarea.interrupt();
    }
}
