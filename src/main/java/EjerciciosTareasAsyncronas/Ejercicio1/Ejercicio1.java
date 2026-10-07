package EjerciciosTareasAsyncronas.Ejercicio1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Ejercicio1 {

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Integer> tarea = () -> {
            Thread.sleep(2000);
            return 42;
        };

        Future<Integer> resultado = executor.submit(tarea);

        System.out.println("Tarea enviada");
        System.out.println("Esperando resultado...");

        System.out.println("Resultado: " + resultado.get());

        executor.shutdown();
    }
}