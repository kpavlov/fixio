package fixio.netty.pipeline.client;

import org.junit.Before;
import org.junit.Test;

import java.net.PasswordAuthentication;
import java.util.Properties;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class PropertyAuthenticationProviderTest {

    private Properties properties;

    @Before
    public void beforeMethod() {
        properties = new Properties();
    }

    @Test
    public void testGetPasswordAuthentication() throws Exception {
        String username = randomAscii(10);
        String password = randomAscii(20);
        properties.setProperty("Username", username);
        properties.setProperty("Password", password);

        final PropertyAuthenticationProvider authenticationProvider = new PropertyAuthenticationProvider(properties);

        final PasswordAuthentication authentication = authenticationProvider.getPasswordAuthentication();
        assertThat(authentication, notNullValue());
        assertThat(authentication.getUserName(), is(username));
        assertThat(authentication.getPassword(), is(password.toCharArray()));
    }

    @Test
    public void testNoAuthentication() throws Exception {
        final PropertyAuthenticationProvider authenticationProvider = new PropertyAuthenticationProvider(properties);
        assertThat(authenticationProvider.getPasswordAuthentication(), nullValue());
    }
}