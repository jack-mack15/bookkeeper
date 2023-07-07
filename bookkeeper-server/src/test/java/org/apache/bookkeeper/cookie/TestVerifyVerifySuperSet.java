package org.apache.bookkeeper.cookie;

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
public class TestVerifyVerifySuperSet {

    private String expected;
    private Cookie firstCookie;
    private Cookie testCookie;
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
    public static Collection<Object[]> getParameters(){

        Builder builder = newBuilder();

        builder.setLedgerDirs(ledger);
        builder.setIndexDirs(index);
        builder.setJournalDirs(journal);
        builder.setBookieId(bookieId);
        builder.setInstanceId(instanceId);
        builder.setLayoutVersion(6);
        Cookie valid = builder.build();

        builder.setLedgerDirs(null);
        builder.setIndexDirs(index);
        builder.setJournalDirs("");
        builder.setBookieId(null);
        builder.setInstanceId(null);
        builder.setLayoutVersion(5);
        Cookie nullCookie1 = builder.build();

        builder.setLedgerDirs(null);
        builder.setIndexDirs(index);
        builder.setJournalDirs(null);
        builder.setBookieId(null);
        builder.setInstanceId("");
        builder.setLayoutVersion(6);
        Cookie nullCookie2 = builder.build();

        builder.setLedgerDirs(ledger);
        builder.setIndexDirs(null);
        builder.setJournalDirs(null);
        builder.setBookieId(null);
        builder.setInstanceId("");
        builder.setLayoutVersion(0);
        Cookie tooOld1 = builder.build();

        builder.setLedgerDirs("");
        builder.setIndexDirs("");
        builder.setJournalDirs(null);
        builder.setBookieId(null);
        builder.setInstanceId(instanceId);
        builder.setLayoutVersion(-1);
        Cookie tooOld2 = builder.build();

        //per aumentare il coverage
        builder.setLedgerDirs(ledger);
        builder.setIndexDirs(index);
        builder.setJournalDirs(journal);
        builder.setBookieId(bookieId+"2");
        builder.setInstanceId(instanceId);
        builder.setLayoutVersion(6);
        Cookie forBadua = builder.build();

        builder.setLedgerDirs(ledger);
        builder.setIndexDirs(index);
        builder.setJournalDirs(journal);
        builder.setBookieId(bookieId);
        builder.setInstanceId(instanceId+"2");
        builder.setLayoutVersion(6);
        Cookie notMatch1 = builder.build();

        builder.setLedgerDirs(ledger);
        builder.setIndexDirs(index);
        builder.setJournalDirs(journal+"2");
        builder.setBookieId(bookieId+"2");
        builder.setInstanceId(instanceId+"2");
        builder.setLayoutVersion(5);
        Cookie notMatch2 = builder.build();

        return Arrays.asList(new Object[][]{
                //casi di test category partitioning con comb uni dimensionale
                {"ok",      valid},
                {"null",    nullCookie1},
                {"too old", tooOld1},
                {"too old", tooOld2},
                {"null",    nullCookie2},
                {"null",    null},
                //ulteriori casi di test per aumentare la coverage
                {"not matching",notMatch1},
                {"not matching",notMatch2},
                {"not matching",forBadua}
        });
    }

    public TestVerifyVerifySuperSet(String expected, Cookie cookie){
        this.expected = expected;
        this.testCookie = cookie;
    }

    @Test
    public void verifyIsSuperSet() throws BookieException.InvalidCookieException {
        String message = null;
        switch (this.expected){
            case "ok":
                try{
                    firstCookie.verifyIsSuperSet(testCookie);
                    message = "ok";
                    Assert.assertEquals(this.expected,message);
                }
                catch(Exception e){
                    //mi aspetto di non entrare mai qui
                }
                break;
            case "null":
                try{
                    firstCookie.verifyIsSuperSet(testCookie);
                }
                catch(NullPointerException e){
                    message = e.getMessage();
                }
                Assert.assertEquals(null,message);
                break;
            case "too old":
                //test identico a "not matching"
            case "not matching":
                try{
                    firstCookie.verifyIsSuperSet(testCookie);
                }
                catch (BookieException.InvalidCookieException e){
                    message = e.getMessage();
                }
                Assert.assertTrue(message.contains(this.expected));
                break;
            default:
                //se entro in questo branch significa che ci sono degli errori
                Assert.assertNotNull(message);
        }
    }


    @Test
    public void verifyTest() throws BookieException.InvalidCookieException {
        String message = null;
        switch (this.expected){
            case "ok":
                try{
                    firstCookie.verify(testCookie);
                    message = "ok";
                    Assert.assertEquals(this.expected,message);
                }
                catch(Exception e){
                    //mmi aspetto di non entrare mai qui
                }
                break;
            case "null":
                try{
                    firstCookie.verify(testCookie);
                }
                catch(NullPointerException e){
                    message = e.getMessage();
                }
                Assert.assertEquals(null,message);
                break;
            case "too old":
                //test identico a "not matching"
            case "not matching":
                try{
                    firstCookie.verify(testCookie);
                }
                catch (BookieException.InvalidCookieException e){
                    message = e.getMessage();
                }
                Assert.assertTrue(message.contains(this.expected));
                break;
            default:
                //similmente al default del metodo di test precedente
                Assert.assertNotNull(message);
        }
    }
}
