package EjerciciosConcurrencia.Ejercicio6;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProductorConsumidor {

    public static void main(String[] args) {

        BlockingQueue<Integer> cola = new ArrayBlockingQueue<>(5);

        Runnable productor = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cola.put(i);
                    System.out.println(Thread.currentThread().getName()
                            + " produce: " + i);

                    Thread.sleep(200);

                } catch (InterruptedException e) {
                    System.out.println("Productor interrumpido.");
                }
            }
        };

        Runnable consumidor = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    int numero = cola.take();

                    System.out.println(Thread.currentThread().getName()
                            + " consume: " + numero);

                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    System.out.println("Consumidor interrumpido.");
                }
            }
        };

        new Thread(productor, "Productor").start();
        new Thread(consumidor, "Consumidor").start();
    }
}