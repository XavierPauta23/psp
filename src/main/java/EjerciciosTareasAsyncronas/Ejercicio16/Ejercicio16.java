package EjerciciosTareasAsyncronas.Ejercicio16;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ejercicio16 {

    public static void main(String[] args) {

        // Executor propio con 3 hilos
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Consulta del usuario
        CompletableFuture<String> usuario = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(
                            "Servicio de usuario - hilo: "
                                    + Thread.currentThread().getName()
                    );

                    return "Oscar";

                }, executor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(error -> "NO DISPONIBLE");


        // Consulta de pedidos
        CompletableFuture<Integer> pedidos = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(
                            "Servicio de pedidos - hilo: "
                                    + Thread.currentThread().getName()
                    );

                    return 8;

                }, executor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(error -> 0);


        // Consulta de estadísticas
        CompletableFuture<String> estadisticas = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(
                            "Servicio de estadísticas - hilo: "
                                    + Thread.currentThread().getName()
                    );

                    return "1250 visitas";

                }, executor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(error -> "NO DISPONIBLES");


        // Esperamos a que las tres consultas terminen
        CompletableFuture<Void> todas = CompletableFuture.allOf(
                usuario,
                pedidos,
                estadisticas
        );

        todas.join();


        // Mostramos el resultado final
        System.out.println();
        System.out.println("===== RESULTADO =====");
        System.out.println("Usuario: " + usuario.join());
        System.out.println("Pedidos: " + pedidos.join());
        System.out.println("Estadísticas: " + estadisticas.join());
        System.out.println("=====================");


        // Cerramos el executor
        executor.shutdown();
    }
}