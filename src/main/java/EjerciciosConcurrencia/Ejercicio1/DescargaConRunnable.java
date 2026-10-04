package EjerciciosConcurrencia.Ejercicio1;

public class DescargaConRunnable implements Runnable{
    private final String nombreArchivo;

    public DescargaConRunnable(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    @Override
    public void run() {
        for (int progreso = 20; progreso <= 100; progreso += 20) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return;
            }
            System.out.println(Thread.currentThread().getName() + " -> "
                    + nombreArchivo + ": " + progreso + "% completado");
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 4; i++) {
            Runnable tarea = new DescargaConRunnable("Documento_" + i);

            // Uso de la API moderna de Java 21+ para la creación e inicio de hilos
            Thread.ofPlatform()
                    .name("descarga-runnable-", i)
                    .start(tarea);
        }
    }
}
