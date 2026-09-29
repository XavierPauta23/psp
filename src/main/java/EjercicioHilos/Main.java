package EjercicioHilos;

public class Main {

    public static void main(String[] args){

        ContadorThread hilo1 = new ContadorThread();
        ContadorThread hilo2 = new ContadorThread();


        hilo1.start();
        hilo2.start();
    }
}
