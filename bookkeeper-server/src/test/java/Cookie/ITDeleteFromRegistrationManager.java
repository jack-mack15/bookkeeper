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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.apache.zookeeper.ZooKeeper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.UnknownHostException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest(BookieImpl.class)
public class ITDeleteFromRegistrationManager {

    private ServerConfiguration testServConf;
    private GetBuilderUtil getBuilderUtil;
    private Builder myBuilder;
    private Cookie testCookie;
    private int testVersion = 5;
    private LongVersion testLongVersion;
    private ZKRegistrationManager zkRegistrationManager;

    @Before
    public void setUp(){
        getBuilderUtil = new GetBuilderUtil();
        myBuilder = getBuilderUtil.getBuilder();
        testCookie = myBuilder.build();
        testLongVersion = new LongVersion(this.testVersion);
        testServConf = new ServerConfiguration();
        testServConf.setBookieId(getBuilderUtil.getBookieId());
    }

    @Test
    public void allMockedTest() throws BookieException, UnknownHostException {
        //qui mocko tutti i messaggi dalla classe Cookie alle altre classi.
        //In particolare mocko la chiamata a metodo di BookImpl getBookieId()
        //e a metodo di ZKRegistrationManager removeCookie()

        ZKRegistrationManager mockedZKRegMan = mock(ZKRegistrationManager.class);


        BookieId testBookieid = BookieId.parse(getBuilderUtil.getBookieId());
        PowerMockito.mockStatic(BookieImpl.class);
        when(BookieImpl.getBookieId(any())).thenReturn(testBookieid);

        doNothing().when(mockedZKRegMan).removeCookie(testBookieid,testLongVersion);

        testCookie.deleteFromRegistrationManager(mockedZKRegMan, testServConf, testLongVersion);

        Mockito.verify(mockedZKRegMan).removeCookie(testBookieid,testLongVersion);
        PowerMockito.verifyStatic(BookieImpl.class);
        BookieImpl.getBookieId(any());
    }

    @Test
    public void mockedRemoveCookieTest() throws BookieException {
        //qui "rilascio" il mock su BookImpl sul metodo getBookieId(), lascio il mock su
        //ZKRegistrationManager removeCookie()

        ZKRegistrationManager mockedZKRegMan = mock(ZKRegistrationManager.class);
        BookieId testBookieid = BookieId.parse(getBuilderUtil.getBookieId());

        doNothing().when(mockedZKRegMan).removeCookie(testBookieid,testLongVersion);

        testCookie.deleteFromRegistrationManager(mockedZKRegMan, testServConf, testLongVersion);

        Mockito.verify(mockedZKRegMan).removeCookie(testBookieid,testLongVersion);
    }


    @Test
    public void almostCompleteTest() throws InterruptedException, KeeperException, BookieException {
        //qui rimuovo anche il mock sul metodo removeCookie() di ZKRegistrationManager.
        //Introduco un mock su ZooKeeper per arrestarmi al corrente livello di scambio
        //messaggi

        ZooKeeper mockedZook = mock(ZooKeeper.class);
        String bookieId = "/ledgers/cookies/"+getBuilderUtil.getBookieId();

        doNothing().when(mockedZook).delete(bookieId,this.testVersion);

        zkRegistrationManager = new ZKRegistrationManager(testServConf,mockedZook);

        testCookie.deleteFromRegistrationManager(zkRegistrationManager, testServConf, testLongVersion);

        Mockito.verify(mockedZook).delete(bookieId,this.testVersion);
    }

}
