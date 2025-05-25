package success;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests for the UserAccount class.
// We're checking if registration and login work as expected.
public class UserAccountTest {

    // These are our standard test inputs.
    private static final String VALID_USERNAME = "kyl_1";
    private static final String VALID_PASSWORD = "Passw0rd!"; // Make sure it meets complexity
    private static final String VALID_CELL_PHONE = "+27123456789";
    private static final String VALID_FIRST_NAME = "Success"; // Or any valid name
    private static final String VALID_LAST_NAME = "Malefo"; // Or any valid name

    private static final String INVALID_USERNAME_NO_UNDERSCORE = "kyle1";
    private static final String INVALID_USERNAME_TOO_LONG = "kyle_too_long";
    private static final String INVALID_PASSWORD_TOO_SHORT = "Pass1!";
    private static final String INVALID_PASSWORD_NO_CAPITAL = "passw0rd!";
    private static final String INVALID_PASSWORD_NO_NUMBER = "PasswOrd!";
    private static final String INVALID_PASSWORD_NO_SPECIAL = "Passw0rd";
    private static final String INVALID_CELL_PHONE_FORMAT = "0712345678";
    private static final String INVALID_FIRST_NAME_EMPTY = "";
    private static final String INVALID_LAST_NAME_WITH_NUMBER = "Litelu1";


    // Expected bits of messages for different scenarios.
    private static final String USERNAME_SUCCESS_MSG = "Username successfully captured";
    private static final String PASSWORD_SUCCESS_MSG = "Password successfully captured";
    private static final String CELLPHONE_SUCCESS_MSG = "Cellphone number successfully captured";
    private static final String FIRSTNAME_SUCCESS_MSG = "First name successfully captured";
    private static final String LASTNAME_SUCCESS_MSG = "Last name successfully captured";
    private static final String REGISTRATION_COMPLETE_MSG = "Registration successful";
    private static final String REGISTRATION_ABORTED_MSG = "Registration aborted";

    private static final String USERNAME_ERROR_MSG = "Username is not correctly formatted";
    private static final String PASSWORD_ERROR_MSG = "Password is not correctly formatted";
    private static final String CELLPHONE_ERROR_MSG = "Cellphone number is incorrectly formatted";
    private static final String FIRSTNAME_ERROR_EMPTY_MSG = "First name is invalid, please ensure it is not empty and contains only letters.";
    private static final String LASTNAME_ERROR_FORMAT_MSG = "Last name is invalid, please ensure it is not empty and contains only letters.";


    // Helper to quickly make a UserAccount and try to register with valid details.
    private UserAccount createAndRegisterValidUser() {
        UserAccount account = new UserAccount();
        account.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        return account;
    }

    @Test
    void testSuccessfulRegistration_AllDetailsCorrect() {
        UserAccount account = new UserAccount();
        String feedback = account.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);

        // Check if all success messages and the final success message are there.
        assertTrue(feedback.contains(USERNAME_SUCCESS_MSG), "Username success feedback missing.");
        assertTrue(feedback.contains(PASSWORD_SUCCESS_MSG), "Password success feedback missing.");
        assertTrue(feedback.contains(CELLPHONE_SUCCESS_MSG), "Cellphone success feedback missing.");
        assertTrue(feedback.contains(FIRSTNAME_SUCCESS_MSG), "First name success feedback missing.");
        assertTrue(feedback.contains(LASTNAME_SUCCESS_MSG), "Last name success feedback missing.");
        assertTrue(feedback.endsWith(REGISTRATION_COMPLETE_MSG), "Final registration success message missing or not at the end.");

