package EjerciciosProblemas.Problema4;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Parking {

    // CONFIGURACIÓN
    private static final int PLAZAS_NORMALES = 20;
    private static final int PLAZAS_VIP = 5;
    private static final int CAPACIDAD_COLA = 10;
    private static final int NUM_COCHES = 200;

    private static final Random random = new Random();

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss");


    // SEMÁFOROS DE PLAZAS
    // Controlan el número máximo de plazas disponibles.
    private static final Semaphore semaforoNormales =
            new Semaphore(PLAZAS_NORMALES, true);

    private static final Semaphore semaforoVIP =
            new Semaphore(PLAZAS_VIP, true);


    // SEMÁFOROS DE BARRERAS
    // Solo un coche puede estar siendo procesado por la barrera.
    private static final Semaphore barreraEntrada =
            new Semaphore(1, true);

    private static final Semaphore barreraSalida =
            new Semaphore(1, true);


    // PLAZAS DISPONIBLES
    private static final Deque<String> plazasNormales =
            new ArrayDeque<>();

    private static final Deque<String> plazasVIP =
            new ArrayDeque<>();

    private static final Object bloqueoPlazas = new Object();


    // COLA DE ESPERA
    private static final Deque<Coche> colaEspera =
            new ArrayDeque<>();

    private static final Object bloqueoCola =
            new Object();


    // ESTADÍSTICAS
    private static final AtomicInteger vehiculosProcesados =
            new AtomicInteger();

    private static final AtomicInteger vehiculosAtendidos =
            new AtomicInteger();

    private static final AtomicInteger vehiculosRechazados =
            new AtomicInteger();

    private static final AtomicInteger ocupacionActual =
            new AtomicInteger();

    private static final AtomicInteger ocupacionMaxima =
            new AtomicInteger();

    private static final AtomicLong tiempoEstanciaTotal =
            new AtomicLong();

    // Guardamos los ingresos en céntimos para evitar
    // problemas de precisión con double.
    private static final AtomicLong ingresosCentimos =
            new AtomicLong();


    // EVENTOS PARA EL DASHBOARD

    private static final Deque<String> ultimosEventos =
            new ArrayDeque<>();

    private static final Object bloqueoEventos =
            new Object();


    // TIPOS DE VEHÍCULO
    enum TipoVehiculo {

        NORMAL(1.0),
        VIP(2.0);

        private final double tarifaPorMinuto;

        TipoVehiculo(double tarifaPorMinuto) {
            this.tarifaPorMinuto = tarifaPorMinuto;
        }

        public double getTarifaPorMinuto() {
            return tarifaPorMinuto;
        }
    }


    // CLASE COCHE
    static class Coche {

        private final int id;
        private final TipoVehiculo tipo;

        private long horaEntrada;
        private String plaza;

        public Coche(int id, TipoVehiculo tipo) {
            this.id = id;
            this.tipo = tipo;
        }

        public int getId() {
            return id;
        }

        public TipoVehiculo getTipo() {
            return tipo;
        }

        public long getHoraEntrada() {
            return horaEntrada;
        }

        public void setHoraEntrada(long horaEntrada) {
            this.horaEntrada = horaEntrada;
        }

        public String getPlaza() {
            return plaza;
        }

        public void setPlaza(String plaza) {
            this.plaza = plaza;
        }

        public String getNombre() {
            return String.format("Coche-%03d", id);
        }
    }


    // MAIN
    public static void main(String[] args) {

        inicializarPlazas();

        System.out.println("======================================");
        System.out.println("       PARKING INTELIGENTE");
        System.out.println("======================================");
        System.out.println("Plazas normales: " + PLAZAS_NORMALES);
        System.out.println("Plazas VIP: " + PLAZAS_VIP);
        System.out.println("Cola máxima: " + CAPACIDAD_COLA);
        System.out.println("Vehículos: " + NUM_COCHES);
        System.out.println();


        ExecutorService executor =
                Executors.newVirtualThreadPerTaskExecutor();


        // DASHBOARD EN TIEMPO REAL
        Thread dashboard = Thread.ofVirtual().start(() -> {

            try {

                while (!executor.isTerminated()) {

                    mostrarDashboard();

                    Thread.sleep(3000);
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        });


        // CREACIÓN DE LOS 200 COCHES
        for (int i = 1; i <= NUM_COCHES; i++) {

            final int id = i;

            executor.submit(() -> procesarCoche(id));

            /*
             * Los coches llegan cada 200 ms.
             *
             * Se utilizan hilos virtuales, por lo que podemos
             * mantener los 200 coches simultáneamente.
             */
            try {

                Thread.sleep(200);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }


        // ESPERAR A QUE TERMINEN TODOS LOS COCHES
        executor.shutdown();

        try {

            if (!executor.awaitTermination(15, TimeUnit.MINUTES)) {

                executor.shutdownNow();
            }

        } catch (InterruptedException e) {

            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }


        // Detener dashboard
        dashboard.interrupt();


        // RESUMEN FINAL
        mostrarResumenFinal();
    }


    // INICIALIZAR PLAZAS
    private static void inicializarPlazas() {

        synchronized (bloqueoPlazas) {

            for (int i = 1; i <= PLAZAS_NORMALES; i++) {

                plazasNormales.addLast("N-" + i);
            }

            for (int i = 1; i <= PLAZAS_VIP; i++) {

                plazasVIP.addLast("V-" + i);
            }
        }
    }


    // PROCESAR COCHE
    private static void procesarCoche(int id) {

        // Aproximadamente 20% de los coches serán VIP.
        TipoVehiculo tipo =
                random.nextInt(100) < 20
                        ? TipoVehiculo.VIP
                        : TipoVehiculo.NORMAL;

        Coche coche = new Coche(id, tipo);

        vehiculosProcesados.incrementAndGet();

        registrarEvento(
                "🚗 " + coche.getNombre()
                        + " (" + coche.getTipo() + ") llega"
        );


        // PRIMER INTENTO DE ENTRADA
        if (intentarEntrada(coche)) {

            realizarEstancia(coche);

            return;
        }


        // PARKING LLENO -> INTENTAR COLA
        if (!entrarEnCola(coche)) {

            vehiculosRechazados.incrementAndGet();

            registrarEvento(
                    "❌ " + coche.getNombre()
                            + " rechazado - Parking y cola llenos"
            );

            return;
        }


        // ESPERAR HASTA CONSEGUIR PLAZA
        esperarEnCola(coche);


        // Si consiguió entrar, realiza su estancia.
        if (coche.getPlaza() != null) {

            realizarEstancia(coche);
        }
    }


    // INTENTAR ENTRADA
    private static boolean intentarEntrada(Coche coche) {

        /*
         * La barrera procesa un coche cada 2 segundos.
         */
        try {

            barreraEntrada.acquire();

            try {

                Thread.sleep(2000);

            } finally {

                barreraEntrada.release();
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            return false;
        }


        String plaza = null;


        // VEHÍCULO VIP
        if (coche.getTipo() == TipoVehiculo.VIP) {

            /*
             * Primero intenta una plaza VIP.
             */
            if (semaforoVIP.tryAcquire()) {

                synchronized (bloqueoPlazas) {

                    plaza = plazasVIP.pollFirst();
                }

            } else {

                /*
                 * Si no hay plaza VIP, el VIP puede utilizar
                 * una plaza normal.
                 */
                if (semaforoNormales.tryAcquire()) {

                    synchronized (bloqueoPlazas) {

                        plaza = plazasNormales.pollFirst();
                    }
                }
            }

        } else {

            // VEHÍCULO NORMAL
            if (semaforoNormales.tryAcquire()) {

                synchronized (bloqueoPlazas) {

                    plaza = plazasNormales.pollFirst();
                }
            }
        }


        // NO HAY PLAZA
        if (plaza == null) {

            return false;
        }


        // ENTRADA CORRECTA
        coche.setPlaza(plaza);
        coche.setHoraEntrada(System.currentTimeMillis());

        int ocupacion =
                ocupacionActual.incrementAndGet();

        actualizarOcupacionMaxima(ocupacion);

        vehiculosAtendidos.incrementAndGet();

        registrarEvento(
                "🚗 " + coche.getNombre()
                        + " (" + coche.getTipo() + ") entra"
                        + " - Plaza " + coche.getPlaza()
        );

        return true;
    }


    // ENTRAR EN COLA
    private static boolean entrarEnCola(Coche coche) {

        synchronized (bloqueoCola) {

            if (colaEspera.size() >= CAPACIDAD_COLA) {

                return false;
            }

            colaEspera.addLast(coche);

            registrarEvento(
                    "⏳ " + coche.getNombre()
                            + " esperando en cola ("
                            + colaEspera.size()
                            + "/" + CAPACIDAD_COLA + ")"
            );

            return true;
        }
    }


    // ESPERAR EN COLA
    private static void esperarEnCola(Coche coche) {

        while (!Thread.currentThread().isInterrupted()) {

            boolean puedeIntentar = false;


            synchronized (bloqueoCola) {

                /*
                 * Buscamos el primer coche que pueda utilizar
                 * una plaza disponible.
                 *
                 * Esto evita que un coche NORMAL bloquee a un
                 * VIP cuando solamente queda una plaza VIP.
                 */
                Coche candidato = buscarCocheCompatible();

                if (candidato == coche) {

                    colaEspera.remove(coche);

                    puedeIntentar = true;
                }
            }


            if (puedeIntentar) {

                if (intentarEntrada(coche)) {

                    return;
                }


                /*
                 * No consiguió plaza porque otro coche pudo
                 * ocuparla mientras esperaba la barrera.
                 *
                 * Lo volvemos a poner al principio de la cola.
                 */
                synchronized (bloqueoCola) {

                    colaEspera.addFirst(coche);
                }
            }


            try {

                Thread.sleep(500);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                return;
            }
        }
    }


    // BUSCAR COCHE COMPATIBLE CON UNA PLAZA
    private static Coche buscarCocheCompatible() {

        boolean hayVIP =
                semaforoVIP.availablePermits() > 0;

        boolean hayNormal =
                semaforoNormales.availablePermits() > 0;


        for (Coche coche : colaEspera) {

            if (coche.getTipo() == TipoVehiculo.NORMAL
                    && hayNormal) {

                return coche;
            }

            if (coche.getTipo() == TipoVehiculo.VIP
                    && (hayVIP || hayNormal)) {

                return coche;
            }
        }

        return null;
    }


    // ESTANCIA DEL COCHE
    private static void realizarEstancia(Coche coche) {

        /*
         * Estancia aleatoria entre 10 y 30 segundos.
         */
        int segundos =
                10 + random.nextInt(21);

        try {

            Thread.sleep(segundos * 1000L);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }


        long tiempoEstancia =
                System.currentTimeMillis()
                        - coche.getHoraEntrada();

        tiempoEstanciaTotal.addAndGet(tiempoEstancia);


        // BARRERA DE SALIDA
        try {

            barreraSalida.acquire();

            try {

                // Procesa un coche cada segundo.
                Thread.sleep(1000);

            } finally {

                barreraSalida.release();
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }


        // CALCULAR PRECIO
        double minutos =
                tiempoEstancia / 60000.0;

        double precio =
                minutos
                        * coche.getTipo().getTarifaPorMinuto();

        long centimos =
                Math.round(precio * 100);

        ingresosCentimos.addAndGet(centimos);


        // LIBERAR PLAZA
        liberarPlaza(coche);


        int ocupacion =
                ocupacionActual.decrementAndGet();


        registrarEvento(
                "🚪 " + coche.getNombre()
                        + " (" + coche.getTipo() + ") sale"
                        + " - Plaza " + coche.getPlaza()
                        + " - Pagó: "
                        + String.format("%.2f€", precio)
        );
    }


    // LIBERAR PLAZA
    private static void liberarPlaza(Coche coche) {

        String plaza = coche.getPlaza();


        synchronized (bloqueoPlazas) {

            if (plaza.startsWith("V-")) {

                plazasVIP.addLast(plaza);

                semaforoVIP.release();

            } else {

                plazasNormales.addLast(plaza);

                semaforoNormales.release();
            }
        }
    }


    // DASHBOARD
    private static void mostrarDashboard() {

        int normalesOcupadas =
                PLAZAS_NORMALES
                        - semaforoNormales.availablePermits();

        int vipOcupadas =
                PLAZAS_VIP
                        - semaforoVIP.availablePermits();


        int cola;

        synchronized (bloqueoCola) {

            cola = colaEspera.size();
        }


        double ingresos =
                ingresosCentimos.get() / 100.0;


        System.out.println();
        System.out.println("=== PARKING INTELIGENTE ===");

        System.out.println(
                "🅿️ Estado actual: ["
                        + LocalTime.now().format(FORMATO_HORA)
                        + "]"
        );

        System.out.println(
                "┌────────────────────────────────────┐"
        );

        System.out.printf(
                "│ PLAZAS NORMALES: %2d/%-2d             │%n",
                normalesOcupadas,
                PLAZAS_NORMALES
        );

        System.out.printf(
                "│ PLAZAS VIP:      %2d/%-2d             │%n",
                vipOcupadas,
                PLAZAS_VIP
        );

        System.out.printf(
                "│ COLA DE ESPERA:  %2d/%-2d             │%n",
                cola,
                CAPACIDAD_COLA
        );

        System.out.printf(
                "│ INGRESOS HOY:              %8.2f€ │%n",
                ingresos
        );

        System.out.println(
                "└────────────────────────────────────┘"
        );


        System.out.println();
        System.out.println("Últimos eventos:");

        synchronized (bloqueoEventos) {

            for (String evento : ultimosEventos) {

                System.out.println(evento);
            }
        }
    }

    // REGISTRAR EVENTO
    private static void registrarEvento(String mensaje) {

        String evento =
                "["
                        + LocalTime.now().format(FORMATO_HORA)
                        + "] "
                        + mensaje;


        synchronized (bloqueoEventos) {

            if (ultimosEventos.size() >= 5) {

                ultimosEventos.removeFirst();
            }

            ultimosEventos.addLast(evento);
        }

        System.out.println(evento);
    }


    // ACTUALIZAR OCUPACIÓN MÁXIMA
    private static void actualizarOcupacionMaxima(
            int ocupacion) {

        int actual;

        do {

            actual = ocupacionMaxima.get();

            if (ocupacion <= actual) {

                return;
            }

        } while (!ocupacionMaxima.compareAndSet(
                actual,
                ocupacion
        ));
    }


    // RESUMEN FINAL
    private static void mostrarResumenFinal() {

        double porcentajeAtendidos =
                vehiculosProcesados.get() == 0
                        ? 0
                        : vehiculosAtendidos.get() * 100.0
                        / vehiculosProcesados.get();


        double tiempoMedio =
                vehiculosAtendidos.get() == 0
                        ? 0
                        : tiempoEstanciaTotal.get()
                        / (double) vehiculosAtendidos.get();


        double ingresos =
                ingresosCentimos.get() / 100.0;


        System.out.println();
        System.out.println("======================================");
        System.out.println("          RESUMEN DEL DÍA");
        System.out.println("======================================");

        System.out.println(
                "Vehículos procesados: "
                        + vehiculosProcesados.get()
        );

        System.out.println(
                "Vehículos atendidos: "
                        + vehiculosAtendidos.get()
                        + " ("
                        + String.format(
                        "%.1f",
                        porcentajeAtendidos
                )
                        + "%)"
        );

        System.out.println(
                "Vehículos rechazados: "
                        + vehiculosRechazados.get()
        );

        System.out.println(
                "Tiempo promedio de estancia: "
                        + String.format(
                        "%.1f",
                        tiempoMedio / 1000.0
                )
                        + "s"
        );

        System.out.println(
                "Ingresos totales: "
                        + String.format(
                        "%.2f€",
                        ingresos
                )
        );

        System.out.println(
                "Ocupación máxima: "
                        + ocupacionMaxima.get()
                        + "/25 plazas ("
                        + String.format(
                        "%.1f",
                        ocupacionMaxima.get()
                                * 100.0 / 25
                )
                        + "%)"
        );

        System.out.println("======================================");
    }
}