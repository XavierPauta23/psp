package EjerciciosConcurrencia.Ejercicio8;

import java.util.concurrent.CountDownLatch;

public class Carrera {

    public static void main(String[] args) throws InterruptedException {

        int numCorredores = 5;

        CountDownLatch corredoresListos = new CountDownLatch(numCorredores);
        CountDownLatch salida = new CountDownLatch(1);

        for (int i = 1; i <= numCorredores; i++) {

            int corredor = i;

            new Thread(() -> {

                try {
                    System.out.println("Corredor " + corredor + " está calentando...");

                    Thread.sleep(500 + (long) (Math.random() * 1500));

                    System.out.println("Corredor " + corredor + " está listo.");

                    corredoresListos.countDown();

                    salida.await();

                    System.out.println("Corredor " + corredor + " empieza a correr.");

                } catch (InterruptedException e) {
                    System.out.println("Corredor " + corredor + " interrumpido.");
                }

            }).start();
        }

        corredoresListos.await();

        System.out.println("Todos los corredores están listos.");

        Thread.sleep(500);

        System.out.println("Juez: ¡Preparados, listos, ya!");

        salida.countDown();
    }
}
