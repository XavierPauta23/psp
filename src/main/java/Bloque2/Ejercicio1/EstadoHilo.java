package Bloque2.Ejercicio1;

public class EstadoHilo {
    public static void main(String[] args) throws InterruptedException {
        // Se define un hilo secundario que se pausará durante 3 segundos
        Thread hiloSecundario = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}
        });

        // Estado inicial del hilo previo a su puesta en marcha
        System.out.println("Estado inicial: " + hiloSecundario.getState());

        hiloSecundario.start();

        // Muestra el estado del hilo en intervalos de 500 ms mientras siga activo
        while (hiloSecundario.isAlive()) {
            System.out.println("Estado actual: " + hiloSecundario.getState());
            Thread.sleep(500);
        }

        // Estado una vez concluida su ejecución
        System.out.println("Estado al finalizar: " + hiloSecundario.getState());
    }
}
