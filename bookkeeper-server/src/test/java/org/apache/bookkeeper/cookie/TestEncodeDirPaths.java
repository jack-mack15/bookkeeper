package org.apache.bookkeeper.cookie;

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
    public static Collection<Object[]> getParameters(){
        String[] valid = {"path1","path2","path3","path4"};
        String[] allNull = {null,null,null,null};
        String[] allEmpty = {"","","",""};
        String[] semi = {"path1","path2","",null};

        return Arrays.asList(new Object[][]{
                {"4\tpath1\tpath2\tpath3\tpath4", valid},   // expected, input
                {"4\tnull\tnull\tnull\tnull", allNull},     // expected, input
                {"4\t\t\t\t", allEmpty},                    // expected, input
                {"4\tpath1\tpath2\t\tnull", semi},          // expected, input
                {null,null}
        });
    }

    public TestEncodeDirPaths(String expected, String[] paths){
        this.expected = expected;
        this.input = paths;
    }
    @Test
    public void encodeTest(){
        String result = null;
        try {
            result = encodeDirPaths(this.input);
            Assert.assertEquals(this.expected, result);
        }
        catch(Exception e){
            Assert.assertTrue(e instanceof NullPointerException);
            Assert.assertEquals(expected,result);
        }
    }
}
