package Ejercicio1;

public class DescargaConThread extends Thread{
    private final String nombreArchivo;

    public DescargaConThread(String nombreArchivo) {
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
            Thread hiloDescarga = new DescargaConThread("Archivo_" + i);
            hiloDescarga.setName("hilo-thread-" + i);
            hiloDescarga.start();
        }
    }
}
