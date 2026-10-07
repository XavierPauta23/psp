package EjerciciosConcurrencia.Ejercicio3;

public class TransferenciaSegura {
    static class CuentaBancaria {
        private final int idUnico;
        private int saldo;

        public CuentaBancaria(int idUnico, int saldo) {
            this.idUnico = idUnico;
            this.saldo = saldo;
        }

        public void transferirSegura(CuentaBancaria destino, int monto) {
            // Se determina el orden global de los bloqueos basándonos en el ID
            CuentaBancaria primera = esteIdMenor(destino) ? this : destino;
            CuentaBancaria segunda = esteIdMenor(destino) ? destino : this;

            synchronized (primera) {
                synchronized (segunda) {
                    this.saldo -= monto;
                    destino.saldo += monto;
                    System.out.println(Thread.currentThread().getName() + " -> Transferencia completada de "
                            + this.idUnico + " a " + destino.idUnico + " por " + monto + "€");
                }
            }
        }

        private boolean esteIdMenor(CuentaBancaria otra) {
            return this.idUnico < otra.idUnico;
        }

        public int getSaldo() {
            return saldo;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CuentaBancaria cuentaA = new CuentaBancaria(101, 1000);
        CuentaBancaria cuentaB = new CuentaBancaria(102, 1000);

        // Hilo 1: A a B
        Thread t1 = new Thread(() -> cuentaA.transferirSegura(cuentaB, 100), "Hilo-Transf-1");
        // Hilo 2: B a A (Misma jerarquía de bloqueo -> Sin Deadlock)
        Thread t2 = new Thread(() -> cuentaB.transferirSegura(cuentaA, 200), "Hilo-Transf-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Saldo A final: " + cuentaA.getSaldo());
        System.out.println("Saldo B final: " + cuentaB.getSaldo());
    }
}
