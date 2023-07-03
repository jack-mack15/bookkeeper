package org.apache.bookkeeper.cookie;

import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.System.out;
import static org.apache.bookkeeper.bookie.Cookie.newBuilder;
import static org.apache.bookkeeper.bookie.Cookie.readFromDirectory;

@RunWith(value= Parameterized.class)
public class TestReadFromDirectory {
    private static Cookie myTestCookie;
    private static Builder myBuilder;
    private boolean expected;
    private File testPath;
    @BeforeClass
    public static void setUp() throws IOException {

        GetBuilderUtil getBuilderUtil = new GetBuilderUtil();
        myBuilder = getBuilderUtil.getBuilder();

        myTestCookie = myBuilder.build();
        Path path = Paths.get("");
        String currDirr = path.toAbsolutePath()+"/fortest/";
        myTestCookie.writeToDirectory(new File(currDirr));

    }
    @Parameters
    public static Collection<Object[]> getParameters(){
        Path path = Paths.get("");
        String validDir = path.toAbsolutePath()+"/fortest/";
        out.println(validDir);
        String invalidDir = "/bookkeeper-server/src/test/testFile";
        String emptyDir = "";
        return Arrays.asList(new Object[][]{
                {true, new File(validDir)},
                {false, new File(invalidDir)},
                {false, new File(emptyDir)},
                {false,null}
        });
    }

    public TestReadFromDirectory(boolean expected, File testPath){
        this.expected = expected;
        this.testPath = testPath;
    }

    @Test
    public void readFromDirectoryTest() {

        try {
            Cookie readCookie = readFromDirectory(testPath);

            out.println(myTestCookie.toString());
            out.println(readCookie.toString());

            Assert.assertTrue(myTestCookie.equals(readCookie));
            Assert.assertTrue(expected);
        }
        catch (IOException e){
            String messageInvalidFile = e.getMessage();
            String expectedMess = "No such file";
            Assert.assertTrue(messageInvalidFile.contains(expectedMess));
            Assert.assertFalse(expected);
        }
        catch (Exception e){
            Assert.assertTrue(e instanceof NullPointerException);
            Assert.assertFalse(expected);
        }


    }

    //cleanUp() crea conflitti con pit
    //@AfterClass
    //public static void cleanUp(){
    //    Path path = Paths.get("");
    //    String currDir = path.toAbsolutePath().toString();
    //    File version = new File(currDir +"/VERSION");
    //    version.delete();
    //}
}
