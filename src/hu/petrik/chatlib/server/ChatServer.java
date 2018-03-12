package hu.petrik.chatlib.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private String ip;
    private int port;
    private List<Client> clients = new CopyOnWriteArrayList<>();
    private ServerSocket socket;
    
    public ChatServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    public void listen() throws IOException {
        socket = new ServerSocket(port, -1, InetAddress.getByName(ip));
        Socket connection;
        while (true) {
            connection = socket.accept();
            
            Client client = new Client(connection, this);
            clients.add(client);
            
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Client connected");
                        client.start();
                    }
                    catch (IOException ex) {
                        System.out.println("Client disconnected");
                    }
                    finally {
                        clients.remove(client);
                    }
                }
            });
            t.start();
            
        }
    }
    
    public void stop() throws IOException {
        socket.close();
        for (Client c: clients) {
            c.stop();
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        ChatServer server = new ChatServer("0.0.0.0", 45000);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.listen();
                } catch (IOException ex) {
                    System.out.println("Server stopped");
                }
            }
        });
        t.start();
        
        System.out.println("Press ENTER to stop the server");
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
        server.stop();
        Thread.sleep(5000);
    }
    
}