        // And make sure the details were actually stored in the object.
        assertEquals(VALID_USERNAME, account.getUserName());
        assertEquals(VALID_PASSWORD, account.getPassword());
        assertEquals(VALID_CELL_PHONE, account.getCellPhoneNumber());
        assertEquals(VALID_FIRST_NAME, account.getFirstName());
        assertEquals(VALID_LAST_NAME, account.getLastName());
        assertTrue(account.isRegistered(), "User should be marked as registered.");
    }

    @Test
    void testRegistrationFailure_InvalidUsername_NoUnderscore() {
        UserAccount account = new UserAccount();
        String feedback = account.registerUser(INVALID_USERNAME_NO_UNDERSCORE, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertTrue(feedback.contains(USERNAME_ERROR_MSG), "Username error feedback missing.");
        assertTrue(feedback.endsWith(REGISTRATION_ABORTED_MSG), "Registration aborted message missing.");
        assertFalse(account.isRegistered(), "User should not be marked as registered.");
        assertNull(account.getUserName(), "Username should not be stored on failure.");
    }
    
    @Test
    void testRegistrationFailure_InvalidUsername_TooLong() {
        UserAccount account = new UserAccount();
        String feedback = account.registerUser(INVALID_USERNAME_TOO_LONG, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertTrue(feedback.contains(USERNAME_ERROR_MSG));
        assertTrue(feedback.endsWith(REGISTRATION_ABORTED_MSG));
        assertFalse(account.isRegistered());
    }

    @Test
    void testRegistrationFailure_InvalidPassword_TooShort() {
        UserAccount account = new UserAccount();
        String feedback = account.registerUser(VALID_USERNAME, INVALID_PASSWORD_TOO_SHORT, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertTrue(feedback.contains(PASSWORD_ERROR_MSG));
        assertTrue(feedback.endsWith(REGISTRATION_ABORTED_MSG));
        assertFalse(account.isRegistered());
    }
    
    // Add similar tests for other password invalid conditions (no capital, no number, no special)
    @Test
    void testRegistrationFailure_InvalidPassword_NoCapital() {
        UserAccount account = new UserAccount();
        String feedback = account.registerUser(VALID_USERNAME, INVALID_PASSWORD_NO_CAPITAL, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertTrue(feedback.contains(PASSWORD_ERROR_MSG));
        assertTrue(feedback.endsWith(REGISTRATION_ABORTED_MSG));
        assertFalse(account.isRegistered());
    }


    @Test
    void testRegistrationFailure_InvalidCellPhoneNumber() {
        UserAccount account = new UserAccount();
        String feedback = account.registerUser(VALID_USERNAME, VALID_PASSWORD, INVALID_CELL_PHONE_FORMAT, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertTrue(feedback.contains(CELLPHONE_ERROR_MSG));
        assertTrue(feedback.endsWith(REGISTRATION_ABORTED_MSG));
        assertFalse(account.isRegistered());
    }

    @Test
    void testRegistrationFailure_InvalidFirstName_Empty() {
        UserAccount account = new UserAccount();
        String feedback = account.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, INVALID_FIRST_NAME_EMPTY, VALID_LAST_NAME);
        assertTrue(feedback.contains(FIRSTNAME_ERROR_EMPTY_MSG)); // Using the more specific error message
        assertTrue(feedback.endsWith(REGISTRATION_ABORTED_MSG));
        assertFalse(account.isRegistered());
    }

    @Test
    void testRegistrationFailure_InvalidLastName_WithNumber() {
        UserAccount account = new UserAccount();
        String feedback = account.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, INVALID_LAST_NAME_WITH_NUMBER);
        assertTrue(feedback.contains(LASTNAME_ERROR_FORMAT_MSG)); // Using the more specific error message
        assertTrue(feedback.endsWith(REGISTRATION_ABORTED_MSG));
        assertFalse(account.isRegistered());
    }

    // --- Login Tests ---
    @Test
    void testSuccessfulLogin() {
        UserAccount account = createAndRegisterValidUser(); // Get a pre-registered user.
        assertTrue(account.loginUser(VALID_USERNAME, VALID_PASSWORD), "Login should succeed with correct credentials.");
        assertTrue(account.returnLoginStatus().startsWith("Welcome"), "Login status message should be a welcome.");
    }

    @Test
    void testLoginFailure_IncorrectPassword() {
        UserAccount account = createAndRegisterValidUser();
        assertFalse(account.loginUser(VALID_USERNAME, "WrongPassword123!"), "Login should fail with incorrect password.");
        assertEquals("Username & Password do not match our records, please try again.", account.returnLoginStatus());
    }

    @Test
    void testLoginFailure_IncorrectUsername() {
        UserAccount account = createAndRegisterValidUser();
        assertFalse(account.loginUser("wrong_user", VALID_PASSWORD), "Login should fail with incorrect username.");
        assertEquals("Username & Password do not match our records, please try again.", account.returnLoginStatus());
    }

    @Test
    void testLoginFailure_UserNotRegistered() {
        UserAccount account = new UserAccount(); // A brand new, unregistered account.
        assertFalse(account.loginUser(VALID_USERNAME, VALID_PASSWORD), "Login should fail if user was never registered.");
        assertEquals("Username & Password do not match our records, please try again.", account.returnLoginStatus());
    }
}
