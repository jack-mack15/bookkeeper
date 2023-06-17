import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static java.lang.System.out;
import static org.apache.bookkeeper.bookie.Cookie.readFromDirectory;
import static org.apache.bookkeeper.bookie.Cookie.newBuilder;

public class TestCookie {

    private Cookie myTestCookie;
    private Builder myBuider;
    File invalidFile;
    File validFile;
    File emptyStringFile;
    File validPath;
    @Before
    public void setUp(){
        int layoutVersion = 5;
        String bookieId = "test";
        String journalDirs = "journal";
        String ledgerDirs = "ledger";
        String instanceId = "instance";
        String indexDirs = "index";

        myBuider = newBuilder();

        myBuider.setIndexDirs(indexDirs);
        myBuider.setBookieId(bookieId);
        myBuider.setInstanceId(instanceId);
        myBuider.setJournalDirs(journalDirs);
        myBuider.setLedgerDirs(ledgerDirs);
        myBuider.setLayoutVersion(layoutVersion);

        myTestCookie = myBuider.build();

        String invalidPath = "/bookkeeper-server/src/test/testFile";
        String validPath = "testFile.txt";
        String empty = "";
        invalidFile = new File(invalidPath);
        validFile = new File(validPath);
        emptyStringFile = new File(empty);

        this.validPath = new File("/home/gianl/Desktop/bookkeeper/bookkeeper-server/");
    }

    @Test
    public void writeToFileTest() throws IOException {

        try {
            myTestCookie.writeToFile(invalidFile);
        }
        catch(FileNotFoundException e){
            String message = e.getMessage();
            String expected = "No such file";
            Assert.assertTrue(message.contains(expected));
        }

        try {
            myTestCookie.writeToFile(emptyStringFile);
        }
        catch(FileNotFoundException e){
            String message = e.getMessage();
            String expected = "No such file";
            Assert.assertTrue(message.contains(expected));
        }

        myTestCookie.writeToFile(validFile);

        BufferedReader br = new BufferedReader(new FileReader(validFile));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }

        String fileContent = stringBuilder.toString();
        Assert.assertEquals(fileContent,myTestCookie.toString());
    }

    @Test
    public void isBookieHostCreatedFromIpTest() throws IOException {
    }

    @Test
    public void readFromDirectoryTest() throws IOException {

        myTestCookie.writeToDirectory(validPath);

        Cookie cookieInvalidFile;
        Cookie cookieValidFile;
        Cookie cookieEmptyPathFile;

        String messageInvalidFile = null;
        try {
            cookieInvalidFile = readFromDirectory(invalidFile);
        }
        catch (IOException e){
            messageInvalidFile = e.getMessage();
        }

        Assert.assertTrue(messageInvalidFile.contains("No such file"));

        String messageEmptyPathFile = null;
        try {
            cookieEmptyPathFile = readFromDirectory(emptyStringFile);
        }
        catch (IOException e){
            messageEmptyPathFile = e.getMessage();
        }

        Assert.assertTrue(messageEmptyPathFile.contains("No such file"));

        cookieValidFile = readFromDirectory(validPath);

        Assert.assertTrue(myTestCookie.equals(cookieValidFile));
    }
}
