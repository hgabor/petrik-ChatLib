package hu.petrik.chatlib.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * A chat szerveren belül egy kliens kapcsolatot kezel.
 */
class Client {
    private Socket socket;
    private AbstractServer server;
    private Object syncRoot = new Object();
    private OutputStreamWriter writer;
    private BufferedReader reader;
    
    /**
     * Létrehoz egy kliens objektumot a megadott socket-hez.
     * 
     * @param socket A kapcsolat a klienshez.
     * @param server A szerver objektum, amely létrehozta a klienst.
     */
    public Client(Socket socket, AbstractServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        writer = new OutputStreamWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public Client(OutputStream os, InputStream is, AbstractServer server) {
        writer = new OutputStreamWriter(os);
        reader = new BufferedReader(new InputStreamReader(is));
        this.server = server;
    }
    
    /**
     * Fogadja a kliens által küldött üzeneteket, és továbbítja a szervernek.
     * A függvény blokkoló. Csak egyszer hívjuk meg!
     * Ha kivételt dob, feltételezhetjük, hogy a kliens kapcsolat használhatatlan.
     * 
     * @throws IOException 
     */
    public void start() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            switch (line) {
                case "/q":
                    if (socket != null) socket.close();
                    return;

                default:
                    server.send(line + "\n");
                    break;
            }
        }
    }
    
    /**
     * Elküldi a megadott üzenetet a kliensnek.
     * A függvény szálbiztos.
     * 
     * @param message
     * @throws IOException 
     */
    void send(String message) throws IOException {
        synchronized(syncRoot) {
            writer.write(message);
            writer.flush();
        }
    }
    
    /**
     * Leállítja a szervert (a hatására a {@link #start()} függvény kivételt dob).
     * A függvény szálbiztos.
     * 
     * @throws IOException 
     */
    public void stop() throws IOException {
        if (socket != null) socket.close();
    }
}
