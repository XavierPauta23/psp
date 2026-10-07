package EjerciciosConcurrencia.Ejercicio9;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurante {

    record Pedido(int id, String plato, long tiempoPreparacion) {}

    public static void main(String[] args) throws Exception {

        int numPedidos = 15;
        int numCocineros = 3;

        BlockingQueue<Pedido> cola = new ArrayBlockingQueue<>(10);

        ConcurrentHashMap<String, Integer> platosPreparados =
                new ConcurrentHashMap<>();

        AtomicInteger pedidosCompletados = new AtomicInteger(0);

        CountDownLatch pedidosTerminados =
                new CountDownLatch(numPedidos);

        List<String> registroPedidos = new ArrayList<>();

        CyclicBarrier barrera = new CyclicBarrier(
                numCocineros,
                () -> System.out.println("Todos los cocineros están listos. ¡Restaurante abierto!")
        );

        String[] platos = {
                "Paella",
                "Tortilla",
                "Gazpacho",
                "Croquetas"
        };

        // Hilo del camarero
        Thread camarero = new Thread(() -> {

            Random random = new Random();

            try {

                for (int i = 1; i <= numPedidos; i++) {

                    String plato = platos[random.nextInt(platos.length)];

                    long tiempo = 500 + random.nextInt(1000);

                    Pedido pedido = new Pedido(i, plato, tiempo);

                    cola.put(pedido);

                    System.out.println("[Camarero] Pedido #" + i
                            + " de " + plato + " enviado a cocina.");

                    Thread.sleep(200);
                }

            } catch (InterruptedException e) {
                System.out.println("Camarero interrumpido.");
            }
        });

        camarero.start();

        // Crear los 3 cocineros
        List<Thread> cocineros = new ArrayList<>();

        for (int i = 1; i <= numCocineros; i++) {

            int cocinero = i;

            Thread hilo = new Thread(() -> {

                try {

                    // Preparación del turno
                    Thread.sleep(500 + new Random().nextInt(1000));

                    System.out.println("[Cocinero " + cocinero
                            + "] preparado para trabajar.");

                    // Esperar a los otros cocineros
                    barrera.await();

                    while (pedidosCompletados.get() < numPedidos) {

                        Pedido pedido = cola.poll(500, TimeUnit.MILLISECONDS);

                        if (pedido == null) {
                            continue;
                        }

                        System.out.println("[Cocinero " + cocinero
                                + "] preparando pedido #" + pedido.id());

                        Thread.sleep(pedido.tiempoPreparacion());

                        // Contar el plato preparado
                        platosPreparados.merge(
                                pedido.plato(),
                                1,
                                Integer::sum
                        );

                        int total = pedidosCompletados.incrementAndGet();

                        // Proteger el registro compartido
                        synchronized (registroPedidos) {

                            registroPedidos.add(
                                    "Pedido #" + pedido.id()
                                            + " - " + pedido.plato()
                                            + " - Cocinero " + cocinero
                            );
                        }

                        System.out.println("[Cocinero " + cocinero
                                + "] terminó el pedido #" + pedido.id()
                                + ". Total completados: " + total);

                        pedidosTerminados.countDown();
                    }

                } catch (Exception e) {
                    System.out.println("Cocinero " + cocinero
                            + " interrumpido.");
                }

            });

            cocineros.add(hilo);
            hilo.start();
        }

        // Esperar a que se terminen los 15 pedidos
        pedidosTerminados.await();

        camarero.join();

        for (Thread cocinero : cocineros) {
            cocinero.join();
        }

        System.out.println();
        System.out.println("Todos los pedidos han sido preparados.");

        // Informes finales
        try (ExecutorService executor =
                     Executors.newFixedThreadPool(3)) {

            Callable<String> informePlatos = () ->
                    "Resumen de platos preparados: "
                            + platosPreparados;

            Callable<String> informeTotal = () ->
                    "Total de pedidos completados: "
                            + pedidosCompletados.get();

            Callable<String> informeAuditoria = () -> {

                synchronized (registroPedidos) {
                    return "Registro de pedidos:\n"
                            + String.join("\n", registroPedidos);
                }
            };

            List<Future<String>> resultados =
                    executor.invokeAll(
                            List.of(
                                    informePlatos,
                                    informeTotal,
                                    informeAuditoria
                            )
                    );

            System.out.println();
            System.out.println("===== INFORMES FINALES =====");

            for (Future<String> resultado : resultados) {
                System.out.println(resultado.get());
            }
        }
    }
}

