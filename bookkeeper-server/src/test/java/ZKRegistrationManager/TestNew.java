package ZKRegistrationManager;


import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.bookkeeper.util.IOUtils;
import org.apache.bookkeeper.zookeeper.ZooKeeperClient;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertTrue;

public class TestNew {

    private ZooKeeperServer zks;
    private String connectString;
    private ZooKeeper zkc;
    private NIOServerCnxnFactory serverFactory;
    private Integer zooKeeperPort = 0;
    @Test
    public void setUp() throws Exception {
        String loopbackIPAddr = InetAddress.getLoopbackAddress().getHostAddress();
        InetSocketAddress zkaddr = new InetSocketAddress(loopbackIPAddr, zooKeeperPort);
        connectString = loopbackIPAddr + ":0";
        File zkTmpDir = IOUtils.createTempDir("zookeeper", "test");
        zks = new ZooKeeperServer(zkTmpDir, zkTmpDir,
                ZooKeeperServer.DEFAULT_TICK_TIME);
        serverFactory = new NIOServerCnxnFactory();
        serverFactory.configure(zkaddr, 100);
        serverFactory.startup(zks);

        if (0 == zooKeeperPort) {
            zooKeeperPort = serverFactory.getLocalPort();
            zkaddr = new InetSocketAddress(zkaddr.getHostName(), zooKeeperPort);
            connectString = zkaddr.getHostName() + ":" + zooKeeperPort;
        }
        zkc = ZooKeeperClient.newBuilder().connectString(connectString).sessionTimeoutMs(10000).build();
        ServerConfiguration conf = new ServerConfiguration();
        ZKRegistrationManager zkRegistrationManager = new ZKRegistrationManager(conf, zkc);
        zkRegistrationManager.prepareFormat();
    }

    public void stopCluster() throws Exception {
        if (zkc != null) {
            zkc.close();
        }

        // shutdown ZK server
        if (serverFactory != null) {
            serverFactory.shutdown();
            assertTrue("waiting for server down",
                    ClientBase.waitForServerDown(this.connectString,
                            ClientBase.CONNECTION_TIMEOUT));
        }
        if (zks != null) {
            zks.getTxnLogFactory().close();
        }
    }


}
