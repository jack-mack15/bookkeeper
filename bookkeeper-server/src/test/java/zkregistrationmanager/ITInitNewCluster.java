package zkregistrationmanager;

import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ITInitNewCluster extends AbstractClusterTest{

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
        setMockedMetadata(conf);
        //mock Zookeper
        setMockZook(true,true);
        //mocked Op.create
        setMockedOp();
        //mocked AbstractZkLedgerManagerFactory
        setMockedLedMan();

        zkRegistrationManager = new ZKRegistrationManager(conf, mockZook);

        boolean result = zkRegistrationManager.initNewCluster();

        Assert.assertTrue(result);
        verifyMockedMetadata(conf);
        verifyMockedZook(true,true,1);
        verifyMockedOp(3);
        verifyMockedLedMan();

    }

    @Test
    public void partialMockedTest() throws Exception {
        //mock Zookeper
        setMockZook(true,false);
        //mocked Op.create
        setMockedOp();
        //mocked AbstractZkLedgerManagerFactory
        setMockedLedMan();

        zkRegistrationManager = new ZKRegistrationManager(conf,mockZook);

        boolean result = zkRegistrationManager.initNewCluster();

        Assert.assertTrue(result);
        verifyMockedZook(true,false,1);
        verifyMockedOp(4);
        verifyMockedLedMan();

    }

    @Test
    public void pureTest() throws Exception {
        zkRegistrationManager = new ZKRegistrationManager(conf,zkc);
        boolean result = zkRegistrationManager.initNewCluster();
        Assert.assertTrue(result);
    }

    @After
    public void closeAllMocks() throws InterruptedException {
        closeMocks();
    }
}
