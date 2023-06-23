package zkregistrationmanager;

import org.apache.bookkeeper.bookie.BookieException;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.BookieServiceInfo;
import org.apache.bookkeeper.discover.BookieServiceInfoUtils;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.bookkeeper.net.BookieId;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(value= Parameterized.class)
public class TestRegisterBookie {

    private ServerConfiguration servConf;
    private ZooKeeper zooKeeper;
    private ZKRegistrationManager zkRegistrationManager;
    private BookieId bookieId;
    private boolean readOnly;
    private BookieServiceInfo bookieServiceInfo;
    private String expected;

    @Before
    public void setUp() throws IOException {
        servConf = new ServerConfiguration();
        zooKeeper = new ZooKeeper("connectString", 15000, WatcherUtil.getWatcher(), true);
        zkRegistrationManager = new ZKRegistrationManager(servConf,zooKeeper);
    }

    @Parameters
    public static Collection<Object> getParams() throws UnknownHostException {

        return Arrays.asList(new Object[][]{
                {"test1",BookieId.parse("160.160.160.160:800"),false, new BookieServiceInfo()},
                {"test2",BookieId.parse("160.160.160.160:800"),true,
                        BookieServiceInfoUtils.buildLegacyBookieServiceInfo("160.160.160.160:800")},
                {"test3",null,true,null}
        });

    }

    public TestRegisterBookie(String expected, BookieId bookieId, boolean readOnly, BookieServiceInfo bookieServiceInfo){
        this.expected = expected;
        this.bookieId = bookieId;
        this.readOnly = readOnly;
        this.bookieServiceInfo = bookieServiceInfo;
    }


    @Test
    public void allMockedTest() throws BookieException, InterruptedException, KeeperException, IOException {
        //testo il metodo registerBookie mockando le istanze di altre classi che ricevono un messaggio

        ZooKeeper mockedZook = mock(ZooKeeper.class);
        if(!readOnly)
            when(mockedZook.create(anyString(),any(), any(), any())).thenReturn(null);
        else
            when(mockedZook.exists(anyString(), anyBoolean())).thenReturn(null);

        MockedZKRegistrationManager mockZK = new MockedZKRegistrationManager(servConf,mockedZook);
        MockedZKRegistrationManager spyZK = spy(mockZK);

        doReturn(false).when(spyZK).checkRegNodeAndWaitExpired(anyString());
        try{
            spyZK.registerBookie(bookieId,readOnly,bookieServiceInfo);

            verify(spyZK).checkRegNodeAndWaitExpired(anyString());
            if(!readOnly)
                Mockito.verify(mockedZook).create(anyString(),any(), any(), any());
            else
                Mockito.verify(mockedZook).exists(anyString(), anyBoolean());

        }
        catch(NullPointerException e){

            if(!readOnly)
                Mockito.verify(mockedZook).create(anyString(),any(), any(), any());
            else
                Mockito.verify(mockedZook).exists(anyString(), anyBoolean());

        }

    }

    //@Test
    //public void test() throws BookieException {
    //    zkRegistrationManager.registerBookie(bookieId,readOnly,bookieServiceInfo);
    //}

    /*
    @Test
    public void partialMockedTest() throws BookieException, InterruptedException, KeeperException, IOException {
        //testo il metodo registerBookie mockando le istanze di altre classi che ricevono un messaggio

        ZooKeeper mockedZook = mock(ZooKeeper.class);
        if(readOnly)
            when(mockedZook.exists(anyString(), any())).thenReturn(null);
        when(mockedZook.create(anyString(),any(), any(), any())).thenReturn(null);
        MockedZKRegistrationManager mockZK = new MockedZKRegistrationManager(servConf,mockedZook);
        MockedZKRegistrationManager spyZK = spy(mockZK);

        doReturn(false).when(spyZK).checkRegNodeAndWaitExpired(anyString());
        try{
            spyZK.registerBookie(bookieId,readOnly,bookieServiceInfo);

            verify(spyZK).checkRegNodeAndWaitExpired(anyString());
            Mockito.verify(mockedZook).create(anyString(),any(), any(), any());
            if(readOnly)
                Mockito.verify(mockedZook).exists(anyString(), any());

        }

        catch(BookieException.MetadataStoreException e){
            Assert.assertEquals("test2",expected);        }

        catch(NullPointerException e){
            Assert.assertEquals("test3",expected);
        }
    }


/*
    @Test
    public void pureTest() throws BookieException, InterruptedException, KeeperException {

        ZooKeeper mockedZook = mock(ZooKeeper.class);

        if(!readOnly)
            when(mockedZook.create(anyString(),any(), any(), any())).thenReturn(null);
        else
            when(mockedZook.exists(anyString(), anyBoolean())).thenReturn(null);

        zkRegistrationManager.registerBookie(bookieId,readOnly,bookieServiceInfo);

        if(!readOnly)
            Mockito.verify(mockedZook).create(anyString(),any(), any(), any());
        else
            Mockito.verify(mockedZook).exists(anyString(), anyBoolean());
    }
*/

}
