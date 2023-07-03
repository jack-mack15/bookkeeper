package org.apache.bookkeeper.cookie;

import org.junit.Assert;
import org.junit.Test;
import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.bookkeeper.bookie.Cookie.newBuilder;

@RunWith(value= Parameterized.class)
public class TestCookieToString {

    private Builder myBuilder = newBuilder();
    private static String ledger = "ledger";
    private static String bookieId = "bookieId";
    private static String index = "index";
    private static String instanceId = "instanceId";
    private static String journal = "journal";
    private String expected;

    @Parameters
    public static Collection<String[]> getParameters(){
        return Arrays.asList(new String[][]{
                //con approccio black box tutti i miei oracoli erano scorretti, quindi ho guardato
                //l'implementazione per generare gli oracoli.
                //Un esempio di case che mi aspettavo fosse:
                //{"3\nbookieId\njournal\nledger\ninstanceId\nindex",index,ledger,bookieId,instanceId,journal,"3"}
                //ma per come è stato implementata il metodo, questo test fallisce

                {"5\nbookieId\njournal\nledger\n",index,ledger,bookieId,instanceId,journal,"3"},
                {"null",index,null,null,null,"","5"},
                {"null",null,ledger,null,journal,"","6"},
                {"5\nnull\nnull\nledger\n",null,ledger,null,"",null,"0"},
                {"5\nnull\nnull\n\n","","",null,instanceId,null,"0"},
                {"5\nnull\njournal\nnull\n",null,null,"","",journal,"6"},
        });
    }

    public TestCookieToString(String expected, String index, String ledger, String bookieId, String instanceId, String journal, String layout){
        this.expected = expected;
        myBuilder.setLedgerDirs(ledger);
        myBuilder.setIndexDirs(index);
        myBuilder.setJournalDirs(journal);
        myBuilder.setBookieId(bookieId);
        myBuilder.setInstanceId(instanceId);
        myBuilder.setLayoutVersion(Integer.parseInt(layout));
    }

    @Test
    public void toStringTest(){
        Cookie testCookie = myBuilder.build();
        String result = null;
        try {
            result = testCookie.toString();
            Assert.assertEquals(expected,result);
        }
        catch(NullPointerException e){
            Assert.assertNull(result);
        }
    }
}
