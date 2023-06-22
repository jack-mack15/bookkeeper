package zkregistrationmanager;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class WatcherUtil {
    public static Watcher getWatcher(){
        return new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //do Something
            }
        };
    }
}
