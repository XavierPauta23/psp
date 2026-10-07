package EjerciciosConcurrencia.Ejercicio8;

import java.util.concurrent.CyclicBarrier;

public class Proyecto {

    public static void main(String[] args) {

        int numTrabajadores = 4;

        CyclicBarrier barrera = new CyclicBarrier(
                numTrabajadores,
                () -> System.out.println("Fase completada, todos avanzan")
        );

        for (int i = 1; i <= numTrabajadores; i++) {

            int trabajador = i;

            new Thread(() -> {

                try {

                    for (int fase = 1; fase <= 3; fase++) {

                        System.out.println("Trabajador " + trabajador
                                + " realizando fase " + fase);

                        Thread.sleep(500 + (long) (Math.random() * 1500));

                        System.out.println("Trabajador " + trabajador
                                + " ha terminado la fase " + fase);

                        barrera.await();
                    }

                } catch (Exception e) {
                    System.out.println("Trabajador " + trabajador
                            + " ha sido interrumpido.");
                }

            }).start();
        }
    }
}
