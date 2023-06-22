package cookie;

import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import static java.lang.System.out;

@RunWith(value= Parameterized.class)
public class TestWriteToFile {

    private Cookie myTestCookie;
    private Builder myBuider;
    private static final String invalidFile = "/bookkeeper-server/src/test/testFile";
    private static final String validFile = "testFile.txt";
    private static final String emptyFile = "";
    private String testFile;
    private String expected;

    @Before
    public void setUp(){

        GetBuilderUtil getBuilderUtil = new GetBuilderUtil();
        myBuider = getBuilderUtil.getBuilder();

        myTestCookie = myBuider.build();

        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();
        out.println(directoryName);
    }

    @Parameters
    public static Collection<String[]> getParameters(){
        return Arrays.asList(new String[][]{
                {"valid", validFile},
                {"no such file", invalidFile},
                {"no such file", emptyFile}
        });
    }

    public TestWriteToFile(String expected, String testFile){
        this.expected = expected;
        this.testFile = testFile;
    }

    @Test
    public void writeToFileTest() throws IOException {

        File currentFile = new File(testFile);

        switch(expected){
            case "invalid":
                try {
                    myTestCookie.writeToFile(currentFile);
                }
                catch(FileNotFoundException e){
                    String message = e.getMessage();
                    Assert.assertTrue(message.contains(expected));
                }
                break;
            case "valid":
                myTestCookie.writeToFile(currentFile);
                BufferedReader br = new BufferedReader(new FileReader(validFile));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }

                String fileContent = stringBuilder.toString();
                Assert.assertEquals(fileContent,myTestCookie.toString());
                break;
            case "null":
                try {
                    myTestCookie.writeToFile(currentFile);
                }
                catch(NullPointerException e){
                    String message = e.getMessage();
                    Assert.assertNull(message);
                }
        }
    }

    @After
    public void cleanEnvironment(){
        File currentFile = new File(testFile);
        if(currentFile.exists()) currentFile.delete();
    }
}
