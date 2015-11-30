package fixio.examples.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractQouteStreamingWorker implements Runnable {

    public static final int BUFFER_LENGTH = 32;

    private volatile boolean stopping;
    private final BlockingQueue<Quote> quoteQueue;

    public AbstractQouteStreamingWorker(BlockingQueue<Quote> quoteQueue) {
        this.quoteQueue = quoteQueue;
    }

    protected abstract void sendQuotes(List<Quote> buffer);

    @Override
    public void run() {
        final List<Quote> buffer = new ArrayList<>(BUFFER_LENGTH);
        while (!stopping) {
            quoteQueue.drainTo(buffer, BUFFER_LENGTH);
            sendQuotes(buffer);
            buffer.clear();
        }
    }

    public void stopWorker() {
        stopping = true;
    }

}
