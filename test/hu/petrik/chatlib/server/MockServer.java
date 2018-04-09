package hu.petrik.chatlib.server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MockServer extends AbstractServer {

    public List<String> messages = new CopyOnWriteArrayList<>();
    
    @Override
    void send(String message) throws IOException {
        messages.add(message);
    }
    
}
