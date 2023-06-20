package Cookie;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.bookkeeper.bookie.Cookie.encodeDirPaths;

@RunWith(value= Parameterized.class)
public class TestEncodeDirPaths {

    private String expected;
    private String[] input;

    @Parameters
    public static Collection<String[]> getParameters(){
        return Arrays.asList(new String[][]{
                {"3\tpath1\tpath2\tpath3", "path1","path2","path3"}, // expected, input1, input2, input3
                {"3\tpath1\tpath2\t", "path1","path2",""},           // expected, input1, input2, input3
                {"3\tnull\t\t", null,"",""},                                   // expected, input1, input2, input3
                {"3\tpath1\t\tnull", "path1","",null},                               // expected, input1, input2, input3
                {"3\t\tnull\tpath3", "",null,"path3"}                                // expected, input1, input2, input3
        });
    }

    public TestEncodeDirPaths(String expected, String path1, String path2, String path3){
        this.expected = expected;
        this.input = new String[3];
        this.input[0] = path1;
        this.input[1] = path2;
        this.input[2] = path3;
    }
    @Test
    public void encodeTest(){
        String result = encodeDirPaths(this.input);
        Assert.assertEquals(this.expected, result);
    }
}
