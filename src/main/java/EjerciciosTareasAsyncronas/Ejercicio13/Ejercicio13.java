package EjerciciosTareasAsyncronas.Ejercicio13;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ejercicio13 {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5; i++) {
            int numeroTarea = i;

            CompletableFuture.supplyAsync(() -> {
                String nombreHilo = Thread.currentThread().getName();

                System.out.println(
                        "Tarea " + numeroTarea + " - hilo: " + nombreHilo
                );

                return numeroTarea;

            }, executor);
        }

        executor.shutdown();
    }
}