package EjerciciosProblemas.Problema1;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ContadorVisitas {

    // Número de visitantes
    private static final int NUM_VISITANTES = 1000;

    public static void main(String[] args) {

        System.out.println("=== CONTADOR DE VISITAS WEB ===");
        System.out.println("Esperando " + NUM_VISITANTES + " visitantes...\n");

        // Ejecutamos las tres versiones
        ejecutarSinSincronizacion();
        ejecutarConSynchronized();
        ejecutarConAtomicInteger();
    }


    // 1. SIN SINCRONIZACIÓN
    public static void ejecutarSinSincronizacion() {

        ContadorSinSincronizacion contador = new ContadorSinSincronizacion();

        long inicio = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < NUM_VISITANTES; i++) {

                executor.submit(() -> {

                    esperarTiempoAleatorio();

                    // Incremento sin ningún tipo de sincronización
                    contador.incrementarVisita();
                });
            }

            // Esperamos a que terminen todos los hilos
            executor.shutdown();

            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Los hilos tardaron demasiado en finalizar.");
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("La ejecución fue interrumpida.");
        }

        long fin = System.currentTimeMillis();

        System.out.println("--- SIN SINCRONIZACIÓN ---");
        System.out.println("Visitas esperadas: " + NUM_VISITANTES);
        System.out.println("Visitas contadas: " + contador.getVisitas());

        if (contador.getVisitas() == NUM_VISITANTES) {
            System.out.println("Resultado: CORRECTO (aunque existe riesgo de condición de carrera)");
        } else {
            System.out.println("Resultado: INCORRECTO - condición de carrera");
        }

        System.out.println("Tiempo: " + (fin - inicio) + "ms\n");
    }


    // 2. CON SYNCHRONIZED
    public static void ejecutarConSynchronized() {

        ContadorSynchronized contador = new ContadorSynchronized();

        long inicio = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < NUM_VISITANTES; i++) {

                executor.submit(() -> {

                    esperarTiempoAleatorio();

                    // Incremento protegido mediante synchronized
                    contador.incrementarVisita();
                });
            }

            executor.shutdown();

            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Los hilos tardaron demasiado en finalizar.");
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("La ejecución fue interrumpida.");
        }

        long fin = System.currentTimeMillis();

        System.out.println("--- CON SYNCHRONIZED ---");
        System.out.println("Visitas esperadas: " + NUM_VISITANTES);
        System.out.println("Visitas contadas: " + contador.getVisitas());

        if (contador.getVisitas() == NUM_VISITANTES) {
            System.out.println("Resultado: CORRECTO");
        } else {
            System.out.println("Resultado: INCORRECTO");
        }

        System.out.println("Tiempo: " + (fin - inicio) + "ms\n");
    }


    // 3. CON ATOMICINTEGER
    public static void ejecutarConAtomicInteger() {

        ContadorAtomic contador = new ContadorAtomic();

        long inicio = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < NUM_VISITANTES; i++) {

                executor.submit(() -> {

                    esperarTiempoAleatorio();

                    // Incremento atómico
                    contador.incrementarVisita();
                });
            }

            executor.shutdown();

            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Los hilos tardaron demasiado en finalizar.");
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("La ejecución fue interrumpida.");
        }

        long fin = System.currentTimeMillis();

        System.out.println("--- CON ATOMICINTEGER ---");
        System.out.println("Visitas esperadas: " + NUM_VISITANTES);
        System.out.println("Visitas contadas: " + contador.getVisitas());

        if (contador.getVisitas() == NUM_VISITANTES) {
            System.out.println("Resultado: CORRECTO");
        } else {
            System.out.println("Resultado: INCORRECTO");
        }

        System.out.println("Tiempo: " + (fin - inicio) + "ms\n");
    }


    // ESPERA ALEATORIA ENTRE 50 Y 150 MILISEGUNDOS
    private static void esperarTiempoAleatorio() {

        Random random = new Random();

        int tiempo = random.nextInt(101) + 50;

        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    // CLASE SIN SINCRONIZACIÓN
    static class ContadorSinSincronizacion {

        private int visitas = 0;

        public void incrementarVisita() {
            visitas++;
        }

        public int getVisitas() {
            return visitas;
        }
    }


    // CLASE CON SYNCHRONIZED
    static class ContadorSynchronized {

        private int visitas = 0;

        public synchronized void incrementarVisita() {
            visitas++;
        }

        public int getVisitas() {
            return visitas;
        }
    }


    // CLASE CON ATOMICINTEGER
    static class ContadorAtomic {

        private final AtomicInteger visitas = new AtomicInteger(0);

        public void incrementarVisita() {
            visitas.incrementAndGet();
        }

        public int getVisitas() {
            return visitas.get();
        }
    }
}
