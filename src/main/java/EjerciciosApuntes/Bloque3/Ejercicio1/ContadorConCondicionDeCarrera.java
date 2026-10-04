package EjerciciosApuntes.Bloque3.Ejercicio1;

public class ContadorConCondicionDeCarrera {
    private int valorContador = 0;

    public void sumarUno() {
        valorContador++; // Operación no atómica: lectura, modificación y escritura
    }

    public int obtenerValor() {
        return valorContador;
    }

    public static void main(String[] args) throws InterruptedException {
        ContadorConCondicionDeCarrera objetoContador = new ContadorConCondicionDeCarrera();

        // Tarea que ejecutará cada hilo: incrementar 10,000 veces
        Runnable rutinaIncremento = () -> {
            for (int i = 0; i < 10_000; i++) {
                objetoContador.sumarUno();
            }
        };

        // Creamos y ponemos en marcha 10 hilos concurrentes
        Thread[] grupoHilos = new Thread[10];
        for (int i = 0; i < 10; i++) {
            grupoHilos[i] = new Thread(rutinaIncremento);
        }

        for (Thread hilo : grupoHilos) {
            hilo.start();
        }

        // El hilo principal aguarda a que todos los hilos terminen
        for (Thread hilo : grupoHilos) {
            hilo.join();
        }

        System.out.println("Valor total obtenido (debería ser 100000): " + objetoContador.obtenerValor());
    }
}
