package EjerciciosConcurrencia.Ejercicio3;

public class TransferenciaDeadlock {
    static class CuentaBancaria {
        private final String idCuenta;
        private int saldo;

        public CuentaBancaria(String idCuenta, int saldo) {
            this.idCuenta = idCuenta;
            this.saldo = saldo;
        }

        public void transferir(CuentaBancaria destino, int monto) {
            // Se bloquea primero la cuenta origen (this) y luego la destino
            synchronized (this) {
                System.out.println(Thread.currentThread().getName() + " -> Bloqueada cuenta " + idCuenta);
                try {
                    Thread.sleep(100); // Pausa para forzar que el otro hilo bloquee su cuenta
                } catch (InterruptedException ignored) {}

                synchronized (destino) {
                    System.out.println(Thread.currentThread().getName() + " -> Bloqueada cuenta destino " + destino.idCuenta);
                    this.saldo -= monto;
                    destino.saldo += monto;
                }
            }
        }
    }

    public static void main(String[] args) {
        CuentaBancaria cuentaA = new CuentaBancaria("ES01-A", 1000);
        CuentaBancaria cuentaB = new CuentaBancaria("ES02-B", 1000);

        // Hilo 1: Transfiere de A a B
        Thread t1 = new Thread(() -> cuentaA.transferir(cuentaB, 100), "Hilo-Transf-1");
        // Hilo 2: Transfiere de B a A (Orden inverso -> Deadlock)
        Thread t2 = new Thread(() -> cuentaB.transferir(cuentaA, 200), "Hilo-Transf-2");

        t1.start();
        t2.start();
    }
}
