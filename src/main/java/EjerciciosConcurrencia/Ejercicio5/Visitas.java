package EjerciciosConcurrencia.Ejercicio5;

import java.util.concurrent.atomic.AtomicInteger;

public class Visitas {
    public static void main(String[] args) throws InterruptedException {

        AtomicInteger contador = new AtomicInteger(0);

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                contador.incrementAndGet();
            }).start();
        }

        Thread.sleep(1000);

        System.out.println("Número de visitas: " + contador.get());
    }
}