package success;

import java.util.regex.Pattern;

// Handles all the nitty-gritty for a user's account - registration and login.
// Keeps track of user details and whether they've successfully logged in.
public class UserAccount {
    private String storedUserName;
    private String storedPassword;
    private String storedCellPhoneNumber;
    private String storedFirstName;
    private String storedLastName;
    private boolean accessGranted = false; // Initially, no access!

    // --- REGISTRATION LOGIC ---

    // This is where we try to sign up a new user.
    // It checks all the details and tells us what happened.
    public String registerUser(String newUserName, String newPassword, String newCellPhoneNumber,
                              String newFirstName, String newLastName) {
        StringBuilder feedbackBuilder = new StringBuilder();
        boolean isOverallValid = true; // Let's assume everything is okay until proven otherwise.

        // Check username
        if (checkUserName(newUserName)) {
            feedbackBuilder.append("Username successfully captured\n");
        } else {
            feedbackBuilder.append("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.\n");
            isOverallValid = false;
        }

        // Check password
        if (checkPasswordComplexity(newPassword)) {
            feedbackBuilder.append("Password successfully captured\n");
        } else {
            feedbackBuilder.append("Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.\n");
            isOverallValid = false;
        }

        // Check cell phone number
        if (checkCellPhoneNumber(newCellPhoneNumber)) {
            feedbackBuilder.append("Cellphone number successfully captured\n");
        } else {
            feedbackBuilder.append("Cellphone number is incorrectly formatted or does not contain an international code, please correct the number and try again.\n");
            isOverallValid = false;
        }

        // Check first name
        if (isNameValid(newFirstName)) {
            feedbackBuilder.append("First name successfully captured\n");
        } else {
            feedbackBuilder.append("First name is invalid, please ensure it is not empty and contains only letters.\n"); // Adjusted error message slightly for clarity
            isOverallValid = false;
        }

        // Check last name
        if (isNameValid(newLastName)) {
            feedbackBuilder.append("Last name successfully captured\n");
        } else {
            feedbackBuilder.append("Last name is invalid, please ensure it is not empty and contains only letters.\n"); // Adjusted error message slightly for clarity
            isOverallValid = false;
        }

        // If everything checked out, let's save the details!
        if (isOverallValid) {
            this.storedUserName = newUserName;
            this.storedPassword = newPassword;
            this.storedCellPhoneNumber = newCellPhoneNumber;
            this.storedFirstName = newFirstName;
            this.storedLastName = newLastName;
            feedbackBuilder.append("Registration successful");
        } else {
            feedbackBuilder.append("Registration aborted");
        }

        return feedbackBuilder.toString();
    }

    // --- LOGIN LOGIC ---

    // Checks if the entered username and password match what we have stored.
    public boolean loginUser(String userNameAttempt, String passwordAttempt) {
        // Both username and password must be provided and must match.
        accessGranted = storedUserName != null && storedUserName.equals(userNameAttempt) &&
                        storedPassword != null && storedPassword.equals(passwordAttempt);
        return accessGranted;
    }

    // Gives a nice welcome message or a "try again" message after a login attempt.
    public String returnLoginStatus() {
        if (accessGranted) {
            return String.format("Welcome %s %s,\nit is great to see you.", storedFirstName, storedLastName);
        } else {
            return "Username & Password do not match our records, please try again.";
        }
    }

    // --- UTILITY AND VALIDATION METHODS ---

    // Quick check to see if this account has any details stored (i.e., if registration happened).
    public boolean isRegistered() {
        return storedUserName != null && !storedUserName.isEmpty();
    }

    // Getters for all the stored details.
    public String getUserName() { return storedUserName; }
    public String getPassword() { return storedPassword; }
    public String getCellPhoneNumber() { return storedCellPhoneNumber; }
    public String getFirstName() { return storedFirstName; }
    public String getLastName() { return storedLastName; }

    // Validation rule: Username must be 5 chars or less and include an underscore.
    private boolean checkUserName(String userName) {
        return userName != null && userName.length() <= 5 && userName.contains("_");
    }

    // Validation rule: Password needs to be a bit complex.
    private boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false; // Too short!
        }
        // Must have: an uppercase letter, a digit, and a special character.
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]").matcher(password).find();
        return hasUpper && hasDigit && hasSpecial;
    }

    // Validation rule: Cell phone number must be in +27xxxxxxxxx format.
    private boolean checkCellPhoneNumber(String cellPhoneNumber) {
        return cellPhoneNumber != null && Pattern.matches("^\\+27[0-9]{9}$", cellPhoneNumber);
    }

    // Validation rule: Names should just be letters and not empty.
    private boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z]+$");
    }
}
