package Ejercicio3Hilos;

public class Main {
    public static void main(String[] args) {
        // Creamos la instancia del hilo
        Thread miHilo = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Ejecutando tarea en el hilo: "
                        + Thread.currentThread().getName());
            }
        });

        System.out.println("Hilo principal antes de ejecutar: "
                + Thread.currentThread().getName());

        // Llamada incorrecta: llamamos directamente a run()
        miHilo.run();

        System.out.println("Hilo principal después de ejecutar: "
                + Thread.currentThread().getName());

        // RESPUESTA PREGUNTA: Cuando se invoca run() en lugar de start(), el metodo se ejecuta como cualquier otro dentro
        // de una clase. Entonces, no ocurre ninguna interaccion con el sistema operativo. Al contrario de si se usa el
        // metodo start(), que este es el encargado de realizar la tarea pesada, solicitando al sistema operativo la
        // asignación de recursos y un nuevo contexto de pila.
    }
}
