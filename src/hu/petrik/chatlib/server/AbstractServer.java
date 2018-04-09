package hu.petrik.chatlib.server;

import java.io.IOException;

public abstract class AbstractServer {
    
    public AbstractServer() {
    }

    /**
     * Elküldi a megadott üzenetet az összes kliensnek.
     * A függvény szálbiztos.
     *
     * @param message Az üzenet
     * @throws IOException
     */
    abstract void send(String message) throws IOException;
    
}
