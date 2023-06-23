package zkregistrationmanager;

import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.meta.AbstractZkLedgerManagerFactory;
import org.apache.bookkeeper.meta.zk.ZKMetadataDriverBase;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public abstract class AbstractClusterTest {
    protected MockedStatic<ZKMetadataDriverBase> mockedMetadata;
    protected MockedStatic<Op> mockedOp;
    protected ZooKeeper mockZook;
    protected MockedStatic<AbstractZkLedgerManagerFactory> mockedLedMan;

    protected void setMockedMetadata(ServerConfiguration conf){
        mockedMetadata = Mockito.mockStatic(ZKMetadataDriverBase.class);
        mockedMetadata.when(() -> ZKMetadataDriverBase.resolveZkServers(conf))
                .thenReturn("test");
    }
    protected void setMockedOp(){
        mockedOp = Mockito.mockStatic(Op.class);
        mockedOp.when(() -> Op.create(anyString(),any(),any(),any()))
                .thenReturn(null);
    }
    protected void setMockZook(boolean isMulti,boolean isExists) throws InterruptedException, KeeperException {
        mockZook = mock(ZooKeeper.class);
        if(isExists) {
            Mockito.doReturn(null).when(mockZook).exists(null,false);

        }
        if(isMulti)
            Mockito.doReturn(null).when(mockZook).multi(anyList());
    }
    protected void setMockedLedMan() throws InterruptedException, KeeperException, IOException {
        mockedLedMan = Mockito.mockStatic(AbstractZkLedgerManagerFactory.class);
        mockedLedMan.when(() -> AbstractZkLedgerManagerFactory.newLedgerManagerFactory(any(),any()))
                .thenReturn(null);
    }
    protected void verifyMockedZook(boolean isMulti,boolean isExists,int times) throws InterruptedException, KeeperException {
        if(isExists)
            verify(mockZook,times(times)).exists(null,false);
        if(isMulti)
            verify(mockZook,times(times)).multi(anyList());
    }
    protected void verifyMockedOp(int times){
        mockedOp.verify(
                () -> Op.create(anyString(),any(),any(),any()),
                times(times)
        );
    }
    protected void verifyMockedLedMan() throws IOException, InterruptedException, KeeperException {
        mockedLedMan.verify(
                () -> AbstractZkLedgerManagerFactory.newLedgerManagerFactory(any(), any()),
                times(1)
        );
    }
    protected void verifyMockedMetadata(ServerConfiguration conf){
        mockedMetadata.verify(
                () -> ZKMetadataDriverBase.resolveZkServers(conf),
                times(1)
        );
    }
    protected void closeMocks() throws InterruptedException {
        if(mockedMetadata != null)
            mockedMetadata.close();
        if(mockedOp != null)
            mockedOp.close();
        if(mockedLedMan != null)
            mockedLedMan.close();
    }
}
