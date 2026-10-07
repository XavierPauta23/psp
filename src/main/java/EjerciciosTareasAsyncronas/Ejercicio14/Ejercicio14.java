package EjerciciosTareasAsyncronas.Ejercicio14;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Ejercicio14 {

    public static void main(String[] args) {

        List<Integer> numeros =
                List.of(1, 2, 3, 4, 5);

        List<CompletableFuture<Integer>> tareas = numeros.stream()
                .map(numero -> CompletableFuture.supplyAsync(() -> numero * numero))
                .toList();

        CompletableFuture<Void> todas = CompletableFuture.allOf(
                tareas.toArray(new CompletableFuture[0])
        );

        todas.join();

        for (int i = 0; i < numeros.size(); i++) {
            System.out.println(
                    numeros.get(i) + " → " + tareas.get(i).join()
            );
        }
    }
}