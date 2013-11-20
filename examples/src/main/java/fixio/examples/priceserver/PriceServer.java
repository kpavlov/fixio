package fixio.examples.priceserver;

import fixio.FixServer;

public class PriceServer {

    public static final int DEFAULT_PORT = 10101;

    private final PriceStreamingApp app;
    private final FixServer server;

    public PriceServer(int port) {
        app = new PriceStreamingApp();
        server = new FixServer(port, app);
    }

    public void start() throws InterruptedException {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public static void main(String[] args) throws InterruptedException {
        PriceServer priceServer = new PriceServer(DEFAULT_PORT);
        priceServer.start();
    }
}
