import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.bookie.Cookie;
import org.apache.bookkeeper.bookie.Cookie.Builder;
import org.apache.bookkeeper.conf.AbstractConfiguration;
import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.discover.RegistrationManager;
import org.apache.bookkeeper.discover.ZKRegistrationManager;
import org.apache.bookkeeper.meta.NullMetadataBookieDriver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.out;
import static jdk.jfr.internal.SecuritySupport.getAbsolutePath;
import static org.apache.bookkeeper.bookie.Cookie.*;

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
        String bookieId = "160.160.160.160:8000";
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

        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();
        this.validPath = new File(directoryName);
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
        String invalidBookieId = "test:test";
        String emptyBookieId = "";
        myBuider.setBookieId(invalidBookieId);
        Cookie cookieInvalidBookieId = myBuider.build();
        myBuider.setBookieId(emptyBookieId);
        Cookie cookieEmptyBookieID = myBuider.build();

        Assert.assertFalse(cookieInvalidBookieId.isBookieHostCreatedFromIp());
        Assert.assertFalse(cookieEmptyBookieID.isBookieHostCreatedFromIp());
        Assert.assertTrue(myTestCookie.isBookieHostCreatedFromIp());
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


    @Test
    public void readFromRegistrationManagerWithBookieTest(){
        //QUESTO è DA RISOLVERE IN QUALCHE MODO
        //per creeare il BookieId usa metodo static parse(stringa)
        //registration manager ha 3 implementazioni, zkregistrationmanager,
        //nullmetadatabookiedriver, e etcdregistrationmanager

        RegistrationManager nullRegistrationManager = new NullMetadataBookieDriver.NullRegistrationManager();
        //RegistrationManager zkRegistrationManager = new ZKRegistrationManager();

    }

    @Test
    public void readFromRegistrationManagerWithConfTest(){}

    @After
    public void cleanEnvironment(){
        validFile.delete();
        File version = new File(validPath+"/VERSION");
        version.delete();
    }
}
