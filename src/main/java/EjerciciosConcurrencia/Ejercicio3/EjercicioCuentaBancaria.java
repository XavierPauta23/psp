package EjerciciosConcurrencia.Ejercicio3;

public class EjercicioCuentaBancaria {
    // 1. VERSIÓN NO SINCRONIZADA (Provoca condición de carrera)
    static class CuentaRota {
        private int saldoActual = 0;

        public void depositar(int cantidad) {
            // Operación no atómica: Lectura -> Suma -> Escritura
            saldoActual = saldoActual + cantidad;
        }

        public int getSaldo() {
            return saldoActual;
        }
    }

    // 2. VERSIÓN CON METODO SYNCHRONIZED
    static class CuentaSincronizadaMetodo {
        private int saldoActual = 0;

        public synchronized void depositar(int cantidad) {
            saldoActual = saldoActual + cantidad;
        }

        public synchronized int getSaldo() {
            return saldoActual;
        }
    }

    // 3. VERSIÓN CON BLOQUE SYNCHRONIZED(THIS)
    static class CuentaSincronizadaBloque {
        private int saldoActual = 0;

        public void depositar(int cantidad) {
            synchronized (this) {
                saldoActual = saldoActual + cantidad;
            }
        }

        public int getSaldo() {
            synchronized (this) {
                return saldoActual;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int numHilos = 100;

        // Prueba 1: Demostración del fallo
        CuentaRota cuentaInsegura = new CuentaRota();
        ejecutarDepositosConcurrente(() -> cuentaInsegura.depositar(1), numHilos);
        System.out.println("Saldo final (SIN sincronizar, esperado 100): " + cuentaInsegura.getSaldo());

        // Prueba 2: Solución con metodo synchronized
        CuentaSincronizadaMetodo cuentaMetodo = new CuentaSincronizadaMetodo();
        ejecutarDepositosConcurrente(() -> cuentaMetodo.depositar(1), numHilos);
        System.out.println("Saldo final (Con método synchronized): " + cuentaMetodo.getSaldo());

        // Prueba 3: Solución con bloque synchronized(this)
        CuentaSincronizadaBloque cuentaBloque = new CuentaSincronizadaBloque();
        ejecutarDepositosConcurrente(() -> cuentaBloque.depositar(1), numHilos);
        System.out.println("Saldo final (Con bloque synchronized): " + cuentaBloque.getSaldo());
    }

    private static void ejecutarDepositosConcurrente(Runnable tarea, int cantidadHilos) throws InterruptedException {
        Thread[] grupoHilos = new Thread[cantidadHilos];
        for (int i = 0; i < cantidadHilos; i++) {
            grupoHilos[i] = new Thread(tarea);
            grupoHilos[i].start();
        }
        for (Thread hilo : grupoHilos) {
            hilo.join();
        }
    }
}
