package fixio;

import org.junit.Before;
import org.junit.Test;

public class FixServerTest {

    private FixServer server;

    @Before
    public void setUp() throws Exception {
        server = new FixServer(10100);
    }

    @Test
    public void testStartStop() throws Exception {
        server.start();
        server.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void testStopBeforeStart() throws Exception {
        server.stop();
    }
}
