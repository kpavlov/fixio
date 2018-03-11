package fixio.examples.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractQuoteStreamingWorker implements Runnable {

    private static final int BUFFER_LENGTH = 32;

    private final BlockingQueue<Quote> quoteQueue;

    public AbstractQuoteStreamingWorker(BlockingQueue<Quote> quoteQueue) {
        this.quoteQueue = quoteQueue;
    }

    protected abstract void sendQuotes(List<Quote> buffer);

    @Override
    public void run() {
        final List<Quote> buffer = new ArrayList<>(BUFFER_LENGTH);
        //noinspection InfiniteLoopStatement
        while (true) {
            quoteQueue.drainTo(buffer, BUFFER_LENGTH);
            sendQuotes(buffer);
            buffer.clear();
        }
    }

}
