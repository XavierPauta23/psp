package EjerciciosApuntes.Bloque1.Ejercicio1Hilos;

public class ContadorThread extends Thread{

    @Override
    public void run(){
        for (int i = 1; i<= 10; i++){
            System.out.println(i);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("El hilo se ha interrumpido");
            }
        }
    }
}
