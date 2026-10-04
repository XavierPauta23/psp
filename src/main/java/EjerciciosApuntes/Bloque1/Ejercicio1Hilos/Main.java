package EjerciciosApuntes.Bloque1.Ejercicio1Hilos;

public class Main {

    public static void main(String[] args){
        //Crear dos hilos
        ContadorThread hilo1 = new ContadorThread();
        ContadorThread hilo2 = new ContadorThread();

        //Arrancar hilos
        hilo1.start();
        hilo2.start();
    }
}
