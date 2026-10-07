package EjerciciosTareasAsyncronas.Ejercicio17;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ejercicio17 {

    // Comprobar stock
    public static CompletableFuture<Boolean> comprobarStock(
            ExecutorService executor) {

        return CompletableFuture.supplyAsync(() -> {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(
                            "Stock comprobado - hilo: "
                                    + Thread.currentThread().getName()
                    );

                    return true;

                }, executor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(error -> false);
    }


    // Calcular precio
    public static CompletableFuture<Boolean> calcularPrecio(
            ExecutorService executor) {

        return CompletableFuture.supplyAsync(() -> {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(
                            "Precio calculado - hilo: "
                                    + Thread.currentThread().getName()
                    );

                    return true;

                }, executor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(error -> false);
    }


    // Consultar envío
    public static CompletableFuture<Boolean> consultarEnvio(
            ExecutorService executor) {

        return CompletableFuture.supplyAsync(() -> {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(
                            "Envío consultado - hilo: "
                                    + Thread.currentThread().getName()
                    );

                    return true;

                }, executor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(error -> false);
    }


    public static void main(String[] args) {

        // Executor personalizado
        ExecutorService executor =
                Executors.newFixedThreadPool(3);


        // Las tres operaciones comienzan de forma independiente
        CompletableFuture<Boolean> stock =
                comprobarStock(executor);

        CompletableFuture<Boolean> precio =
                calcularPrecio(executor);

        CompletableFuture<Boolean> envio =
                consultarEnvio(executor);


        // Esperamos a que terminen las tres
        CompletableFuture<Void> todas =
                CompletableFuture.allOf(
                        stock,
                        precio,
                        envio
                );


        todas.join();


        // Comprobamos si todas han tenido éxito
        if (stock.join() && precio.join() && envio.join()) {

            System.out.println();
            System.out.println("Compra confirmada");

        } else {

            System.out.println();
            System.out.println("Compra no disponible");
        }


        // Cerramos el executor
        executor.shutdown();
    }
}