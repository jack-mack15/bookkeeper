package zkregistrationmanager;

import org.apache.bookkeeper.bookie.storage.ldb.DbLedgerStorage;
import org.apache.bookkeeper.common.allocator.PoolingPolicy;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.BookieServiceInfo;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.bookkeeper.meta.AbstractZkLedgerManagerFactory;
import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.util.PortManager;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import static java.lang.System.out;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ITNukeExistingCluster extends AbstractClusterTest {
    private SetUpForZKTesting setUpper;
    private ZooKeeper zkc;
    private ZKRegistrationManager zkRegistrationManager;
    private ServerConfiguration conf;
    private AbstractZkLedgerManagerFactory mockFact;

    @Before
    public void setUp() throws Exception {
        setUpper = new SetUpForZKTesting();
        zkc = setUpper.getZooKeeper();
        conf = new ServerConfiguration();
    }


    @Test
    public void allMockedTest() throws Exception {

        //mocked ZooKeeper
        setMockZook(false,true);
        //mocked ZKMetadataDriverBase
        setMockedMetadata(conf);

        zkRegistrationManager = new ZKRegistrationManager(conf,mockZook);
        boolean result = zkRegistrationManager.nukeExistingCluster();

        Assert.assertTrue(result);
        verifyMockedMetadata(conf);
        verifyMockedZook(false,true,1);
    }

    @Test
    public void pureTest() throws Exception {
        zkRegistrationManager = new ZKRegistrationManager(conf,zkc);
        boolean result = zkRegistrationManager.nukeExistingCluster();

        Assert.assertTrue(result);
    }

    @After
    public void closeAllMocks() throws InterruptedException {
        closeMocks();
    }
}
