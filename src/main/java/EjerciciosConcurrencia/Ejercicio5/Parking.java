package EjerciciosConcurrencia.Ejercicio5;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Parking {
    public static void main(String[] args) {
        Semaphore plazas = new Semaphore(3);
        Random random = new Random();

        for (int i = 1; i <= 10; i++) {
            int coche = i;

            new Thread(() -> {
                try {
                    System.out.println("Coche " + coche + " intentando entrar...");

                    if (plazas.availablePermits() == 0) {
                        System.out.println("Coche " + coche + " espera porque no hay plazas.");
                    }

                    plazas.acquire();

                    System.out.println("Coche " + coche + " ha entrado al parking. Plazas libres: "
                            + plazas.availablePermits());

                    Thread.sleep(500 + random.nextInt(1500));

                    System.out.println("Coche " + coche + " sale del parking.");
                    plazas.release();

                    System.out.println("Plazas libres: " + plazas.availablePermits());

                } catch (InterruptedException e) {
                    System.out.println("El coche " + coche + " ha sido interrumpido.");
                }
            }).start();
        }
    }
}