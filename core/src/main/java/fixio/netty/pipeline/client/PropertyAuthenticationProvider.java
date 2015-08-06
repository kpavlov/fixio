package fixio.netty.pipeline.client;

import fixio.fixprotocol.FieldType;

import java.net.PasswordAuthentication;
import java.util.Properties;

public class PropertyAuthenticationProvider implements AuthenticationProvider {

    private final PasswordAuthentication authentication;

    public PropertyAuthenticationProvider(Properties properties) {
        String username = properties.getProperty(FieldType.Username.name());
        String password = properties.getProperty(FieldType.Password.name());

        if (username != null && password != null) {
            authentication = new PasswordAuthentication(username, password.toCharArray());
        } else {
            authentication = null;
        }
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }
}
