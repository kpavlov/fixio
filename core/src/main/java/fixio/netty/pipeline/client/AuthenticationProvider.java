package fixio.netty.pipeline.client;

import java.net.PasswordAuthentication;

/**
 * Provides {@link PasswordAuthentication} used by {@link fixio.FixClient} login process.
 */
public interface AuthenticationProvider {

    PasswordAuthentication getPasswordAuthentication();
}
