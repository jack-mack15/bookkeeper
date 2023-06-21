package ZKRegistrationManager;

import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class MockedZKRegistrationManager extends ZKRegistrationManager {
    public MockedZKRegistrationManager(ServerConfiguration conf, ZooKeeper zk) {
        super(conf, zk);
    }

    @Override
    public boolean checkRegNodeAndWaitExpired(String regPath) throws IOException {

        return super.checkRegNodeAndWaitExpired(regPath);
    }
}
