import static org.apache.bookkeeper.bookie.Cookie.newBuilder;
import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;

public class GetBuilderUtil {
    public Builder getBuilder(){
        int layoutVersion = 5;
        String bookieId = "160.160.160.160:8000";
        String journalDirs = "journal";
        String ledgerDirs = "ledger";
        String instanceId = "instance";
        String indexDirs = "index";

        Builder myBuider = newBuilder();

        myBuider.setIndexDirs(indexDirs);
        myBuider.setBookieId(bookieId);
        myBuider.setInstanceId(instanceId);
        myBuider.setJournalDirs(journalDirs);
        myBuider.setLedgerDirs(ledgerDirs);
        myBuider.setLayoutVersion(layoutVersion);

        return myBuider;
    }
}
