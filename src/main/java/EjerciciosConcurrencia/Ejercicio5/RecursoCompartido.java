package EjerciciosConcurrencia.Ejercicio5;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RecursoCompartido {

    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

        Thread hilo1 = new Thread(() -> {
            try {
                lock.lock();

                System.out.println("Hilo 1 está utilizando el recurso.");
                Thread.sleep(2000);

                System.out.println("Hilo 1 termina.");

            } catch (InterruptedException e) {
                System.out.println("Hilo 1 interrumpido.");
            } finally {
                lock.unlock();
            }
        });

        Thread hilo2 = new Thread(() -> {

            boolean conseguido = false;

            while (!conseguido) {
                try {
                    if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {

                        try {
                            System.out.println("Hilo 2 ha conseguido el recurso.");
                            Thread.sleep(1000);
                            System.out.println("Hilo 2 termina.");

                            conseguido = true;

                        } finally {
                            lock.unlock();
                        }

                    } else {
                        System.out.println("Ocupado, lo intento más tarde");
                    }

                } catch (InterruptedException e) {
                    System.out.println("Hilo 2 interrumpido.");
                    break;
                }
            }
        });

        hilo1.start();
        hilo2.start();
    }
}