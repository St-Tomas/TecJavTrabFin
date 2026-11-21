package app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import scaffoldj.AccesoDenegadoException;
import scaffoldj.Rol;

public class ServicioUsuarios {
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    public ServicioUsuarios(){
        usuarios.put("admin", new Usuario("admin","1234", Rol.ADMINISTRADOR));
        usuarios.put("tomas", new Usuario("Tomas","1234", Rol.CLIENTE));
        usuarios.put("pity", new Usuario("Pity","1234", Rol.CLIENTE));
    }
    public Usuario autenticar(String user, String contraseña){
        Usuario u = usuarios.get(user.toLowerCase());
        if (u == null || !u.getContraseña().equals(contraseña)){
            throw new AccesoDenegadoException("Usuario o contraseña incorrectoss");
        }
        return u;
    }
}
