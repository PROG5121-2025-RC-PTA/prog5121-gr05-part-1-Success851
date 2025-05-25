package success;

import javax.swing.JOptionPane;

// This class shows the pop-up boxes for logging in.
public class UserLoginUI {

    private UserAccountManager accountManager; // Needs to check users against the stored list.

    public UserLoginUI(UserAccountManager manager) {
        this.accountManager = manager;
    }

    // Starts the login dialogs.
    public UserAccount showLoginDialog() {
        JOptionPane.showMessageDialog(null, "Welcome back! Please log in.", "User Login", JOptionPane.INFORMATION_MESSAGE);

        String username = JOptionPane.showInputDialog(null, "Enter your username:", "Login - Username", JOptionPane.PLAIN_MESSAGE);
        if (username == null) return null; // User hit cancel.

        String password = JOptionPane.showInputDialog(null, "Enter your password:", "Login - Password", JOptionPane.PLAIN_MESSAGE);
        if (password == null) return null;

        // Try to find the user.
        UserAccount user = accountManager.findUserByUsername(username);

        if (user != null) {
            // User found, now try to log them in with the password.
            if (user.loginUser(username, password)) {
                JOptionPane.showMessageDialog(null, user.returnLoginStatus(), "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                return user; // Success! Return the logged-in user.
            } else {
                JOptionPane.showMessageDialog(null, user.returnLoginStatus(), "Login Failed", JOptionPane.ERROR_MESSAGE);
                return null; // Wrong password.
            }
        } else {
            // User not found at all.
            JOptionPane.showMessageDialog(null, "Login Failed: Username '" + username + "' not found.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
