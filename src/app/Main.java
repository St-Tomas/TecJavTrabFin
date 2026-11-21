package app;

import scaffoldj.Server;

public class Main {
    public static void main(String[] args) {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : 6776;
        Server servidor = new Server(puerto);

        servidor.registrarServicio(ServicioUsuarios.class, new ServicioUsuarios());
        servidor.registrarServicio(ServicioInventario.class, new ServicioInventario());

        servidor.registrarControlador(new ControladorAuth(servidor.getServicios()));
        servidor.registrarControlador(new ControladorCatalogo(servidor.getServicios()));
        servidor.registrarControlador(new ControladorInventario(servidor.getServicios()));

        servidor.iniciar();
    }
}