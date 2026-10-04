package EjerciciosApuntes.Bloque3.Ejercicio4;

public class DemostracionDeadlock {
    private static final Object cerrojoUno = new Object();
    private static final Object cerrojoDos = new Object();

    public static void main(String[] args) {
        // Hilo 1: Bloquea primero Cerrojo 1 y luego Cerrojo 2
        Thread hiloPrimero = new Thread(() -> {
            synchronized (cerrojoUno) {
                System.out.println("Hilo 1 -> Adquirido Cerrojo 1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}

                synchronized (cerrojoDos) {
                    System.out.println("Hilo 1 -> Adquirido Cerrojo 2");
                }
            }
        });

        // Hilo 2: Bloquea primero Cerrojo 2 y luego Cerrojo 1 (Orden inverso)
        Thread hiloSegundo = new Thread(() -> {
            synchronized (cerrojoDos) {
                System.out.println("Hilo 2 -> Adquirido Cerrojo 2");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}

                synchronized (cerrojoUno) {
                    System.out.println("Hilo 2 -> Adquirido Cerrojo 1");
                }
            }
        });

        hiloPrimero.start();
        hiloSegundo.start();
    }
}
