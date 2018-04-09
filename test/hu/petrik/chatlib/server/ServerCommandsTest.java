package hu.petrik.chatlib.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServerCommandsTest {
    
    private Client client;
    private ByteArrayInputStream input;
    private ByteArrayOutputStream output;
    private MockServer server;
    
    @Before
    public void setUp() {
        output = new ByteArrayOutputStream();
        server = new MockServer();
    }
    
    public void startClient(String messages) throws IOException {
        input = new ByteArrayInputStream(messages.getBytes());
        client = new Client(output, input, server);
        client.start();
    }
    
    @After
    public void tearDown() throws IOException {
        client.stop();
    }
    
    @Test
    public void sendMessage() throws InterruptedException, IOException {
        startClient("Hello world!");
        Thread.sleep(200);
        assertEquals("1 uzenetet kellet, hogy kapjon", 1, server.messages.size());
        assertEquals("1 uzenetet kellet, hogy kapjon", "Hello world!\n", server.messages.get(0));
    }
}
