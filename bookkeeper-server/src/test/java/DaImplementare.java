import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.bookkeeper.bookie.Cookie.newBuilder;

//@RunWith(value= Parameterized.class)
public class DaImplementare {
    private Cookie inputCookie;
    private Builder builder;
    private String[] expected;

 //   @Parameters
    public static Collection<String[]> getParameters(){
        return Arrays.asList(new String[][]{
                {"3\tpath1\tpath2\tpath3", "path1","path2","path3"}, // expected, input1, input2, input3
                {"3\tpath1\tpath2\t", "path1","path2",""}, // expected, input1, input2, input3
                {"3\t\t\t", "","",""}, // expected, input1, input2, input3
        });
    }

    public DaImplementare(String[] expected, String ledgers, String indexes){
        this.builder = newBuilder();
        builder.setLedgerDirs(ledgers);
        builder.setIndexDirs(indexes);
        this.inputCookie = builder.build();
        this.expected = expected;
    }


    public void getLedgerDirPathsFromCookieTest(){

        //Assert.assertEquals(this.expected, result);
    }

    public void getIndexDirPathsFromCookieTest(){

    }
}
