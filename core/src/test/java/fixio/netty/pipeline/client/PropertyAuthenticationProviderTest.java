package fixio.netty.pipeline.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.PasswordAuthentication;
import java.util.Properties;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.assertj.core.api.Assertions.assertThat;

class PropertyAuthenticationProviderTest {

    private Properties properties;

    @BeforeEach
    void beforeMethod() {
        properties = new Properties();
    }

    @Test
    void getPasswordAuthentication() {
        String username = randomAscii(10);
        String password = randomAscii(20);
        properties.setProperty("Username", username);
        properties.setProperty("Password", password);

        final PropertyAuthenticationProvider authenticationProvider = new PropertyAuthenticationProvider(properties);

        final PasswordAuthentication authentication = authenticationProvider.getPasswordAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getUserName()).isEqualTo(username);
        assertThat(authentication.getPassword()).isEqualTo(password.toCharArray());
    }

    @Test
    void noAuthentication() {
        final PropertyAuthenticationProvider authenticationProvider = new PropertyAuthenticationProvider(properties);
        assertThat(authenticationProvider.getPasswordAuthentication()).isNull();
    }
}
