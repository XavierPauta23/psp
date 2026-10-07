package EjerciciosConcurrencia.Ejercicio7;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConsultasApi {

    public static String consultarAPI(int numero) throws InterruptedException {

        Random random = new Random();

        int tiempo = 1000 + random.nextInt(2001);

        Thread.sleep(tiempo);

        return "Respuesta de la API " + numero;
    }

    public static void main(String[] args) throws Exception {

        List<Callable<String>> tareas = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {

            int numero = i;

            tareas.add(() -> consultarAPI(numero));
        }

        try (ExecutorService executor = Executors.newFixedThreadPool(5)) {

            List<Future<String>> resultados = executor.invokeAll(tareas);

            for (Future<String> resultado : resultados) {
                System.out.println(resultado.get());
            }
        }

        System.out.println("Todas las consultas han terminado.");
    }
}