package EjerciciosConcurrencia.Ejercicio2;

public class CocineroTemporizador {
    public static void main(String[] args) throws InterruptedException {
        Thread hiloCocinero = new Thread(() -> {
            for (int segundo = 1; segundo <= 10; segundo++) {
                System.out.println("Cocinando plato... segundo " + segundo + " de 10");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Captura la interrupción y sale limpiamente del hilo
                    System.out.println("Cocción cancelada. Limpiando la cocina...");
                    return;
                }
            }
            System.out.println("¡Plato preparado con éxito!");
        });

        System.out.println("Estado del cocinero al crearse: " + hiloCocinero.getState()); // NEW
        hiloCocinero.start();

        // Tras 3 segundos, el hilo main decide cancelar la cocción
        Thread.sleep(3000);
        System.out.println("Se interrumpe la preparación...");
        hiloCocinero.interrupt();

        // El hilo principal espera a que finalice la recolección/salida del hilo
        hiloCocinero.join();
        System.out.println("Estado del cocinero tras cancelar: " + hiloCocinero.getState()); // TERMINATED
    }
}
