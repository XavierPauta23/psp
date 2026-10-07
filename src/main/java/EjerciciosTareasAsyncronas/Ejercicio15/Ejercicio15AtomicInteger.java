package EjerciciosTareasAsyncronas.Ejercicio15;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Ejercicio15AtomicInteger {

    static AtomicInteger contador = new AtomicInteger(0);

    public static void main(String[] args) {

        CompletableFuture<?>[] tareas = new CompletableFuture[10];

        for (int i = 0; i < 10; i++) {
            tareas[i] = CompletableFuture.runAsync(() -> {

                for (int j = 0; j < 10000; j++) {
                    contador.incrementAndGet();
                }

            });
        }

        CompletableFuture.allOf(tareas).join();

        System.out.println("Contador final: " + contador.get());
    }
}