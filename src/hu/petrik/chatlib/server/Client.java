package hu.petrik.chatlib.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

class Client {
    private Socket socket;
    private ChatServer server;
    private Object syncRoot = new Object();
    private OutputStreamWriter writer;
    
    public Client(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    public void start() throws IOException {
        writer = new OutputStreamWriter(socket.getOutputStream());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                switch (line) {
                    case "/q":
                        socket.close();
                        return;
                        
                    default:
                        server.send(line + "\n");
                        break;
                }
            }
        }
    }
    
    void send(String message) throws IOException {
        synchronized(syncRoot) {
            writer.write(message);
            writer.flush();
        }
    }
    
    public void stop() throws IOException {
        socket.close();
    }
}
