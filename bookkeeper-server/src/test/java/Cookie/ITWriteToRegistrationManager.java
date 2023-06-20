package Cookie;

import org.apache.bookkeeper.bookie.BookieException;
import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.versioning.LongVersion;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.UnknownHostException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BookieImpl.class)
public class ITWriteToRegistrationManager {

    private ServerConfiguration testServConf;
    private GetBuilderUtil getBuilderUtil;
    private Builder myBuilder;
    private Cookie testCookie;
    private int testVersion = 5;
    private LongVersion testLongVersion;
    private ZKRegistrationManager zkRegistrationManager;

    @Before
    public void setUp() {
        getBuilderUtil = new GetBuilderUtil();
        myBuilder = getBuilderUtil.getBuilder();
        testCookie = myBuilder.build();
        testLongVersion = new LongVersion(this.testVersion);
        testServConf = new ServerConfiguration();
        testServConf.setBookieId(getBuilderUtil.getBookieId());
    }

    @Test
    public void allMockedTest() throws UnknownHostException, BookieException {
        //qui mocko tutti i messaggi dalla classe Cookie alle altre classi.
        //In particolare mocko la chiamata a metodo di BookImpl getBookieId()
        //e a metodo di ZKRegistrationManager writeCookie()
        ZKRegistrationManager mockedZKRegMan = mock(ZKRegistrationManager.class);


        BookieId testBookieId = BookieId.parse(getBuilderUtil.getBookieId());
        PowerMockito.mockStatic(BookieImpl.class);
        when(BookieImpl.getBookieId(any())).thenReturn(testBookieId);
        doNothing().when(mockedZKRegMan).writeCookie(eq(testBookieId), any());

        testCookie.writeToRegistrationManager(mockedZKRegMan, testServConf, testLongVersion);

        verify(mockedZKRegMan).writeCookie(eq(testBookieId), any());
        PowerMockito.verifyStatic(BookieImpl.class);
        BookieImpl.getBookieId(any());
    }

    @Test
    public void mockedWriteCookieTest() throws BookieException {
        //qui "rilascio" il mock su BookImpl sul metodo getBookieId(), lascio il mock su
        //ZKRegistrationManager writeCookie()

        ZKRegistrationManager mockedZKRegMan = mock(ZKRegistrationManager.class);
        String bookieId = getBuilderUtil.getBookieId();
        BookieId testBookieId = BookieId.parse(bookieId);

        doNothing().when(mockedZKRegMan).writeCookie(eq(testBookieId), any());

        testCookie.writeToRegistrationManager(mockedZKRegMan, testServConf, testLongVersion);

        verify(mockedZKRegMan).writeCookie(eq(testBookieId), any());
    }

    @Test
    public void writeToRegistrationManagerTest() throws BookieException, InterruptedException, KeeperException {

        ZooKeeper mockedZook = mock(ZooKeeper.class);
        String bookieId = "/ledgers/cookies/" + getBuilderUtil.getBookieId();

        when(mockedZook.setData(eq(bookieId), any(), anyInt())).thenReturn(null);
        zkRegistrationManager = new ZKRegistrationManager(testServConf, mockedZook);

        testCookie.writeToRegistrationManager(zkRegistrationManager, testServConf, testLongVersion);

        verify(mockedZook).setData(eq(bookieId), any(), anyInt());
    }
}