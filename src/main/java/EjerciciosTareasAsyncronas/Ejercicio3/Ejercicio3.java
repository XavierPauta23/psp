package EjerciciosTareasAsyncronas.Ejercicio3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Ejercicio3 {

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        long inicio = System.currentTimeMillis();

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

        Future<Integer> resultado1 = executor.submit(tarea1);
        Future<Integer> resultado2 = executor.submit(tarea2);
        Future<Integer> resultado3 = executor.submit(tarea3);

        int total = resultado1.get()
                + resultado2.get()
                + resultado3.get();

        long fin = System.currentTimeMillis();

        System.out.println("Resultado total: " + total);
        System.out.println("Tiempo total: " + (fin - inicio) + " ms");

        executor.shutdown();
    }

    //¿¿Por qué el programa tarda aproximadamente 3 segundos y no 6?
    //Por que tenemos 3 hilos disponibles y las tareas se ejecutan en paralelo.
    //Por eso el programa tiene que esperar a la tarea que más tarda, que es la Tarea 3 con 3 segundos.
}
