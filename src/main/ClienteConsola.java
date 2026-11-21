package main;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import scaffoldj.Comunicador;

public class ClienteConsola {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 6776);
             Scanner scanner = new Scanner(System.in)) {

            Comunicador com = new Comunicador(socket.getInputStream(), socket.getOutputStream());
            System.out.println("Servidor: " + com.recibir());

            while (true) {
                System.out.print("> ");
                String linea = scanner.nextLine();
                com.enviar(linea);

                String respuesta = com.recibir();
                if (respuesta == null) break;
                System.out.println("Servidor: " + respuesta);

                if (linea.trim().equalsIgnoreCase("salir")) break;
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}