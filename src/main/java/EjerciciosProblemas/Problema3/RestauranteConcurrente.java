package EjerciciosProblemas.Problema3;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RestauranteConcurrente {

    // CONFIGURACIÓN
    private static final int NUM_CLIENTES = 100;
    private static final int NUM_CAMAREROS = 5;
    private static final int NUM_COCINEROS = 3;

    // Capacidad máxima de la mesa de pedidos
    private static final int CAPACIDAD_MESA = 10;

    // Tiempo entre llegada de clientes
    private static final int TIEMPO_LLEGADA_CLIENTE = 500;

    // Tiempo que tarda un camarero en tomar un pedido
    private static final int TIEMPO_CAMARERO = 1000;

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final Random random = new Random();


    public static void main(String[] args) {

        System.out.println("=== RESTAURANTE CONCURRENTE ===");
        System.out.println(
                "Iniciando servicio con "
                        + NUM_COCINEROS
                        + " cocineros y "
                        + NUM_CAMAREROS
                        + " camareros..."
        );

        System.out.println(
                "Clientes previstos: "
                        + NUM_CLIENTES
        );

        System.out.println();


        // COLAS

        // Cola donde esperan los clientes que han llegado
        // y todavía no han sido atendidos por un camarero.

        BlockingQueue<Pedido> pedidosClientes =
                new LinkedBlockingQueue<>();


        // Mesa de pedidos. IMPORTANTE:  Tiene una capacidad máxima de 10.
        // Si está llena, los camareros tendrán que esperar.
        BlockingQueue<Pedido> mesaPedidos =
                new LinkedBlockingQueue<>(
                        CAPACIDAD_MESA
                );


        Estadisticas estadisticas =
                new Estadisticas();


        long inicioRestaurante =
                System.currentTimeMillis();


        // EXECUTOR DE HILOS VIRTUALES
        try (ExecutorService executor =
                     Executors.newVirtualThreadPerTaskExecutor()) {


            // 3 COCINEROS

            for (int i = 1; i <= NUM_COCINEROS; i++) {

                int numeroCocinero = i;

                executor.submit(() ->
                        cocinar(
                                numeroCocinero,
                                mesaPedidos,
                                estadisticas
                        )
                );
            }


            // 5 CAMAREROS

            for (int i = 1; i <= NUM_CAMAREROS; i++) {

                int numeroCamarero = i;

                executor.submit(() ->
                        atenderClientes(
                                numeroCamarero,
                                pedidosClientes,
                                mesaPedidos,
                                estadisticas
                        )
                );
            }


            // 100 CLIENTES

            for (int i = 1; i <= NUM_CLIENTES; i++) {

                int numeroCliente = i;

                executor.submit(() ->
                        clienteLlega(
                                numeroCliente,
                                pedidosClientes,
                                estadisticas
                        )
                );
            }


            // ESPERAR A QUE LLEGUEN TODOS LOS CLIENTES
            while (
                    estadisticas.getClientesLlegados()
                            < NUM_CLIENTES
            ) {

                Thread.sleep(100);
            }


            // ESPERAR A QUE TODOS LOS CLIENTES SEAN ATENDIDOS
            while (
                    estadisticas.getClientesAtendidos()
                            < NUM_CLIENTES
            ) {

                Thread.sleep(100);
            }


            // ESPERAR A QUE TODOS LOS PLATOS SE SIRVAN
            while (
                    estadisticas.getPlatosServidos()
                            < NUM_CLIENTES
            ) {

                Thread.sleep(200);
            }


            // PARAR RESTAURANTE
            executor.shutdownNow();


            if (!executor.awaitTermination(
                    10,
                    TimeUnit.SECONDS
            )) {

                System.out.println(
                        "Algunos hilos no terminaron correctamente."
                );
            }


        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "El restaurante fue interrumpido."
            );
        }


        long finRestaurante =
                System.currentTimeMillis();


        // ESTADÍSTICAS FINALES

        mostrarEstadisticas(
                estadisticas,
                finRestaurante - inicioRestaurante
        );
    }


    // CLIENTE

    private static void clienteLlega(
            int numeroCliente,
            BlockingQueue<Pedido> pedidosClientes,
            Estadisticas estadisticas) {

        try {

             // Cada cliente llega 500 ms después del anterior.
             // Cliente 1 -> 0 ms
             // Cliente 2 -> 500 ms
             // Cliente 3 -> 1000 ms


            Thread.sleep(
                    (long) (numeroCliente - 1)
                            * TIEMPO_LLEGADA_CLIENTE
            );


            // Elegimos un plato aleatorio
            TipoPlato tipo =
                    obtenerPlatoAleatorio();


            long momentoLlegada =
                    System.currentTimeMillis();


            Pedido pedido =
                    new Pedido(
                            numeroCliente,
                            tipo,
                            momentoLlegada
                    );


            estadisticas.registrarClienteLlegado();


            imprimir(
                    "Cliente-"
                            + formatoCliente(numeroCliente)
                            + " pide "
                            + tipo
            );



             // El cliente deja su pedido en la cola.
             // Los camareros serán los consumidores de esta cola.

            pedidosClientes.put(pedido);


        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }


    // CAMAREROS
    private static void atenderClientes(
            int numeroCamarero,
            BlockingQueue<Pedido> pedidosClientes,
            BlockingQueue<Pedido> mesaPedidos,
            Estadisticas estadisticas) {

        while (
                estadisticas.getClientesAtendidos()
                        < NUM_CLIENTES
        ) {

            try {

                // El camarero espera hasta que haya un cliente.
                // take() bloquea automáticamente si no hay
                // ningún pedido disponible.

                Pedido pedido =
                        pedidosClientes.take();


                imprimir(
                        "Camarero-"
                                + numeroCamarero
                                + " toma pedido de Cliente-"
                                + formatoCliente(
                                pedido.getNumeroCliente()
                        )
                                + " - "
                                + pedido.getTipo()
                );


                 //El camarero tarda 1 segundo en gestionar
                 //el pedido.

                Thread.sleep(
                        TIEMPO_CAMARERO
                );



                 //Comprobamos si la mesa está llena antes
                 //de intentar introducir el pedido.

                if (
                        mesaPedidos.remainingCapacity()
                                == 0
                ) {

                    estadisticas.registrarMesaLlena();

                    imprimir(
                            "⚠ Mesa llena. "
                                    + "Camarero-"
                                    + numeroCamarero
                                    + " debe esperar."
                    );
                }


                long inicioEsperaMesa =
                        System.currentTimeMillis();


                 // put() bloquea automáticamente al camarero
                 // mientras la mesa esté llena.

                mesaPedidos.put(pedido);


                long finEsperaMesa =
                        System.currentTimeMillis();


                long tiempoEspera =
                        finEsperaMesa
                                - inicioEsperaMesa;


                if (tiempoEspera > 0) {

                    estadisticas.registrarTiempoMesaLlena(
                            tiempoEspera
                    );
                }


                estadisticas.registrarClienteAtendido();


                imprimir(
                        "Camarero-"
                                + numeroCamarero
                                + " deja pedido de Cliente-"
                                + formatoCliente(
                                pedido.getNumeroCliente()
                        )
                                + " en la mesa"
                );


            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                return;
            }
        }
    }


    // COCINEROS
    private static void cocinar(
            int numeroCocinero,
            BlockingQueue<Pedido> mesaPedidos,
            Estadisticas estadisticas) {

        while (
                estadisticas.getPlatosServidos()
                        < NUM_CLIENTES
        ) {

            try {

                long inicioEspera =
                        System.currentTimeMillis();


                Pedido pedido =
                        mesaPedidos.take();


                long finEspera =
                        System.currentTimeMillis();


                long tiempoEspera =
                        finEspera
                                - inicioEspera;



                 // Si el cocinero tuvo que esperar para conseguir
                 // un pedido, registramos ese tiempo.

                if (tiempoEspera > 10) {

                    estadisticas.registrarTiempoEsperaCocinero(
                            tiempoEspera
                    );
                }


                imprimir(
                        "Cocinero-"
                                + numeroCocinero
                                + " comienza "
                                + pedido.getTipo()
                                + " para Cliente-"
                                + formatoCliente(
                                pedido.getNumeroCliente()
                        )
                );


                // Tiempo de preparación del plato
                Thread.sleep(
                        pedido.getTipo().getTiempoMs()
                );


                // El plato ya está preparado.

                estadisticas.registrarPlatoServido();


                long momentoFinal =
                        System.currentTimeMillis();



                 // Tiempo desde que llegó el cliente hasta
                 // que su plato estuvo preparado.

                long tiempoTotal =
                        momentoFinal
                                - pedido.getMomentoLlegada();


                estadisticas.registrarTiempoEsperaCliente(
                        tiempoTotal
                );


                imprimir(
                        "Cocinero-"
                                + numeroCocinero
                                + " termina "
                                + pedido.getTipo()
                                + " para Cliente-"
                                + formatoCliente(
                                pedido.getNumeroCliente()
                        )
                );


            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                return;
            }
        }
    }


    // PLATO ALEATORIO

    private static TipoPlato obtenerPlatoAleatorio() {

        TipoPlato[] platos =
                TipoPlato.values();

        return platos[
                random.nextInt(platos.length)
                ];
    }


    // ESTADÍSTICAS FINALES

    private static void mostrarEstadisticas(
            Estadisticas estadisticas,
            long tiempoTotal) {

        System.out.println();
        System.out.println(
                "--- ESTADÍSTICAS FINALES ---"
        );


        System.out.println(
                "Clientes atendidos: "
                        + estadisticas.getClientesAtendidos()
                        + "/"
                        + NUM_CLIENTES
                        + " "
                        + (
                        estadisticas.getClientesAtendidos()
                                == NUM_CLIENTES
                                ? "✅"
                                : "❌"
                )
        );


        System.out.println(
                "Platos servidos: "
                        + estadisticas.getPlatosServidos()
                        + "/"
                        + NUM_CLIENTES
        );


        double promedioEspera =
                estadisticas.getTiempoPromedioCliente();


        System.out.printf(
                "Tiempo promedio de espera: %.2f segundos%n",
                promedioEspera / 1000.0
        );


        System.out.println(
                "Mesa llena (veces): "
                        + estadisticas.getVecesMesaLlena()
        );


        System.out.printf(
                "Cocineros esperando: %.2f segundos total%n",
                estadisticas.getTiempoEsperaCocineros()
                        / 1000.0
        );


        System.out.printf(
                "Tiempo total del restaurante: %.2f segundos%n",
                tiempoTotal / 1000.0
        );


        double eficiencia =
                calcularEficiencia(
                        estadisticas,
                        tiempoTotal
                );


        System.out.printf(
                "Eficiencia de los cocineros: %.2f%%%n",
                eficiencia
        );


        System.out.println();


        if (
                estadisticas.getClientesAtendidos()
                        == NUM_CLIENTES
                        &&
                        estadisticas.getPlatosServidos()
                                == NUM_CLIENTES
        ) {

            System.out.println(
                    "✅ Restaurante finalizado correctamente."
            );

        } else {

            System.out.println(
                    "❌ No se completaron todos los pedidos."
            );
        }
    }


    // EFICIENCIA

    private static double calcularEficiencia(
            Estadisticas estadisticas,
            long tiempoTotal) {

        if (tiempoTotal <= 0) {
            return 0;
        }


        /*
         * Tiempo máximo disponible de los 3 cocineros.
         */

        long tiempoDisponible =
                tiempoTotal * NUM_COCINEROS;


        long tiempoCocina =
                estadisticas.getTiempoCocina();


        double eficiencia =
                (
                        (double) tiempoCocina
                                / tiempoDisponible
                ) * 100;


        return Math.min(
                eficiencia,
                100
        );
    }


    // IMPRIMIR MENSAJES
    private static synchronized void imprimir(
            String mensaje) {

        System.out.println(
                "["
                        + LocalTime.now()
                        .format(FORMATO_HORA)
                        + "] "
                        + mensaje
        );
    }


    // FORMATO DEL CLIENTE

    private static String formatoCliente(
            int numeroCliente) {

        return String.format(
                "%03d",
                numeroCliente
        );
    }


    // ENUM TIPO DE PLATO

    enum TipoPlato {

        ENSALADA(2000),
        PASTA(3000),
        PIZZA(4000),
        CARNE(5000);

        private final int tiempoMs;


        TipoPlato(int tiempoMs) {
            this.tiempoMs = tiempoMs;
        }


        public int getTiempoMs() {
            return tiempoMs;
        }
    }


    // CLASE PEDIDO

    static class Pedido {

        private final int numeroCliente;

        private final TipoPlato tipo;

        private final long momentoLlegada;


        public Pedido(
                int numeroCliente,
                TipoPlato tipo,
                long momentoLlegada) {

            this.numeroCliente =
                    numeroCliente;

            this.tipo =
                    tipo;

            this.momentoLlegada =
                    momentoLlegada;
        }


        public int getNumeroCliente() {
            return numeroCliente;
        }


        public TipoPlato getTipo() {
            return tipo;
        }


        public long getMomentoLlegada() {
            return momentoLlegada;
        }
    }


    // ESTADÍSTICAS

    static class Estadisticas {

        // Clientes que han llegado
        private final AtomicInteger clientesLlegados =
                new AtomicInteger(0);


        // Clientes que ya han sido atendidos por un camarero
        private final AtomicInteger clientesAtendidos =
                new AtomicInteger(0);


        // Platos terminados por los cocineros
        private final AtomicInteger platosServidos =
                new AtomicInteger(0);


        // Número de veces que la mesa estaba llena
        private final AtomicInteger vecesMesaLlena =
                new AtomicInteger(0);


        // Tiempo total esperando por mesa llena
        private final AtomicLong tiempoMesaLlena =
                new AtomicLong(0);


        // Tiempo total de espera de los cocineros
        private final AtomicLong tiempoEsperaCocineros =
                new AtomicLong(0);


        // Tiempo total de espera de los clientes
        private final AtomicLong tiempoEsperaClientes =
                new AtomicLong(0);


        // Tiempo total cocinando
        private final AtomicLong tiempoCocina =
                new AtomicLong(0);


        public void registrarClienteLlegado() {

            clientesLlegados.incrementAndGet();

            mostrarEstadisticasTiempoReal();
        }


        public void registrarClienteAtendido() {

            clientesAtendidos.incrementAndGet();

            mostrarEstadisticasTiempoReal();
        }


        public void registrarPlatoServido() {

            platosServidos.incrementAndGet();

            mostrarEstadisticasTiempoReal();
        }


        public void registrarMesaLlena() {

            vecesMesaLlena.incrementAndGet();
        }


        public void registrarTiempoMesaLlena(
                long tiempo) {

            tiempoMesaLlena.addAndGet(
                    tiempo
            );
        }


        public void registrarTiempoEsperaCocinero(
                long tiempo) {

            tiempoEsperaCocineros.addAndGet(
                    tiempo
            );
        }


        public void registrarTiempoEsperaCliente(
                long tiempo) {

            tiempoEsperaClientes.addAndGet(
                    tiempo
            );
        }


        public int getClientesLlegados() {

            return clientesLlegados.get();
        }


        public int getClientesAtendidos() {

            return clientesAtendidos.get();
        }


        public int getPlatosServidos() {

            return platosServidos.get();
        }


        public int getVecesMesaLlena() {

            return vecesMesaLlena.get();
        }


        public long getTiempoEsperaCocineros() {

            return tiempoEsperaCocineros.get();
        }


        public long getTiempoCocina() {

            return tiempoCocina.get();
        }


        public double getTiempoPromedioCliente() {

            int platos =
                    platosServidos.get();


            if (platos == 0) {
                return 0;
            }


            return (
                    (double) tiempoEsperaClientes.get()
                            / platos
            );
        }


        private void mostrarEstadisticasTiempoReal() {

            System.out.println(
                    "   [ESTADÍSTICAS] "
                            + "Clientes llegados: "
                            + clientesLlegados.get()
                            + "/"
                            + NUM_CLIENTES
                            + " | Atendidos: "
                            + clientesAtendidos.get()
                            + " | Platos servidos: "
                            + platosServidos.get()
            );
        }
    }
}
