package hu.petrik.chatlib.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ChatClient {
    private String ip;
    private int port;
    private Socket socket;
    private OutputStreamWriter writer;
    private BufferedReader reader;

    public ChatClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    public void connect() throws IOException {
        socket = new Socket(InetAddress.getByName(ip), port);
        OutputStream stream = socket.getOutputStream();
        writer = new OutputStreamWriter(stream);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        startListening();
    }
    
    public void sendMessage(String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();

        if ("/q".equals(message)) {
            socket.close();
            return;
        }
    }
    
    public void close() throws IOException {
        sendMessage("/q");
    }
    
    private void startListening() throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        for (MessageReceivedListener listener: receivedListeners) {
                            listener.messageReceived(line);
                        }
                    }
                }
                catch (IOException ex) {
                    for (MessageReceivedListener listener: receivedListeners) {
                        listener.error(ex);
                    }
                }
            }
        });
        thread.start();
    }
    
    public interface MessageReceivedListener {
        public void messageReceived(String message);
        public void error(IOException ex);
    }
    private List<MessageReceivedListener> receivedListeners = new ArrayList<>();
    
    public void addMessageReceivedListener(MessageReceivedListener listener) {
        receivedListeners.add(listener);
    }
    public void removeMessageReceivedListener(MessageReceivedListener listener) {
        receivedListeners.remove(listener);
    }
    
    
    
    private void writeToConsole() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
    
    private void readFromConsole() throws IOException {
        Scanner sc = new Scanner(System.in);
        String line;
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
