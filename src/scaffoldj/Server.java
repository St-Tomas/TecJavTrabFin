package scaffoldj;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int puerto;
    private final Router router;
    private final Servicios servicios;

    public Server(int puerto){
        this.puerto = puerto;
        this.router = new Router();
        this.servicios = new Servicios();
    }
    public <T> void registrarServicio(Class<T> tipo, T servicio){
        servicios.registrar(tipo, servicio);
    }
    public Servicios getServicios(){
        return servicios;
    }
    public void registrarControlador(Controlador controlador){
        router.registrar(controlador);
    }
    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("El server se inicio en el puerto " + puerto);
            while (true) {
                Socket cliente = serverSocket.accept();
                Thread hilo = new Thread(() -> atenderCliente(cliente));
                hilo.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private void atenderCliente(Socket socket){
        Sesion sesion = new Sesion();
        ParsearSoli parser = new ParsearSoli();
        String id = socket.getRemoteSocketAddress().toString();
        System.out.println("Cliente conectado: " + id);

        try (socket) {
            Comunicador com = new Comunicador(socket.getInputStream(), socket.getOutputStream());
            com.enviar("OK|Conectado al sv. Ingresar 'recurso/accion[/params]' o 'salir'.");

            String linea;
            while ((linea = com.recibir()) != null) {
                if (linea.trim().equalsIgnoreCase("salir")) {
                    com.enviar("OK|Desconectado");
                    break;
                }
                try {
                    Solicitud sol = parser.parsear(linea, sesion);
                    String resultado = router.despachar(sol);
                    com.enviar("OK| " + resultado.replace("\n", " | "));
                } catch (ProtocoloException | FrameworkException e) {
                    com.enviar("ERROR| " + e.getMessage());
                } catch (Exception e) {
                    com.enviar("ERROR| Error inesperado del sv: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al atender al cliente " + id + ": " + e.getMessage());
        }
    }
}
