import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.net.BookieId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.UnknownHostException;

import static org.apache.bookkeeper.bookie.Cookie.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BookieImpl.class)
public class TestGenerateCookie {
    @Test
    public void generateCookieTest() throws UnknownHostException {
        PowerMockito.mockStatic(BookieImpl.class);
        when(BookieImpl.getBookieId(any())).thenReturn(BookieId.parse("mockedBookieId"));

        ServerConfiguration serverConfiguration = new ServerConfiguration();
        ServerConfiguration clientConfiguration = new ServerConfiguration(new ClientConfiguration());

        Builder serverBuilder = generateCookie(serverConfiguration);
        Assert.assertNotNull(serverBuilder);

        Builder clientBuilder = generateCookie(clientConfiguration);
        Assert.assertNotNull(clientBuilder);

        Builder nullBuilder = null;
        try {
            nullBuilder = generateCookie(null);
        }
        catch(Exception e){
            //some exception has been catched
        }
        Assert.assertNull(nullBuilder);

    }
}
