package Ejercicio1;

public class ComparativaHilosVirtuales {
    private static final int TOTAL_TAREAS = 10_000;

    public static void main(String[] args) throws InterruptedException {

        // Tarea que simula una descarga liviana con pausas
        Runnable tareaDescarga = () -> {
            try {
                Thread.sleep(200); // Simula el tiempo de red/E-S
            } catch (InterruptedException e) {
                return;
            }
        };

        System.out.println("Iniciando prueba con " + TOTAL_TAREAS + " descargas simultáneas...\n");

        // 1. EJECUCIÓN CON HILOS VIRTUALES (Java 21+)
        long inicioVirtuales = System.currentTimeMillis();

        Thread[] hilosVirtuales = new Thread[TOTAL_TAREAS];
        for (int i = 0; i < TOTAL_TAREAS; i++) {
            hilosVirtuales[i] = Thread.ofVirtual()
                    .name("virtual-descarga-", i)
                    .start(tareaDescarga);
        }

        for (Thread hilo : hilosVirtuales) {
            hilo.join();
        }

        long tiempoVirtuales = System.currentTimeMillis() - inicioVirtuales;
        System.out.println("Tiempo con Hilos Virtuales: " + tiempoVirtuales + " ms");


        // 2. EJECUCIÓN CON HILOS DE PLATAFORMA (Tradicionales)
        long inicioPlataforma = System.currentTimeMillis();

        Thread[] hilosPlataforma = new Thread[TOTAL_TAREAS];
        for (int i = 0; i < TOTAL_TAREAS; i++) {
            hilosPlataforma[i] = Thread.ofPlatform()
                    .name("plataforma-descarga-", i)
                    .start(tareaDescarga);
        }

        for (Thread hilo : hilosPlataforma) {
            hilo.join();
        }

        long tiempoPlataforma = System.currentTimeMillis() - inicioPlataforma;
        System.out.println("Tiempo con Hilos de Plataforma: " + tiempoPlataforma + " ms");
    }
}
