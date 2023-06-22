package cookie;

import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.net.BookieId;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import static org.apache.bookkeeper.bookie.Cookie.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(value= Parameterized.class)
public class TestGenerateCookie {

    private ServerConfiguration conf;

    @Parameters
    public static Collection<ServerConfiguration> getParameters(){
        return Arrays.asList(new ServerConfiguration(),
                new ServerConfiguration(new ClientConfiguration()),
                null,
                //caso di test inserito per aumentare il coverage
                new ServerConfiguration().setIndexDirName(new String[]{"index1","index2"}));
    }

    public TestGenerateCookie(ServerConfiguration conf){
        this.conf = conf;
    }

    @BeforeClass
    public static void setUpMock(){
        MockedStatic<BookieImpl> mockBook = Mockito.mockStatic(BookieImpl.class);
        mockBook.when(() -> BookieImpl.getBookieId(any()))
                .thenReturn(BookieId.parse("mockedBookieId"));
    }

    @Test
    public void generateCookieTest() throws UnknownHostException {

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
