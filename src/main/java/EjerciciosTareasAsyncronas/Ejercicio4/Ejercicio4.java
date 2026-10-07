package EjerciciosTareasAsyncronas.Ejercicio4;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Ejercicio4 {

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Callable<Integer> tarea1 = () -> {
            Thread.sleep(2000);
            return 10;
        };

        Callable<Integer> tarea2 = () -> {
            Thread.sleep(1000);
            return 20;
        };

        Callable<Integer> tarea3 = () -> {
            Thread.sleep(3000);
            return 30;
        };

        Future<Integer> f1 = executor.submit(tarea1);
        Future<Integer> f2 = executor.submit(tarea2);
        Future<Integer> f3 = executor.submit(tarea3);

        System.out.println("Orden: f1, f2, f3");

        long inicio = System.currentTimeMillis();

        int resultado1 = f1.get();
        int resultado2 = f2.get();
        int resultado3 = f3.get();

        long fin = System.currentTimeMillis();

        System.out.println("Resultado total: "
                + (resultado1 + resultado2 + resultado3));
        System.out.println("Tiempo: " + (fin - inicio) + " ms");

        executor.shutdown();


        // Segunda prueba
        executor = Executors.newFixedThreadPool(3);

        f1 = executor.submit(tarea1);
        f2 = executor.submit(tarea2);
        f3 = executor.submit(tarea3);

        System.out.println("\nOrden: f2, f1, f3");

        inicio = System.currentTimeMillis();

        resultado2 = f2.get();
        resultado1 = f1.get();
        resultado3 = f3.get();

        fin = System.currentTimeMillis();

        System.out.println("Resultado total: "
                + (resultado1 + resultado2 + resultado3));
        System.out.println("Tiempo: " + (fin - inicio) + " ms");

        executor.shutdown();
    }
}