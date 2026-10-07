package EjerciciosTareasAsyncronas.Ejercicio10;

import java.util.concurrent.CompletableFuture;

public class Ejercicio10 {

    public static CompletableFuture<String> consultarServidor() {
        return CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Error al consultar el servidor");
        });
    }

    public static void main(String[] args) {

        CompletableFuture<String> resultado = consultarServidor()
                .exceptionally(error -> "DATOS POR DEFECTO");

        System.out.println(resultado.join());
    }
}