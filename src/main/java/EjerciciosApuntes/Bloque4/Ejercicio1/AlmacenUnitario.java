package EjerciciosApuntes.Bloque4.Ejercicio1;

public class AlmacenUnitario {
    private Integer elemento = null;

    public synchronized void guardar(int nuevoValor) throws InterruptedException {
        // Mantiene la espera mientras la casilla esté ocupada
        while (elemento != null) {
            wait();
        }
        elemento = nuevoValor;
        System.out.println("Producido elemento: " + nuevoValor);
        notifyAll();
    }

    public synchronized int extraer() throws InterruptedException {
        // Mantiene la espera mientras la casilla esté vacía
        while (elemento == null) {
            wait();
        }
        int valorExtraido = elemento;
        elemento = null;
        System.out.println("Consumido elemento: " + valorExtraido);
        notifyAll();
        return valorExtraido;
    }

    public static void main(String[] args) {
        AlmacenUnitario almacen = new AlmacenUnitario();

        Thread hiloProductor = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    almacen.guardar(i);
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        });

        Thread hiloConsumidor = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    almacen.extraer();
                    Thread.sleep(300);
                } catch (InterruptedException ignored) {}
            }
        });

        hiloProductor.start();
        hiloConsumidor.start();
    }
}
