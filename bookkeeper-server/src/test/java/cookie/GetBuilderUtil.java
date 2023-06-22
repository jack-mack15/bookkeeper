package cookie;

import static org.apache.bookkeeper.bookie.Cookie.newBuilder;

import org.apache.bookkeeper.bookie.Cookie.Builder;

public class GetBuilderUtil {
    private final String bookieId = "160.160.160.160:8000";
    private final String journalDirs = "journal";
    private final String ledgerDirs = "ledger";
    private final String instanceId = "instance";
    private final String indexDirs = "index";
    public Builder getBuilder(){
        int layoutVersion = 5;

        Builder myBuider = newBuilder();

        myBuider.setIndexDirs(indexDirs);
        myBuider.setBookieId(bookieId);
        myBuider.setInstanceId(instanceId);
        myBuider.setJournalDirs(journalDirs);
        myBuider.setLedgerDirs(ledgerDirs);
        myBuider.setLayoutVersion(layoutVersion);

        return myBuider;
    }

    public String getBookieId(){
        return this.bookieId;
    }

}
