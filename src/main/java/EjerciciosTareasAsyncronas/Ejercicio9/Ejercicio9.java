package EjerciciosTareasAsyncronas.Ejercicio9;

import java.util.concurrent.CompletableFuture;

public class Ejercicio9 {

    public static CompletableFuture<Void> descargarArchivo(String nombre, int segundos) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(segundos * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println(nombre + " descargado");
        });
    }

    public static void main(String[] args) {

        CompletableFuture<Void> archivo1 = descargarArchivo("Archivo 1", 2);
        CompletableFuture<Void> archivo2 = descargarArchivo("Archivo 2", 1);
        CompletableFuture<Void> archivo3 = descargarArchivo("Archivo 3", 3);
        CompletableFuture<Void> archivo4 = descargarArchivo("Archivo 4", 2);
        CompletableFuture<Void> archivo5 = descargarArchivo("Archivo 5", 1);

        CompletableFuture<Void> todasLasDescargas = CompletableFuture.allOf(
                archivo1,
                archivo2,
                archivo3,
                archivo4,
                archivo5
        );

        todasLasDescargas.join();

        System.out.println("Todas las descargas han terminado");
    }
}