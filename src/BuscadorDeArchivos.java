import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BuscadorDeArchivos {

    private static final List<String> archivosEncontrados = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ingreso de directorio y archivo
        System.out.print("Ingrese el directorio donde quiere buscar: ");
        String rutaDirectorio = scanner.nextLine();
        System.out.print("Ingrese el nombre (o parte del nombre) del archivo a buscar: ");
        String nombreArchivo = scanner.nextLine();

        File directorio = new File(rutaDirectorio);

        if (!directorio.isDirectory()) {
            System.out.println("\n[------------------------------------]\n");
            System.out.println("El directorio ingresado no es válido.");
            System.out.println("\n[------------------------------------]\n");
            scanner.close();
            return;
        }

        // Hilo para buscar archivos
        Thread hiloBusqueda = new Thread(() -> buscarArchivo(directorio, nombreArchivo));
        hiloBusqueda.start();

        try {
            hiloBusqueda.join();
        } catch (InterruptedException e) {
            System.out.println("[------------------------------------]\n");
            System.out.println("Error: la búsqueda fue interrumpida.");
            System.out.println("\n[------------------------------------]");
        }

        // Mostrar los resultados
        if (archivosEncontrados.isEmpty()) {
            System.out.println("\n[------------------------------------]\n");
            System.out.println("No se encontraron archivos con ese nombre.");
            System.out.println("\n[------------------------------------]\n");
        } else {
            System.out.println("\n[------------------------------------]\n");
            System.out.println("Archivos encontrados:");
            archivosEncontrados.forEach(System.out::println);
            System.out.println("\n[------------------------------------]\n");
        }

        scanner.close();
    }

    private static void buscarArchivo(File directorio, String nombreArchivo) {
        File[] archivos = directorio.listFiles();

        if (archivos == null) {
            return;
        }

        for (File archivo : archivos) {
            if (archivo.isDirectory()) {
                // Nuevo hilo para buscar en subdirectorios
                Thread hiloSubDir = new Thread(() -> buscarArchivo(archivo, nombreArchivo));
                hiloSubDir.start();

                try {
                    hiloSubDir.join();
                } catch (InterruptedException e) {
                    System.out.println("\n[------------------------------------]\n");
                    System.out.println("La búsqueda en subdirectorio fue interrumpida.");
                    System.out.println("\n[------------------------------------]\n");
                }
            } else if (archivo.getName().toLowerCase().contains(nombreArchivo.toLowerCase())) {
                // Agregar archivo encontrado a la lista
                synchronized (archivosEncontrados) {
                    archivosEncontrados.add(archivo.getAbsolutePath());
                }
            }
        }
    }
}