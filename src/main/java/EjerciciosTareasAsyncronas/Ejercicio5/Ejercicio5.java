package EjerciciosTareasAsyncronas.Ejercicio5;

import java.util.concurrent.CompletableFuture;

public class Ejercicio5 {

    public static void main(String[] args) {

        CompletableFuture<Integer> resultado = CompletableFuture.supplyAsync(() -> {
            return 10 + 20;
        });

        resultado
                .thenApply(numero -> numero * 2)
                .thenAccept(numero -> System.out.println("Resultado: " + numero));
    }
}
