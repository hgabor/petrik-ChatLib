package hu.petrik.chatlib.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

class Client {
    private Socket socket;
    private ChatServer server;
    
    public Client(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    public void start() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                switch (line) {
                    case "/q":
                        socket.close();
                        return;
                        
                    default:
                        System.out.println("Client: " + line);
                        break;
                }
            }
        }
    }
    
    public void stop() throws IOException {
        socket.close();
    }
    
}
