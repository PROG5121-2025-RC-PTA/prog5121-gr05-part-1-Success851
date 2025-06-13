package success;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

// This class tests the core logic in our manager classes.
public class SuccessMalefoPOETest {

    private AccountManager accountManager;
    private MessageManager messageManager;

    // This runs before each test to set up a clean environment.
    @BeforeEach
    public void setUp() {
        // Create new managers for each test.
        accountManager = new AccountManager();
        messageManager = new MessageManager();

        // Register the two test users as specified in the POE.
        // The student's name is used for the first user.
        accountManager.registerUser(new User("kyl_1", "Ch&&sec@ke99!", "Success", "Malefo"));
        accountManager.registerUser(new User("ken_1", "Ch&&sec@ke88!", "Ken", "Block"));

        // Send some messages to test with.
        messageManager.sendMessage(new Message("kyl_1", "ken_1", "Hello Ken, how are you?"));
        messageManager.sendMessage(new Message("ken_1", "kyl_1", "I am good thanks, and you?"));
        messageManager.sendMessage(new Message("kyl_1", "ken_1", "I am great, thanks for asking."));
    }

    // This runs after each test to clean up the JSON files.
    @AfterEach
    public void tearDown() {
        new File("users.json").delete();
        new File("sentMessages.json").delete();
        new File("draftMessages.json").delete();
    }
    
    // --- Part 1 Tests: User Account Logic ---

    @Test
    public void testCheckUserNameCorrectlyFormatted() {
        assertTrue(accountManager.checkUserName("u_ser"));
    }

    @Test
    public void testCheckUserNameIncorrectlyFormatted() {
        assertFalse(accountManager.checkUserName("username")); // Missing underscore
    }
    
    @Test
    public void testCheckPasswordComplexityMet() {
        assertTrue(accountManager.checkPasswordComplexity("Password@1"));
    }

    @Test
    public void testCheckPasswordComplexityNotMet() {
        assertFalse(accountManager.checkPasswordComplexity("password")); // Missing requirements
    }
    
    @Test
    public void testSuccessfulLogin() {
        User user = accountManager.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertNotNull(user);
        assertEquals("Success", user.getFirstName());
    }

    @Test
    public void testFailedLogin() {
        assertNull(accountManager.loginUser("kyl_1", "wrongpassword"));
    }

    // --- Part 3 Tests: Reports and Management ---

    @Test
    public void testFindExistingUser() {
        assertNotNull(accountManager.findUser("ken_1"));
    }

    @Test
    public void testDeleteUser() {
        assertTrue(accountManager.deleteUser("ken_1"));
        assertNull(accountManager.findUser("ken_1")); // Verify it's gone.
    }

    @Test
    public void testSearchMessageByContent() {
        String result = messageManager.searchMessages("great");
        assertTrue(result.contains("I am great, thanks for asking."));
    }
    
    @Test
    public void testDeleteMessage() {
        // We need to get the actual random ID of a message to delete it.
        // Let's get the first message we created.
        String report = messageManager.displayAllSentMessages();
        String firstLine = report.split("\n")[2]; // Get the "ID: xxxx, ..." line
        String idStr = firstLine.substring(firstLine.indexOf(":") + 2, firstLine.indexOf(","));
        int idToDelete = Integer.parseInt(idStr);
        
        assertTrue(messageManager.deleteMessage(idToDelete));
        
        // Verify the message is gone from the report.
        String newReport = messageManager.displayAllSentMessages();
        assertFalse(newReport.contains("ID: " + idToDelete));
    }
}
