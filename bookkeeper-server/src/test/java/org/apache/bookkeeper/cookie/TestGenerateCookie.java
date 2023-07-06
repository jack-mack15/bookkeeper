package org.apache.bookkeeper.cookie;

import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.System.out;
import static org.apache.bookkeeper.bookie.Cookie.*;

@RunWith(value= Parameterized.class)
public class TestGenerateCookie {

    private ServerConfiguration conf;
    private boolean expected;

    @Parameters
    public static Collection<Object[]> getParameters() {
        ServerConfiguration validServ = new ServerConfiguration();
        ServerConfiguration validClient = new ServerConfiguration(new ClientConfiguration());

        validServ.setBookieId("160.160.160.160:800");
        validClient.setBookieId("160.160.160.160:800");

        ServerConfiguration invalidServ = new ServerConfiguration();

        //aggiunto dopo i report
        ServerConfiguration lateCase = new ServerConfiguration(new ClientConfiguration());
        String[] dirs = new String[2];
        dirs[0] = "index1";
        dirs[1] = "index2";
        lateCase.setBookieId("160.160.160.160:800");
        lateCase.setIndexDirName(dirs);
        out.println(lateCase.getIndexDirs().toString());

        return Arrays.asList(new Object[][]{
                {true, validServ},
                {true, validClient},
                {false, invalidServ},
                {false, null},
                //aggiunto dopo report
                {true, lateCase}
        });
    }



    public TestGenerateCookie(boolean expected,ServerConfiguration conf){
        this.conf = conf;
        this.expected = expected;
    }

    @Test
    public void generateCookieTest() {

        Builder testBuilder = null;

        try {
            testBuilder = generateCookie(conf);
            Assert.assertNotNull(testBuilder);
            Assert.assertTrue(expected);
        }
        catch (NullPointerException e){
            Assert.assertNull(testBuilder);
            Assert.assertFalse(expected);
        }
        catch (Exception e){
            Assert.assertTrue(e instanceof UnknownHostException);
            Assert.assertFalse(expected);
        }
    }

}
