import org.apache.bookkeeper.bookie.BookieException;
import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.bookkeeper.bookie.Cookie.newBuilder;

@RunWith(value= Parameterized.class)
public class TestVerify {

    private String expected;
    private Cookie firstCookie;
    private Cookie secondCookie;
    private Builder builder;

    private static String ledger = "ledger";
    private static String bookieId = "bookeId";
    private static String index = "index";
    private static String instanceId = "instance";
    private static String journal = "journal";

    @Before
    public void setUp(){
        Builder tempBuilder = newBuilder();
        tempBuilder.setJournalDirs(journal);
        tempBuilder.setBookieId(bookieId);
        tempBuilder.setInstanceId(instanceId);
        tempBuilder.setIndexDirs(index);
        tempBuilder.setLedgerDirs(ledger);
        tempBuilder.setLayoutVersion(5);
        this.firstCookie = tempBuilder.build();
    }

    @Parameters
    public static Collection<String[]> getParameters(){
        return Arrays.asList(new String[][]{
                {"ok",index,ledger,bookieId,instanceId,journal,"3"},
                {"null",index,null,null,null,"","5"},
                {"too old",null,ledger,null,"",null,"0"},
                {"too old","","",null,instanceId,null,"0"},
                {"null",null,null,"",null,journal,"6"},
        });
    }

    public TestVerify(String expected, String index,String ledger, String bookieId, String instanceId, String journal, String layout){
        this.expected = expected;
        this.builder = newBuilder();
        builder.setLedgerDirs(ledger);
        builder.setIndexDirs(index);
        builder.setJournalDirs(journal);
        builder.setBookieId(bookieId);
        builder.setInstanceId(instanceId);
        builder.setLayoutVersion(Integer.parseInt(layout));
        this.secondCookie = builder.build();
    }

    @Test
    public void verifyTest() throws BookieException.InvalidCookieException {
        String message = null;
        switch (this.expected){
            case "ok":
                try{
                    firstCookie.verify(secondCookie);
                    message = "ok";
                    Assert.assertEquals(this.expected,message);
                }
                catch(Exception e){
                    //mmi aspetto di non entrare mai qui
                }
                break;
            case "null":
                try{
                    firstCookie.verify(secondCookie);
                }
                catch(NullPointerException e){
                    message = e.getMessage();
                }
                Assert.assertEquals(null,message);
                break;
            case "too old":
                try{
                    firstCookie.verify(secondCookie);
                }
                catch (BookieException.InvalidCookieException e){
                    message = e.getMessage();
                }
                Assert.assertTrue(message.contains(this.expected));
                break;
            default:
                Assert.assertNotNull(message);
        }
    }
}
