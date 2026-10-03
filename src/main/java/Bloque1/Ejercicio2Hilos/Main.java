package Bloque1.Ejercicio2Hilos;

public class Main {
    public static void main (String[] args){
        Thread t1 = new Thread(new ContadorRunnable());
        Thread t2 = new Thread(new ContadorRunnable());
        t1.start();
        t2.start();
    }
}
