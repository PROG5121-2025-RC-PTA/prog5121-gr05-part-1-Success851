package success;

import javax.swing.JOptionPane;

// This class handles the pop-up boxes for signing up a new user.
public class UserRegistrationUI {

    private UserAccountManager accountManager; // Needs a manager to actually save the user.

    public UserRegistrationUI(UserAccountManager manager) {
        this.accountManager = manager;
    }

    // Kicks off the registration process.
    public UserAccount showRegistrationDialog() {
        JOptionPane.showMessageDialog(null, "Let's get you registered!", "New User Registration", JOptionPane.INFORMATION_MESSAGE);

        // Ask for all the details, one by one.
        String username = JOptionPane.showInputDialog(null, "Enter your desired username (e.g., sue_1):", "Username", JOptionPane.PLAIN_MESSAGE);
        if (username == null) return null; // User cancelled.

        String password = JOptionPane.showInputDialog(null, "Create a password (8+ chars, with capital, number, special char):", "Password", JOptionPane.PLAIN_MESSAGE);
        if (password == null) return null;

        String cellphone = JOptionPane.showInputDialog(null, "Enter your cellphone number (e.g., +27xxxxxxxxx):", "Cellphone", JOptionPane.PLAIN_MESSAGE);
        if (cellphone == null) return null;

        String firstName = JOptionPane.showInputDialog(null, "Enter your first name:", "First Name", JOptionPane.PLAIN_MESSAGE);
        if (firstName == null) return null;

        String lastName = JOptionPane.showInputDialog(null, "Enter your last name:", "Last Name", JOptionPane.PLAIN_MESSAGE);
        if (lastName == null) return null;

        // Create a new account object and try to register.
        UserAccount newUser = new UserAccount();
        String feedback = accountManager.registerNewUser(newUser, username, password, cellphone, firstName, lastName);

        // Show the combined feedback messages.
        JOptionPane.showMessageDialog(null, feedback, "Registration Status", JOptionPane.INFORMATION_MESSAGE);

        // If registration was successful (the UserAccount object will know), return the new user.
        if (newUser.isRegistered()) {
            return newUser;
        } else {
            return null; // Registration failed.
        }
    }
}
