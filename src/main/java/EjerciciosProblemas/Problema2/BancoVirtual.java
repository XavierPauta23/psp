package EjerciciosProblemas.Problema2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class BancoVirtual {

    private static final double SALDO_INICIAL = 10000.00;
    private static final int NUM_CLIENTES = 50;
    private static final int OPERACIONES_POR_CLIENTE = 10;

    public static void main(String[] args) {

        System.out.println("=== BANCO VIRTUAL ===");
        System.out.printf("Saldo inicial: %.2f€%n", SALDO_INICIAL);
        System.out.println(
                NUM_CLIENTES + " clientes realizando "
                        + (NUM_CLIENTES * OPERACIONES_POR_CLIENTE)
                        + " operaciones totales...\n"
        );

        ejecutarConReentrantLock();
        ejecutarConSynchronized();
        ejecutarConVolatile();
    }


    // 1. REENTRANTLOCK

    private static void ejecutarConReentrantLock() {

        CuentaReentrantLock cuenta =
                new CuentaReentrantLock(SALDO_INICIAL);

        long inicio = System.currentTimeMillis();

        ejecutarClientes(cuenta);

        long fin = System.currentTimeMillis();

        System.out.println("--- CON REENTRANTLOCK ---");

        mostrarResultado(
                cuenta.consultarSaldo(),
                cuenta.getOperacionesExitosas(),
                cuenta.getOperacionesFallidas(),
                fin - inicio,
                cuenta.obtenerHistorial()
        );

        System.out.println();
    }


    // 2. SYNCHRONIZED

    private static void ejecutarConSynchronized() {

        CuentaSynchronized cuenta =
                new CuentaSynchronized(SALDO_INICIAL);

        long inicio = System.currentTimeMillis();

        ejecutarClientes(cuenta);

        long fin = System.currentTimeMillis();

        System.out.println("--- CON SYNCHRONIZED ---");

        mostrarResultado(
                cuenta.consultarSaldo(),
                cuenta.getOperacionesExitosas(),
                cuenta.getOperacionesFallidas(),
                fin - inicio,
                cuenta.obtenerHistorial()
        );

        System.out.println();
    }


    // 3. VOLATILE

    private static void ejecutarConVolatile() {

        CuentaVolatile cuenta =
                new CuentaVolatile(SALDO_INICIAL);

        long inicio = System.currentTimeMillis();

        ejecutarClientes(cuenta);

        long fin = System.currentTimeMillis();

        System.out.println("--- CON VOLATILE ---");

        mostrarResultado(
                cuenta.consultarSaldo(),
                cuenta.getOperacionesExitosas(),
                cuenta.getOperacionesFallidas(),
                fin - inicio,
                cuenta.obtenerHistorial()
        );

        System.out.println(
                "volatile NO garantiza que las operaciones sean atómicas."
        );

        System.out.println();
    }


    // EJECUTAR LOS 50 CLIENTES

    private static void ejecutarClientes(
            CuentaBancaria cuenta) {

        try (ExecutorService executor =
                     Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 1; i <= NUM_CLIENTES; i++) {

                int numeroCliente = i;

                executor.submit(() -> {

                    Random random = new Random();

                    for (int j = 1;
                         j <= OPERACIONES_POR_CLIENTE;
                         j++) {

                        try {

                            // Latencia bancaria entre 100 y 300 ms
                            int tiempo =
                                    random.nextInt(201) + 100;

                            Thread.sleep(tiempo);

                            // 60% retiros y 40% ingresos
                            int tipoOperacion =
                                    random.nextInt(100);

                            if (tipoOperacion < 60) {

                                // Retiro entre 1€ y 100€
                                double cantidad =
                                        random.nextInt(100) + 1;

                                cuenta.retirar(
                                        cantidad,
                                        numeroCliente
                                );

                            } else {

                                // Ingreso entre 1€ y 50€
                                double cantidad =
                                        random.nextInt(50) + 1;

                                cuenta.ingresar(
                                        cantidad,
                                        numeroCliente
                                );
                            }

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();

                            cuenta.registrarInterrupcion(
                                    numeroCliente
                            );

                            return;
                        }
                    }
                });
            }

            executor.shutdown();

            if (!executor.awaitTermination(
                    60,
                    TimeUnit.SECONDS)) {

                System.out.println(
                        "Los clientes tardaron demasiado."
                );

                executor.shutdownNow();
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "La ejecución fue interrumpida."
            );
        }
    }


    // MOSTRAR RESULTADOS

    private static void mostrarResultado(
            double saldo,
            int exitosas,
            int fallidas,
            long tiempo,
            List<String> historial) {

        int totalOperaciones =
                exitosas + fallidas;

        System.out.printf(
                "Saldo final: %.2f€%n",
                saldo
        );

        System.out.println(
                "Operaciones exitosas: "
                        + exitosas
                        + "/"
                        + totalOperaciones
        );

        System.out.println(
                "Operaciones fallidas: "
                        + fallidas
                        + " (fondos insuficientes)"
        );

        System.out.println(
                "Tiempo total: "
                        + tiempo
                        + "ms"
        );

        System.out.println(
                "Operaciones registradas: "
                        + historial.size()
        );

        if (saldo < 0) {

            System.out.println(
                    "❌ ERROR: El saldo es negativo."
            );

        } else {

            System.out.println(
                    "✅ El saldo nunca es negativo."
            );
        }
    }


    // INTERFAZ DE LA CUENTA

    interface CuentaBancaria {

        boolean retirar(
                double cantidad,
                int cliente
        );

        void ingresar(
                double cantidad,
                int cliente
        );

        double consultarSaldo();

        List<String> obtenerHistorial();

        int getOperacionesExitosas();

        int getOperacionesFallidas();

        void registrarInterrupcion(int cliente);
    }


    // RECURSO COMPARTIDO PARA HISTORIAL Y ESTADÍSTICAS

    static class RegistroOperaciones {

        private final List<String> historial =
                new ArrayList<>();

        private final AtomicInteger operacionesExitosas =
                new AtomicInteger(0);

        private final AtomicInteger operacionesFallidas =
                new AtomicInteger(0);

        private final Object lockHistorial =
                new Object();

        private final DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss.SSS"
                );


        public void registrarOperacion(
                int cliente,
                String operacion,
                double cantidad,
                String resultado,
                double saldo) {

            if (resultado.equals("OK")) {
                operacionesExitosas.incrementAndGet();
            } else {
                operacionesFallidas.incrementAndGet();
            }

            String registro =
                    obtenerTimestamp()
                            + " | Cliente "
                            + cliente
                            + " | "
                            + operacion
                            + " | "
                            + String.format(
                            "%.2f€",
                            cantidad
                    )
                            + " | "
                            + resultado
                            + " | Saldo: "
                            + String.format(
                            "%.2f€",
                            saldo
                    );

            synchronized (lockHistorial) {
                historial.add(registro);
            }
        }


        public void registrarInterrupcion(
                int cliente) {

            synchronized (lockHistorial) {

                historial.add(
                        obtenerTimestamp()
                                + " | Cliente "
                                + cliente
                                + " | OPERACIÓN INTERRUMPIDA"
                );
            }
        }


        public int getOperacionesExitosas() {
            return operacionesExitosas.get();
        }


        public int getOperacionesFallidas() {
            return operacionesFallidas.get();
        }


        public List<String> obtenerHistorial() {

            synchronized (lockHistorial) {

                return new ArrayList<>(historial);
            }
        }


        private String obtenerTimestamp() {

            return LocalDateTime.now()
                    .format(formato);
        }
    }


    // CUENTA CON REENTRANTLOCK

    static class CuentaReentrantLock
            implements CuentaBancaria {

        private double saldo;

        private final ReentrantLock lock =
                new ReentrantLock();

        private final RegistroOperaciones registro =
                new RegistroOperaciones();


        public CuentaReentrantLock(
                double saldoInicial) {

            this.saldo = saldoInicial;
        }


        @Override
        public boolean retirar(
                double cantidad,
                int cliente) {

            lock.lock();

            try {

                if (saldo >= cantidad) {

                    saldo -= cantidad;

                    registro.registrarOperacion(
                            cliente,
                            "RETIRO",
                            cantidad,
                            "OK",
                            saldo
                    );

                    return true;

                } else {

                    registro.registrarOperacion(
                            cliente,
                            "RETIRO",
                            cantidad,
                            "FONDOS INSUFICIENTES",
                            saldo
                    );

                    return false;
                }

            } finally {

                lock.unlock();
            }
        }


        @Override
        public void ingresar(
                double cantidad,
                int cliente) {

            lock.lock();

            try {

                saldo += cantidad;

                registro.registrarOperacion(
                        cliente,
                        "INGRESO",
                        cantidad,
                        "OK",
                        saldo
                );

            } finally {

                lock.unlock();
            }
        }


        @Override
        public double consultarSaldo() {

            lock.lock();

            try {
                return saldo;
            } finally {
                lock.unlock();
            }
        }


        @Override
        public List<String> obtenerHistorial() {
            return registro.obtenerHistorial();
        }


        @Override
        public int getOperacionesExitosas() {
            return registro.getOperacionesExitosas();
        }


        @Override
        public int getOperacionesFallidas() {
            return registro.getOperacionesFallidas();
        }


        @Override
        public void registrarInterrupcion(
                int cliente) {

            registro.registrarInterrupcion(cliente);
        }
    }


    // CUENTA CON SYNCHRONIZED
    static class CuentaSynchronized
            implements CuentaBancaria {

        private double saldo;

        private final RegistroOperaciones registro =
                new RegistroOperaciones();


        public CuentaSynchronized(
                double saldoInicial) {

            this.saldo = saldoInicial;
        }


        @Override
        public synchronized boolean retirar(
                double cantidad,
                int cliente) {

            if (saldo >= cantidad) {

                saldo -= cantidad;

                registro.registrarOperacion(
                        cliente,
                        "RETIRO",
                        cantidad,
                        "OK",
                        saldo
                );

                return true;

            } else {

                registro.registrarOperacion(
                        cliente,
                        "RETIRO",
                        cantidad,
                        "FONDOS INSUFICIENTES",
                        saldo
                );

                return false;
            }
        }


        @Override
        public synchronized void ingresar(
                double cantidad,
                int cliente) {

            saldo += cantidad;

            registro.registrarOperacion(
                    cliente,
                    "INGRESO",
                    cantidad,
                    "OK",
                    saldo
            );
        }


        @Override
        public synchronized double consultarSaldo() {
            return saldo;
        }


        @Override
        public List<String> obtenerHistorial() {
            return registro.obtenerHistorial();
        }


        @Override
        public int getOperacionesExitosas() {
            return registro.getOperacionesExitosas();
        }


        @Override
        public int getOperacionesFallidas() {
            return registro.getOperacionesFallidas();
        }


        @Override
        public void registrarInterrupcion(
                int cliente) {

            registro.registrarInterrupcion(cliente);
        }
    }


    // CUENTA CON VOLATILE

    static class CuentaVolatile
            implements CuentaBancaria {

        private volatile double saldo;

        private final RegistroOperaciones registro =
                new RegistroOperaciones();


        public CuentaVolatile(
                double saldoInicial) {

            this.saldo = saldoInicial;
        }


        @Override
        public boolean retirar(
                double cantidad,
                int cliente) {

            if (saldo >= cantidad) {

                saldo -= cantidad;

                registro.registrarOperacion(
                        cliente,
                        "RETIRO",
                        cantidad,
                        "OK",
                        saldo
                );

                return true;

            } else {

                registro.registrarOperacion(
                        cliente,
                        "RETIRO",
                        cantidad,
                        "FONDOS INSUFICIENTES",
                        saldo
                );

                return false;
            }
        }


        @Override
        public void ingresar(
                double cantidad,
                int cliente) {

            saldo += cantidad;

            registro.registrarOperacion(
                    cliente,
                    "INGRESO",
                    cantidad,
                    "OK",
                    saldo
            );
        }


        @Override
        public double consultarSaldo() {
            return saldo;
        }


        @Override
        public List<String> obtenerHistorial() {
            return registro.obtenerHistorial();
        }


        @Override
        public int getOperacionesExitosas() {
            return registro.getOperacionesExitosas();
        }


        @Override
        public int getOperacionesFallidas() {
            return registro.getOperacionesFallidas();
        }


        @Override
        public void registrarInterrupcion(
                int cliente) {

            registro.registrarInterrupcion(cliente);
        }
    }
}
