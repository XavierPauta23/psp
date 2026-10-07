package EjerciciosTareasAsyncronas.Ejercicio15;
import java.util.concurrent.CompletableFuture;

public class Ejercicio15IntContador {

    static int contador = 0;

    public static void main(String[] args) {

        CompletableFuture<?>[] tareas = new CompletableFuture[10];

        for (int i = 0; i < 10; i++) {
            tareas[i] = CompletableFuture.runAsync(() -> {

                for (int j = 0; j < 10000; j++) {
                    contador++;
                }

            });
        }

        CompletableFuture.allOf(tareas).join();

        System.out.println("Contador final: " + contador);
    }
}