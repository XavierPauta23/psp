package EjerciciosConcurrencia.Ejercicio2;

public class LanzamientoCohete {
    public static void main(String[] args) throws InterruptedException {
        // Hilo secundario que gestiona el conteo regresivo
        Thread hiloConteo = new Thread(() -> {
            for (int segundo = 10; segundo >= 0; segundo--) {
                System.out.println("Cuenta regresiva: " + segundo);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        // Verificación de estados antes y después de iniciar
        System.out.println("Estado antes de start(): " + hiloConteo.getState()); // NEW
        hiloConteo.start();
        System.out.println("Estado justo tras start(): " + hiloConteo.getState()); // RUNNABLE

        // Pausa breve para observar el estado cuando está durmiendo
        Thread.sleep(1500);
        System.out.println("Estado durante la pausa sleep(): " + hiloConteo.getState()); // TIMED_WAITING

        // El hilo principal espera a que termine el conteo
        hiloConteo.join();

        System.out.println("Estado tras concluir ejecución: " + hiloConteo.getState()); // TERMINATED
        System.out.println("¡Despegue!");
    }
}
