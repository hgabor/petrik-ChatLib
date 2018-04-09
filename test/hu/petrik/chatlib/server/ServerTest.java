package hu.petrik.chatlib.server;

import hu.petrik.chatlib.client.ChatClient;
import hu.petrik.chatlib.server.ChatServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServerTest {
    private ChatServer server;
    
    public ServerTest() {
    }
    
    @Before
    public void setUp() throws InterruptedException {
        server = new ChatServer("127.0.0.1", 45000);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.listen();
                } catch (IOException ex) {}
            }
        });
        thread.start();
        Thread.sleep(200);
    }
    
    @After
    public void tearDown() throws IOException {
        server.stop();
    }

    @Test
    public void initialClientCountZero() {
        int clientCount = server.getClientCount();
        assertEquals("A csatlakozott kliensek szama nem 0", 0, clientCount);
    }
    
    @Test
    public void clientCount() throws IOException, InterruptedException {
        newClient();
        assertEquals("Egy kliensnek kellene lennie", 1, server.getClientCount());
        newClient();
        assertEquals("Ket kliensnek kellene lennie", 2, server.getClientCount());
    }
    
    public void newClient() throws IOException, InterruptedException {
        ChatClient client = new ChatClient("127.0.0.1", 45000);
        client.connect();
        Thread.sleep(200);
    }
}
