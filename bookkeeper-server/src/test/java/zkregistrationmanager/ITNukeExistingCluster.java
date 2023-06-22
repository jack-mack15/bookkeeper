package zkregistrationmanager;

import org.apache.bookkeeper.bookie.storage.ldb.DbLedgerStorage;
import org.apache.bookkeeper.common.allocator.PoolingPolicy;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.BookieServiceInfo;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.util.PortManager;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class ITNukeExistingCluster {
    private SetUpForZKTesting setUpper;
    private ZooKeeper zkc;
    private ZKRegistrationManager zkRegistrationManager;
    private ServerConfiguration conf;

    @Test
    public void test() throws Exception {
        //prendo un cookie stringa da un test precedente e lo uso come valid byte[]
        String cookie = "5\nbookieHost:\"160.160.160.160:8000\"\njournalDir:\"journal\"\nledgerDirs:\"ledger\"\n"+
        "instanceId:\"instance\"\nindexDirs:\"index\"";
        BookieId bookieId = BookieId.parse("160.160.160.160:8000");
        setUpper = new SetUpForZKTesting();
        zkc = setUpper.getZooKeeper();
        conf = newServerConfiguration();


        zkRegistrationManager = new ZKRegistrationManager(conf,zkc);
        zkRegistrationManager.registerBookie(bookieId,true,new BookieServiceInfo());
    }

    public ServerConfiguration newServerConfiguration() {
        ServerConfiguration confReturn = new ServerConfiguration();
        confReturn.setTLSEnabledProtocols("TLSv1.2,TLSv1.1");
        confReturn.setJournalFlushWhenQueueEmpty(true);
        // enable journal format version
        confReturn.setJournalFormatVersionToWrite(5);
        confReturn.setAllowEphemeralPorts(false);
        confReturn.setBookiePort(PortManager.nextFreePort());
        confReturn.setGcWaitTime(1000);
        confReturn.setDiskUsageThreshold(0.999f);
        confReturn.setDiskUsageWarnThreshold(0.99f);
        confReturn.setAllocatorPoolingPolicy(PoolingPolicy.UnpooledHeap);
        confReturn.setProperty(DbLedgerStorage.WRITE_CACHE_MAX_SIZE_MB, 4);
        confReturn.setProperty(DbLedgerStorage.READ_AHEAD_CACHE_MAX_SIZE_MB, 4);
        /**
         * if testcase has zk error,just try 0 time for fast running
         */
        confReturn.setZkRetryBackoffMaxRetries(0);
        setLoopbackInterfaceAndAllowLoopback(confReturn);
        return confReturn;
    }

    private String getLoopbackInterfaceName() {
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface nif : Collections.list(nifs)) {
                if (nif.isLoopback()) {
                    return nif.getName();
                }
            }
        } catch (SocketException se) {
            return null;
        }
        return null;
    }

    public ServerConfiguration setLoopbackInterfaceAndAllowLoopback(ServerConfiguration serverConf) {
        serverConf.setListeningInterface(getLoopbackInterfaceName());
        serverConf.setAllowLoopback(true);
        return serverConf;
    }
}
