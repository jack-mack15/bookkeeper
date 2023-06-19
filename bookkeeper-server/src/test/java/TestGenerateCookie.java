import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.conf.AbstractConfiguration;
import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.net.BookieId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import static org.apache.bookkeeper.bookie.Cookie.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(BookieImpl.class)
public class TestGenerateCookie {

    private ServerConfiguration conf;

    @Parameters
    public static Collection<ServerConfiguration> getParameters(){
        return Arrays.asList(new ServerConfiguration(),
                new ServerConfiguration(new ClientConfiguration()),
                null,
                //per aumentare il coverage
                new ServerConfiguration().setIndexDirName(new String[]{"index1","index2"}));
    }

    public TestGenerateCookie(ServerConfiguration conf){
        this.conf = conf;
    }

    @Test
    public void generateCookieTest() throws UnknownHostException {
        PowerMockito.mockStatic(BookieImpl.class);
        when(BookieImpl.getBookieId(any())).thenReturn(BookieId.parse("mockedBookieId"));
        Builder testBuilder = null;

        try {
            testBuilder = generateCookie(conf);
            Assert.assertNotNull(testBuilder);
        }
        catch (NullPointerException e){
            Assert.assertNull(testBuilder);
        }
    }
}
