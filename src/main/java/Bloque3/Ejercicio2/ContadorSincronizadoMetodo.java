package Bloque3.Ejercicio2;

public class ContadorSincronizadoMetodo {
    private int valorContador = 0;

    // Al añadir 'synchronized', solo un hilo puede ejecutar este método a la vez
    public synchronized void sumarUno() {
        valorContador++;
    }

    public synchronized int obtenerValor() {
        return valorContador;
    }

    public static void main(String[] args) throws InterruptedException {
        ContadorSincronizadoMetodo objetoContador = new ContadorSincronizadoMetodo();

        // Tarea que incrementa el contador 10,000 veces
        Runnable rutinaIncremento = () -> {
            for (int i = 0; i < 10_000; i++) {
                objetoContador.sumarUno();
            }
        };

        // Creación e inicio de los 10 hilos
        Thread[] grupoHilos = new Thread[10];
        for (int i = 0; i < 10; i++) {
            grupoHilos[i] = new Thread(rutinaIncremento);
        }

        for (Thread hilo : grupoHilos) {
            hilo.start();
        }

        // Espera a la finalización de todos los hilos
        for (Thread hilo : grupoHilos) {
            hilo.join();
        }

        System.out.println("Valor total obtenido (sincronizado): " + objetoContador.obtenerValor());
    }
}
