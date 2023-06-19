import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.System.out;
import static org.apache.bookkeeper.bookie.Cookie.newBuilder;


@RunWith(value= Parameterized.class)
public class TestBookieHostIp {

    private Builder myBuilder;
    private String bookieId;
    private boolean expected;

    @Parameters
    public static Collection<String[]> getParameters(){
        return Arrays.asList(new String[][]{
                {"true","160.160.160.160:800"},
                {"false","test:test"},
                {"false",""},
                {"false",null}
                });
    }

    public TestBookieHostIp(String expected, String bookieId){
        this.expected = Boolean.parseBoolean(expected);
        this.bookieId = bookieId;
    }

    @Before
    public void setUp(){
        this.myBuilder = newBuilder();
        myBuilder.setBookieId(bookieId);
    }

    @Test
    public void isBookieHostCreatedFromIpTest() throws IOException {

        Cookie testCookie = myBuilder.build();
        if (bookieId == null){
            try{
                testCookie.isBookieHostCreatedFromIp();
            }
            catch(NullPointerException e){
                Assert.assertTrue(e.getMessage().contains("null"));
            }
        }
        else {
            Assert.assertEquals(expected, testCookie.isBookieHostCreatedFromIp());
        }
    }
}
