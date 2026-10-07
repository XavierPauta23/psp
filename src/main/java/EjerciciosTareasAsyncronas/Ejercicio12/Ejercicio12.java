package EjerciciosTareasAsyncronas.Ejercicio12;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Ejercicio12 {

    public static CompletableFuture<String> tareaLenta() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return "Tarea terminada";
        });
    }

    public static void main(String[] args) {

        CompletableFuture<String> resultado = tareaLenta()
                .completeOnTimeout(
                        "Resultado no disponible",
                        3,
                        TimeUnit.SECONDS
                );

        System.out.println(resultado.join());
    }
}