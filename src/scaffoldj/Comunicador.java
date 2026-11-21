package scaffoldj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class Comunicador {

    private final BufferedReader in;
    private final PrintWriter out;

    public Comunicador(InputStream in, OutputStream out) {
        this.in = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this.out = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
    }
    public String recibir() throws IOException {
        return in.readLine();
    }
    public void enviar(String mensaje) {
        out.println(mensaje);
    }
}
