package EjerciciosTareasAsyncronas.Ejercicio2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Ejercicio2 {

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Integer> future = executor.submit(() -> {

            System.out.println("Hilo que ejecuta la tarea: "
                    + Thread.currentThread().getName());

            Thread.sleep(5000);

            return 100;
        });

        while (!future.isDone()) {
            System.out.println("Esperando...");

            Thread.sleep(500);
        }

        System.out.println("Resultado: " + future.get());

        executor.shutdown();
    }
}
