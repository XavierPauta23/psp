package Bloque3.Ejercicio4;

public class SolucionDeadlock {
    private static final Object cerrojoUno = new Object();
    private static final Object cerrojoDos = new Object();

    public static void main(String[] args) {
        // Rutina que asegura la adquisición en el orden estricto: Cerrojo 1 -> Cerrojo 2
        Runnable procesoSeguro = () -> {
            synchronized (cerrojoUno) {
                System.out.println(Thread.currentThread().getName() + " -> Adquirido Cerrojo 1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}

                synchronized (cerrojoDos) {
                    System.out.println(Thread.currentThread().getName() + " -> Adquirido Cerrojo 2");
                }
            }
        };

        // Ambos hilos ejecutan la misma rutina, garantizando la misma jerarquía de cerrojos
        Thread hiloA = new Thread(procesoSeguro, "Hilo-Alfa");
        Thread hiloB = new Thread(procesoSeguro, "Hilo-Beta");

        hiloA.start();
        hiloB.start();
    }
}
