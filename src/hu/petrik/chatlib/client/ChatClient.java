package hu.petrik.chatlib.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class ChatClient {
    private String ip;
    private int port;
    private Socket socket;

    public ChatClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    public void connect() throws IOException {
        socket = new Socket(InetAddress.getByName(ip), port);
    }
    
    private void writeToConsole() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
    
    private void readFromConsole() throws IOException {
        Scanner sc = new Scanner(System.in);
        String line;
        OutputStream stream = socket.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        while ((line = sc.nextLine()) != null) {
            writer.write(line + "\n");
            writer.flush();
            
            if ("/q".equals(line)) {
                socket.close();
                return;
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 45000);
        client.connect();
        
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.writeToConsole();
                } catch (IOException ex) {
                    System.out.println("Disconnected");
                }
                System.exit(0);
            }
        });
        System.out.println("Type to chat, type /q to quit");
        t.start();
        
        client.readFromConsole();
    }
}
