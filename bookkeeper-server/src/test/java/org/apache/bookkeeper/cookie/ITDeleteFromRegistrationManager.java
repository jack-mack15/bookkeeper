package org.apache.bookkeeper.cookie;

import org.apache.bookkeeper.bookie.BookieException;
import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.RegistrationManager;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.versioning.LongVersion;
import org.apache.zookeeper.KeeperException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.apache.zookeeper.ZooKeeper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



public class ITDeleteFromRegistrationManager {

    private ServerConfiguration testServConf;
    private GetBuilderUtil getBuilderUtil;
    private Builder myBuilder;
    private Cookie testCookie;
    private int testVersion = 5;
    private LongVersion testLongVersion;
    private RegistrationManager registrationManager;

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
    public void allMockedTest() throws BookieException {
        //qui mocko tutti i messaggi dalla classe Cookie alle altre classi.
        //In particolare mocko la chiamata a metodo di BookImpl getBookieId()
        //e a metodo di ZKRegistrationManager removeCookie()

        RegistrationManager mockedRegMan = mock(RegistrationManager.class);


        BookieId testBookieId = BookieId.parse(getBuilderUtil.getBookieId());

        MockedStatic<BookieImpl> mockBook = Mockito.mockStatic(BookieImpl.class);
        mockBook.when(() -> BookieImpl.getBookieId(any()))
                .thenReturn(testBookieId);


        doNothing().when(mockedRegMan).removeCookie(testBookieId,testLongVersion);

        testCookie.deleteFromRegistrationManager(mockedRegMan, testServConf, testLongVersion);

        Mockito.verify(mockedRegMan).removeCookie(testBookieId,testLongVersion);

        mockBook.verify(
                () -> BookieImpl.getBookieId(any()),
                times(1)
        );

        mockBook.close();
    }

    @Test
    public void mockedRemoveCookieTest() throws BookieException {
        //qui "rilascio" il mock su BookImpl sul metodo getBookieId(), lascio il mock su
        //ZKRegistrationManager removeCookie()

        RegistrationManager mockedRegMan = mock(RegistrationManager.class);
        BookieId testBookieid = BookieId.parse(getBuilderUtil.getBookieId());

        doNothing().when(mockedRegMan).removeCookie(testBookieid,testLongVersion);

        testCookie.deleteFromRegistrationManager(mockedRegMan, testServConf, testLongVersion);

        Mockito.verify(mockedRegMan).removeCookie(testBookieid,testLongVersion);
    }


    @Test
    public void almostCompleteTest() throws InterruptedException, KeeperException, BookieException {
        //qui rimuovo anche il mock sul metodo removeCookie() di ZKRegistrationManager.
        //Introduco un mock su ZooKeeper per arrestarmi al corrente livello di scambio
        //messaggi

        ZooKeeper mockedZook = mock(ZooKeeper.class);
        String bookieId = "/ledgers/cookies/"+getBuilderUtil.getBookieId();

        doNothing().when(mockedZook).delete(bookieId,this.testVersion);

        registrationManager = new ZKRegistrationManager(testServConf,mockedZook);

        testCookie.deleteFromRegistrationManager(registrationManager, testServConf, testLongVersion);

        Mockito.verify(mockedZook).delete(bookieId,this.testVersion);
    }

}
