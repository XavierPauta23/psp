package Bloque3.Ejercicio3;

public class ContadorConBloqueSincronizado {
    private int valorContador = 0;
    // Objeto dedicado exclusivamente a actuar como cerrojo
    private final Object objetoCerrojo = new Object();

    public void sumarUno() {
        // Solo la sección crítica queda dentro del bloque de exclusión mutua
        synchronized (objetoCerrojo) {
            valorContador++;
        }
    }

    public int obtenerValor() {
        synchronized (objetoCerrojo) {
            return valorContador;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ContadorConBloqueSincronizado objetoContador = new ContadorConBloqueSincronizado();

        // Tarea que incrementa el contador 10,000 veces
        Runnable rutinaIncremento = () -> {
            for (int i = 0; i < 10_000; i++) {
                objetoContador.sumarUno();
            }
        };

        // Creación e inicio de 10 hilos concurrentes
        Thread[] grupoHilos = new Thread[10];
        for (int i = 0; i < 10; i++) {
            grupoHilos[i] = new Thread(rutinaIncremento);
        }

        for (Thread hilo : grupoHilos) {
            hilo.start();
        }

        // Espera a que todos los hilos concluyan
        for (Thread hilo : grupoHilos) {
            hilo.join();
        }

        System.out.println("Valor total obtenido (bloque synchronized): " + objetoContador.obtenerValor());
    }
}
