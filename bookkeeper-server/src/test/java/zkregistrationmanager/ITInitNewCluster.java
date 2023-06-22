package zkregistrationmanager;

import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.bookkeeper.meta.AbstractZkLedgerManagerFactory;
import org.apache.bookkeeper.meta.zk.ZKMetadataDriverBase;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.ZooKeeper;
import org.junit.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ITInitNewCluster {

    private SetUpForZKTesting setUpper;
    private ZooKeeper zkc;
    private ZKRegistrationManager zkRegistrationManager;
    private ServerConfiguration conf;


    @Before
    public void setUp() throws Exception {
        setUpper = new SetUpForZKTesting();
        zkc = setUpper.getZooKeeper();
        conf = new ServerConfiguration();

    }

    @Test
    public void allMockedTest() throws Exception {

        //mocked ZKMetadataDriverBase
        MockedStatic<ZKMetadataDriverBase> mockedMetadata = Mockito.mockStatic(ZKMetadataDriverBase.class);
        mockedMetadata.when(() -> ZKMetadataDriverBase.resolveZkServers(conf))
                .thenReturn("test");

        //mock Zookeper
        ZooKeeper mockZook = mock(ZooKeeper.class);
        Mockito.doReturn(null).when(mockZook).exists(anyString(),any());
        Mockito.doReturn(null).when(mockZook).multi(anyList());

        //mocked Op.create
        MockedStatic<Op> mockedOp = Mockito.mockStatic(Op.class);
        mockedOp.when(() -> Op.create(anyString(),any(),any(),any()))
                .thenReturn(null);

        //mocked AbstractZkLedgerManagerFactory
        MockedStatic<AbstractZkLedgerManagerFactory> mockedLedMan = Mockito.mockStatic(AbstractZkLedgerManagerFactory.class);
        mockedLedMan.when(() -> AbstractZkLedgerManagerFactory.newLedgerManagerFactory(any(),any()))
                .thenReturn(null);

        zkRegistrationManager = new ZKRegistrationManager(conf, mockZook);

        boolean result = zkRegistrationManager.initNewCluster();

        Assert.assertTrue(result);

        mockedMetadata.verify(
                () -> ZKMetadataDriverBase.resolveZkServers(conf),
                times(1)
        );

        verify(mockZook,times(1)).exists(null,false);
        verify(mockZook,times(1)).multi(anyList());

        mockedOp.verify(
                () -> Op.create(anyString(),any(),any(),any()),
                times(3)
        );

        mockedLedMan.verify(
                () -> AbstractZkLedgerManagerFactory.newLedgerManagerFactory(any(),any()),
                times(1)
        );

        mockedOp.close();
        mockedLedMan.close();
        mockedMetadata.close();
    }

    @Test
    public void partialMockedTest() throws Exception {
        //mock Zookeper
        ZooKeeper mockedZook = mock(ZooKeeper.class);
        Mockito.doReturn(null).when(mockedZook).multi(anyList());

        //mocked Op.create
        MockedStatic<Op> mockedOp = Mockito.mockStatic(Op.class);
        mockedOp.when(() -> Op.create(anyString(),any(),any(),any()))
                .thenReturn(null);

        //mocked AbstractZkLedgerManagerFactory
        MockedStatic<AbstractZkLedgerManagerFactory> mockedLedMan = Mockito.mockStatic(AbstractZkLedgerManagerFactory.class);
        mockedLedMan.when(() -> AbstractZkLedgerManagerFactory.newLedgerManagerFactory(any(),any()))
                .thenReturn(null);

        zkRegistrationManager = new ZKRegistrationManager(conf,mockedZook);

        boolean result = zkRegistrationManager.initNewCluster();

        Assert.assertTrue(result);

        verify(mockedZook,times(1)).multi(anyList());

        mockedOp.verify(
                () -> Op.create(anyString(),any(),any(),any()),
                times(4)
        );

        mockedLedMan.verify(
                () -> AbstractZkLedgerManagerFactory.newLedgerManagerFactory(any(),any()),
                times(1)
        );

        mockedOp.close();
        mockedLedMan.close();
    }

    @Test
    public void pureTest() throws Exception {
        zkRegistrationManager = new ZKRegistrationManager(conf,zkc);
        boolean result = zkRegistrationManager.initNewCluster();
        Assert.assertTrue(result);
    }

    @After
    public void tearDown() throws Exception {
        setUpper.closeAll();
    }

}
