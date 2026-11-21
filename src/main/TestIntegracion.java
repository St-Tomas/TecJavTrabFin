package main;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestIntegracion {

    private static final String HOST = "localhost";
    private static final int PUERTO = 6776;
    private static final String ARCHIVO_LOG = "test_log.txt";

    private static int total = 0;
    private static int pasados = 0;
    private static int fallados = 0;
    private static PrintWriter logFile;

    private static class ClienteTest implements AutoCloseable {
        final String nombre;
        final Socket socket;
        final BufferedReader in;
        final PrintWriter out;

        ClienteTest(String nombre) throws IOException {
            this.nombre = nombre;
            this.socket = new Socket(HOST, PUERTO);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            String bienvenida = in.readLine();
            escribirLog("[" + nombre + "] CONECTADO -> " + bienvenida);
        }

        String enviar(String comando) throws IOException {
            out.println(comando);
            return in.readLine();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }
    }

    public static void main(String[] args) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (PrintWriter fw = new PrintWriter(new FileWriter(ARCHIVO_LOG, StandardCharsets.UTF_8, false))) {
            logFile = fw;
            escribirLog("=================================================================");
            escribirLog("  TEST DE INTEGRACIÓN - ScaffoldJ (Ferretería)");
            escribirLog("  Fecha: " + fecha + " | Target: " + HOST + ":" + PUERTO);
            escribirLog("=================================================================\n");

            try (ClienteTest cliA = new ClienteTest("Cliente-A");
                 ClienteTest cliB = new ClienteTest("Cliente-B")) {

                // 1. PARSER Y PROTOCOLO
                seccion("1. PARSER Y ERRORES DE PROTOCOLO");
                probar(cliA, "Línea vacía", "", "ERROR|", null);
                probar(cliA, "Sin barra separadora", "hola", "ERROR|", null);
                probar(cliA, "Segmento vacío (//)", "catalogo//listar", "ERROR|", null);
                probar(cliA, "Recurso inexistente", "autos/listar", "ERROR|", null);
                probar(cliA, "Acción inexistente", "catalogo/borrar", "ERROR|", null);

                // 2. SEGURIDAD, SESIÓN Y ROLES (CLIENTE)
                seccion("2. SEGURIDAD, SESIÓN Y ROL CLIENTE");
                probar(cliA, "Acceso sin login (INVITADO)", "catalogo/listar", "ERROR|", null);
                probar(cliA, "Login contraseña incorrecta", "auth/login/tomas/9999", "ERROR|", null);
                probar(cliA, "Login válido como CLIENTE", "auth/login/tomas/1234", "OK|", "CLIENTE");
                probar(cliA, "Consultar perfil en sesión", "auth/perfil", "OK|", "Tomas");
                probar(cliA, "Listar catálogo como CLIENTE", "catalogo/listar", "OK|", "MART-01");
                probar(cliA, "Buscar producto por texto", "catalogo/buscar/taladro", "OK|", "TALA-03");
                probar(cliA, "Verificar stock por código", "catalogo/stock/MART-01", "OK|", "DISPONIBLE");
                probar(cliA, "Bloqueo acción ADMIN (precio)", "inventario/precio/MART-01/99999", "ERROR|", null);
                probar(cliA, "Bloqueo acción ADMIN (alta)", "inventario/alta/TEST-99/Prueba/Herramientas/1000/5", "ERROR|", null);

                // 3. VALIDADORES REUTILIZABLES (DESDE CLIENTE B COMO ADMIN)
                seccion("3. VALIDADORES Y REGLAS DE NEGOCIO (ADMIN)");
                probar(cliB, "Login válido como ADMINISTRADOR", "auth/login/admin/1234", "OK|", "ADMINISTRADOR");
                probar(cliB, "Cantidad incorrecta de parámetros", "inventario/alta/AMOL-06/Amoladora", "ERROR|", null);
                probar(cliB, "ValidadorLargoMaximo (>40 chars)", "inventario/alta/AMOL-06/NombreDeProductoDemasiadoLargoQueSuperaLosCuarentaCaracteres/Herramientas/45000/5", "ERROR|", null);
                probar(cliB, "ValidadorDecimalPositivo (negativo)", "inventario/alta/AMOL-06/Amoladora Angular/Herramientas/-500/5", "ERROR|", null);
                probar(cliB, "ValidadorDecimalPositivo (texto)", "inventario/alta/AMOL-06/Amoladora Angular/Herramientas/caro/5", "ERROR|", null);
                probar(cliB, "ValidadorEnteroPositivo (cero)", "inventario/alta/AMOL-06/Amoladora Angular/Herramientas/45000/0", "ERROR|", null);
                probar(cliB, "ValidadorEnteroPositivo (texto)", "inventario/alta/AMOL-06/Amoladora Angular/Herramientas/45000/diez", "ERROR|", null);
                probar(cliB, "ValidadorEntero (delta texto)", "inventario/stock/MART-01/muchos", "ERROR|", null);
                probar(cliB, "Stock insuficiente (-999)", "inventario/stock/MART-01/-999", "ERROR|", null);

                // 4. CONCURRENCIA Y SERVICIO COMPARTIDO ENTRE HILOS
                seccion("4. CRUD ADMIN + SERVICIO COMPARTIDO ENTRE CLIENTES");
                probar(cliB, "Alta de producto nuevo", "inventario/alta/AMOL-06/Amoladora Angular 750W/Electricas/62500/8", "OK|", "AMOL-06");
                probar(cliB, "Rechazo de código duplicado", "inventario/alta/AMOL-06/Duplicado/Electricas/1000/2", "ERROR|", null);
                probar(cliB, "Modificar precio de MART-01", "inventario/precio/MART-01/18000", "OK|", "18000");
                probar(cliB, "Restar 2 unidades a MART-01", "inventario/stock/MART-01/-2", "OK|", "10");

                // Cliente A (otro hilo) consulta los cambios hechos por Cliente B
                probar(cliA, "Cliente-A ve producto nuevo (AMOL-06)", "catalogo/stock/AMOL-06", "OK|", "62500");
                probar(cliA, "Cliente-A ve precio y stock actualizados", "catalogo/stock/MART-01", "OK|", "10 u.");

                // 5. LOGOUT, AISLAMIENTO DE SESIÓN Y DESCONEXIÓN
                seccion("5. LOGOUT, AISLAMIENTO DE SESIÓN Y CIERRE");
                probar(cliB, "Logout de Cliente-B", "auth/logout", "OK|", null);
                probar(cliB, "Cliente-B ya no tiene permisos ADMIN", "inventario/precio/MART-01/100", "ERROR|", null);
                probar(cliB, "Desconexión limpia de Cliente-B", "salir", "OK|", null);

                // Cliente A sigue intacto tras el logout/cierre de Cliente B
                probar(cliA, "Cliente-A mantiene su sesión activa", "auth/perfil", "OK|", "Tomas");
                probar(cliA, "Desconexión limpia de Cliente-A", "salir", "OK|", null);
            }

            escribirLog("\n=================================================================");
            escribirLog("  RESUMEN FINAL: " + pasados + "/" + total + " PASADOS | " + fallados + " FALLADOS");
            escribirLog("  Log guardado en: " + ARCHIVO_LOG);
            escribirLog("=================================================================");

        } catch (IOException e) {
            System.err.println("Error ejecutando el test (¿está corriendo app.Main en el puerto " + PUERTO + "?): " + e.getMessage());
        }
    }

    private static void seccion(String titulo) {
        escribirLog("\n--- " + titulo + " ---");
    }

    private static void probar(ClienteTest cliente, String descripcion, String comando, String prefijoEsperado, String textoEsperado) throws IOException {
        total++;
        String respuesta = cliente.enviar(comando);

        boolean okPrefijo = respuesta != null && respuesta.startsWith(prefijoEsperado);
        boolean okTexto = textoEsperado == null || (respuesta != null && respuesta.contains(textoEsperado));
        boolean paso = okPrefijo && okTexto;

        if (paso) {
            pasados++;
        } else {
            fallados++;
        }

        String estado = paso ? "[PASS]" : "[FAIL]";
        escribirLog(estado + " (" + cliente.nombre + ") " + descripcion);
        escribirLog("   ENVIADO  >> " + (comando.isEmpty() ? "<vacío>" : comando));
        escribirLog("   RECIBIDO << " + respuesta);
        if (!paso) {
            escribirLog("   ESPERADO !! Prefijo '" + prefijoEsperado + "'" + (textoEsperado != null ? " y contener '" + textoEsperado + "'" : ""));
        }
    }

    private static void escribirLog(String linea) {
        System.out.println(linea);
        if (logFile != null) {
            logFile.println(linea);
            logFile.flush();
        }
    }
}