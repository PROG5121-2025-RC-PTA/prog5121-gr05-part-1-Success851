package success;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

// This is the main starting point for Success Malefo's SimpleChat app!
// It coordinates the different parts of the application like registration, login, and messaging.
public class SuccessMalefoApp {

    public static void main(String[] args) {
        // Let's try to make the pop-up boxes look a bit nicer, like the system's style.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't set the system look and feel, using default. Error: " + e.getMessage());
        }

        // We need a way to manage all the user accounts.
        UserAccountManager accountManager = new UserAccountManager();
        UserAccount currentUser = null; // No one is logged in yet.

        // Welcome message!
        JOptionPane.showMessageDialog(null, "Welcome to SimpleChat by Success Malefo!", "SimpleChat", JOptionPane.INFORMATION_MESSAGE);

        // The main application loop. It keeps running until the user decides to exit.
        while (true) {
            String[] options;
            if (currentUser == null) {
                // If no one is logged in, they can register, login, or exit.
                options = new String[]{"Register New User", "Login", "Exit SimpleChat"};
            } else {
                // If someone IS logged in, they can message or logout.
                options = new String[]{"Start Messaging", "Logout", "Exit SimpleChat"};
            }

            int choice = JOptionPane.showOptionDialog(null,
                    currentUser == null ? "What would you like to do?" : "Hi " + currentUser.getFirstName() + "! What's next?",
                    "SimpleChat Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (currentUser == null) { // --- Actions when NOT logged in ---
                switch (choice) {
                    case 0: // Register New User
                        UserRegistrationUI registrationUI = new UserRegistrationUI(accountManager);
                        registrationUI.showRegistrationDialog(); // This handles the whole registration process.
                        break;
                    case 1: // Login
                        UserLoginUI loginUI = new UserLoginUI(accountManager);
                        UserAccount loggedInUser = loginUI.showLoginDialog();
                        if (loggedInUser != null) {
                            currentUser = loggedInUser; // Yay, login successful!
                        }
                        break;
                    case 2: // Exit
                    default: // Also handles closing the dialog.
                        JOptionPane.showMessageDialog(null, "Thanks for using SimpleChat! Goodbye.", "Exiting", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0); // Time to close the app.
                        break;
                }
            } else { // --- Actions when LOGGED IN ---
                switch (choice) {
                    case 0: // Start Messaging
                        ChatMessagingUI messagingUI = new ChatMessagingUI(currentUser);
                        messagingUI.startMessagingSession(); // Dive into the messaging part.
                        break;
                    case 1: // Logout
                        JOptionPane.showMessageDialog(null, "You have been logged out, " + currentUser.getFirstName() + ".", "Logout Successful", JOptionPane.INFORMATION_MESSAGE);
                        currentUser = null; // Forget who was logged in.
                        ChatMessage.resetMessageCounterForTesting(); // Good idea to reset this if user logs out, for a fresh count next session for any user
                        break;
                    case 2: // Exit
                    default: // Also handles closing the dialog.
                        JOptionPane.showMessageDialog(null, "Thanks for using SimpleChat! Goodbye, " + currentUser.getFirstName() + ".", "Exiting", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                        break;
                }
            }
        }
    }
}
