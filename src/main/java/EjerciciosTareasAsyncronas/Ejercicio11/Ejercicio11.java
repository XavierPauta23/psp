package EjerciciosTareasAsyncronas.Ejercicio11;

import java.util.concurrent.CompletableFuture;

public class Ejercicio11 {

    public static CompletableFuture<Integer> dividir(int a, int b) {
        return CompletableFuture.supplyAsync(() -> a / b)
                .handle((resultado, error) -> {
                    if (error != null) {
                        return 0;
                    }

                    return resultado;
                });
    }

    public static void main(String[] args) {

        CompletableFuture<Integer> resultado1 = dividir(10, 2);
        CompletableFuture<Integer> resultado2 = dividir(10, 0);

        System.out.println("10 / 2 = " + resultado1.join());
        System.out.println("10 / 0 = " + resultado2.join());
    }
}