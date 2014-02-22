package fixio.netty.pipeline.client;

/**
 * Created with IntelliJ IDEA.
 * User: maestro
 * Date: 22.02.14
 * Time: 7:45
 * To change this template use File | Settings | File Templates.
 */
public class StatelessMessageSequenceProvider {
    private static StatelessMessageSequenceProvider ourInstance = new StatelessMessageSequenceProvider();

    public static StatelessMessageSequenceProvider getInstance() {
        return ourInstance;
    }

    private StatelessMessageSequenceProvider() {
    }
}
