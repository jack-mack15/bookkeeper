package cookie;

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
    private static Builder myBuider;
    private static final String invalidFile = "/bookkeeper-server/src/test/testFile";
    private static final String emptyFile = "";
    private String expected;
    private  String testPath;
    @BeforeClass
    public static void setUp() throws IOException {

        GetBuilderUtil getBuilderUtil = new GetBuilderUtil();
        myBuider = getBuilderUtil.getBuilder();

        myTestCookie = myBuider.build();
        Path path = Paths.get("");
        String currDirr = path.toAbsolutePath().toString();
        myTestCookie.writeToDirectory(new File(currDirr));

    }
    @Parameters
    public static Collection<String[]> getParameters(){
        Path path = Paths.get("");
        String currentDir = path.toAbsolutePath().toString();
        return Arrays.asList(new String[][]{
                {"No such file", invalidFile},
                {"No such file", emptyFile},
                {"valid",currentDir}
        });
    }

    public TestReadFromDirectory(String expected, String testPath){
        this.expected = expected;
        this.testPath = testPath;
    }
    @Test
    public void readFromDirectoryTest() throws IOException {

        out.println(testPath);
        File currentPath = new File(testPath);

        try {

            Cookie readCookie = readFromDirectory(currentPath);
            out.println(myTestCookie.toString());
            out.println(readCookie.toString());
            Assert.assertTrue(myTestCookie.equals(readCookie));

        }
        catch (IOException e){
            String messageInvalidFile = e.getMessage();
            Assert.assertTrue(messageInvalidFile.contains(expected));
        }
    }

    @AfterClass
    public static void cleanUp(){
        Path path = Paths.get("");
        String currDir = path.toAbsolutePath().toString();
        File version = new File(currDir +"/VERSION");
        version.delete();
    }
}
