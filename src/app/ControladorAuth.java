package app;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import scaffoldj.Rol;
import scaffoldj.Controlador;
import scaffoldj.Servicios;
import scaffoldj.Solicitud;
import scaffoldj.Validacion.Validador;
import scaffoldj.Validacion.ValidadorNoVacio;

public class ControladorAuth implements Controlador{
    private final ServicioUsuarios servicioUsuarios;

    public ControladorAuth(Servicios servicios){
        this.servicioUsuarios = servicios.obtener(ServicioUsuarios.class);
    }
    @Override
    public String recurso(){
        return "auth";
    }
    @Override
    public Map<String, Function<Solicitud, String>> acciones(){
        return Map.of(
            "login", this::login,
            "perfil", this::perfil,
            "logout", this::logout
        );
    }
    @Override
    public Map <String, Rol> permisos(){
        return Map.of(
            "perfil", Rol.CLIENTE,
            "logout", Rol.CLIENTE
        );
    }
    @Override
    public  Map<String, List<Validador>> validadores(){
        return Map.of(
            "login", List.of(new ValidadorNoVacio(), new ValidadorNoVacio())
        );
    }
    private String login(Solicitud sol){
        Usuario u = servicioUsuarios.autenticar(sol.getParametro(0), sol.getParametro(1));
        sol.getSesion().iniciar(u.getUser(), u.getRol());
        return "Bienvenido " + u.getUser() + "(rol: " + u.getRol() + ")";
    }
    private String perfil (Solicitud sol){
        return "Usuario " + sol.getSesion().getUsuario() + " | Rol: " + sol.getSesion().getRol();
    }
    private String logout(Solicitud sol){
        String user = sol.getSesion().getUsuario();
        sol.getSesion().cerrar();
        return "Sesion cerrada para el user: " + user;
    }
    
}
