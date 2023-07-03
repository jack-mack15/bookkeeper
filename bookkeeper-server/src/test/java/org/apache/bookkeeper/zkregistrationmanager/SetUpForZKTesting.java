package org.apache.bookkeeper.zkregistrationmanager;

import org.apache.bookkeeper.util.IOUtils;
import org.apache.bookkeeper.zookeeper.ZooKeeperClient;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;


public class SetUpForZKTesting {
    private ZooKeeperServer zks;
    private String connectString;
    private ZooKeeper zkc;
    private NIOServerCnxnFactory serverFactory;
    private Integer zooKeeperPort = 0;
    public ZooKeeper getZooKeeper() throws Exception {
        String address = InetAddress.getLoopbackAddress().getHostAddress();
        connectString = address + ":0";

        File tempDir = IOUtils.createTempDir("zkTest", "test");

        InetSocketAddress zkaddr = new InetSocketAddress(address, zooKeeperPort);

        zks = new ZooKeeperServer(tempDir, tempDir, ZooKeeperServer.DEFAULT_TICK_TIME);

        serverFactory = new NIOServerCnxnFactory();
        serverFactory.configure(zkaddr, 100);
        serverFactory.startup(zks);

        zooKeeperPort = serverFactory.getLocalPort();
        zkaddr = new InetSocketAddress(zkaddr.getHostName(), zooKeeperPort);
        connectString = zkaddr.getHostName() + ":" + zooKeeperPort;

        zkc = ZooKeeperClient.newBuilder().connectString(connectString).build();
        return zkc;
    }

    public void closeAll() throws Exception {
        if (zkc != null) {
            zkc.close();
        }

        if (serverFactory != null) {
            serverFactory.shutdown();
        }
        if (zks != null) {
            zks.getTxnLogFactory().close();
        }
    }
}
