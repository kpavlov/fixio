package fixio.examples.priceclient;

import fixio.FixClient;
import fixio.examples.priceserver.PriceServer;
import io.netty.channel.ChannelFuture;

public class PriceClient {

    private final PriceReadingApp app;
    private final FixClient client;
    private final int port;

    public PriceClient(int port) {
        this.port = port;
        app = new PriceReadingApp();
        client = new FixClient(app);
        client.setSettingsResource("/client.properties");
    }

    public static void main(String[] args) throws InterruptedException {
        PriceClient priceClient = new PriceClient(PriceServer.DEFAULT_PORT);
        priceClient.connect().sync();
    }

    public ChannelFuture connect() throws InterruptedException {
        return client.connect(port);
    }
}
