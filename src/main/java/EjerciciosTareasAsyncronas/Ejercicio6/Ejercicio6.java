package EjerciciosTareasAsyncronas.Ejercicio6;

import java.util.concurrent.CompletableFuture;

public class Ejercicio6 {

    public static void main(String[] args) {

        CompletableFuture.supplyAsync(() -> 10)

                .thenApply(numero -> numero * 2)

                .thenApply(numero -> numero + 5)

                .thenApply(numero -> String.valueOf(numero))

                .thenAccept(resultado -> System.out.println("Resultado: " + resultado));
    }
}
