package EjerciciosApuntes.Bloque4.Ejercicio1;

import java.util.LinkedList;
import java.util.Queue;

public class AlmacenMultielemento {
    private final Queue<Integer> colaDatos = new LinkedList<>();
    private final int capacidadMaxima;

    public AlmacenMultielemento(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public synchronized void guardar(int nuevoValor) throws InterruptedException {
        while (colaDatos.size() == capacidadMaxima) {
            wait();
        }
        colaDatos.add(nuevoValor);
        System.out.println(Thread.currentThread().getName() + " -> Insertado: " + nuevoValor);
        notifyAll();
    }

    public synchronized int extraer() throws InterruptedException {
        while (colaDatos.isEmpty()) {
            wait();
        }
        int valorExtraido = colaDatos.poll();
        System.out.println(Thread.currentThread().getName() + " -> Retirado: " + valorExtraido);
        notifyAll();
        return valorExtraido;
    }

    public static void main(String[] args) {
        AlmacenMultielemento almacen = new AlmacenMultielemento(5);

        Runnable rutinaGeneradora = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    almacen.guardar(i);
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        };

        Runnable rutinaConsumidora = () -> {
            for (int i = 0; i < 5; i++) {
                try {
                    almacen.extraer();
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        };

        new Thread(rutinaGeneradora, "HiloProductor-1").start();
        new Thread(rutinaGeneradora, "HiloProductor-2").start();
        new Thread(rutinaConsumidora, "HiloConsumidor-1").start();
        new Thread(rutinaConsumidora, "HiloConsumidor-2").start();
    }
}
