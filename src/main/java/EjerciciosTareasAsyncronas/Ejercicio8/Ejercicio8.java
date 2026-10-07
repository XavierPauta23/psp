package EjerciciosTareasAsyncronas.Ejercicio8;

import java.util.concurrent.CompletableFuture;


public class Ejercicio8 {

    public static CompletableFuture<Integer> consultarTemperatura() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return 25;
        });
    }

    public static CompletableFuture<Integer> consultarHumedad() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return 60;
        });
    }

    public static void main(String[] args) {

        CompletableFuture<Integer> temperatura = consultarTemperatura();
        CompletableFuture<Integer> humedad = consultarHumedad();

        CompletableFuture<String> resultado = temperatura.thenCombine(
                humedad,
                (temp, hum) -> "Temperatura: " + temp + " ºC\n" +
                        "Humedad: " + hum + " %"
        );

        System.out.println(resultado.join());
    }

    //No debemos hacer join() antes de combinar porque estaríamos esperando manualmente
    // a que terminen las tareas. thenCombine() está diseñado para esperar a que ambos CompletableFuture
    //terminen y combinar sus resultados, manteniendo las tareas independientes y ejecutándose en paralelo.
}