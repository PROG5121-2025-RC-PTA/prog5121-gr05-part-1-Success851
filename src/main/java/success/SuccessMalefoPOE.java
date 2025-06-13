package success;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// This is the main class that runs the application.
// All user interaction happens through JOptionPane dialogs.
public class SuccessMalefoPOE {
    
    private static AccountManager accountManager = new AccountManager();
    private static MessageManager messageManager = new MessageManager();
    private static User currentUser = null;

    public static void main(String[] args) {
        showWelcomeMenu();
    }
    
    // The first menu the user sees.
    public static void showWelcomeMenu() {
        while (currentUser == null) {
            String[] options = {"Login", "Register", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Welcome to QuickChat!\nPlease select an option.", 
                    "QuickChat", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0: // Login
                    handleLogin();
                    break;
                case 1: // Register
                    handleRegister();
                    break;
                case 2: // Exit
                default: // Also handles closing the dialog
                    JOptionPane.showMessageDialog(null, "Goodbye!");
                    System.exit(0);
                    break;
            }
        }
    }
    
    // Handles the user registration process.
    public static void handleRegister() {
        String firstName = JOptionPane.showInputDialog("Enter First Name:");
        if (firstName == null) return; // User cancelled
        
        String lastName = JOptionPane.showInputDialog("Enter Last Name:");
        if (lastName == null) return;
        
        String username = JOptionPane.showInputDialog("Enter Username (must contain '_' and be max 5 chars):");
        if (username == null) return;
        
        if (!accountManager.checkUserName(username)) {
            JOptionPane.showMessageDialog(null, "Username is not correctly formatted.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String password = JOptionPane.showInputDialog("Enter Password (min 8 chars, 1 capital, 1 number, 1 special char):");
        if (password == null) return;

        if (!accountManager.checkPasswordComplexity(password)) {
            JOptionPane.showMessageDialog(null, "Password does not meet the complexity requirements.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(username, password, firstName, lastName);
        if (accountManager.registerUser(newUser)) {
            JOptionPane.showMessageDialog(null, "Registration successful! Please log in.");
        } else {
            JOptionPane.showMessageDialog(null, "This username is already taken.", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Handles the user login process.
    public static void handleLogin() {
        String username = JOptionPane.showInputDialog("Enter Username:");
        if (username == null) return;
        
        String password = JOptionPane.showInputDialog("Enter Password:");
        if (password == null) return;

        currentUser = accountManager.loginUser(username, password);
        
        if (currentUser != null) {
            JOptionPane.showMessageDialog(null, "Welcome " + currentUser.getFirstName() + ", it is great to see you again.");
            showMainMenu(); // Move to the main application menu.
        } else {
            JOptionPane.showMessageDialog(null, "Username or password incorrect, please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // The main menu shown after a user logs in.
    public static void showMainMenu() {
        while (true) {
            String[] options = {"Send Message", "View Reports", "Logout"};
            int choice = JOptionPane.showOptionDialog(null, "What would you like to do?", "Main Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            
            switch(choice) {
                case 0: // Send Message
                    handleSendMessage();
                    break;
                case 1: // View Reports
                    showReportsMenu();
                    break;
                case 2: // Logout
                default:
                    currentUser = null;
                    JOptionPane.showMessageDialog(null, "You have been logged out.");
                    return; // Go back to the welcome menu
            }
        }
    }
    
    // Handles sending or drafting a new message.
    public static void handleSendMessage() {
        String recipient = JOptionPane.showInputDialog("Enter recipient's username:");
        if (recipient == null) return;
        
        // Check if the recipient user actually exists.
        if (accountManager.findUser(recipient) == null) {
            JOptionPane.showMessageDialog(null, "User '" + recipient + "' does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String content = JOptionPane.showInputDialog("Enter your message:");
        if (content == null || content.trim().isEmpty()) return;
        
        Message newMessage = new Message(currentUser.getUsername(), recipient, content);
        
        String[] options = {"Send", "Save as Draft", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, "Send or save this message?", "Confirm Message",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            messageManager.sendMessage(newMessage);
            JOptionPane.showMessageDialog(null, "Message sent!");
        } else if (choice == 1) {
            messageManager.saveDraft(newMessage);
            JOptionPane.showMessageDialog(null, "Message saved as draft.");
        }
    }
    
    // The menu for all the Part 3 reports.
    public static void showReportsMenu() {
        while(true) {
            String[] options = {"Display All Users", "Find a User", "Delete a User", "Display All Messages", "Search Messages", "Delete a Message", "Back to Main Menu"};
            int choice = JOptionPane.showOptionDialog(null, "Select a report or action", "Reports Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            
            switch(choice) {
                case 0: // Display All Users
                    showScrollableMessage("All Users", accountManager.displayAllUsers());
                    break;
                case 1: // Find a User
                    String userToFind = JOptionPane.showInputDialog("Enter username to find:");
                    if (userToFind != null) {
                        User foundUser = accountManager.findUser(userToFind);
                        String result = (foundUser != null) ? "User Found:\nName: " + foundUser.getFirstName() + " " + foundUser.getLastName() : "User not found.";
                        JOptionPane.showMessageDialog(null, result);
                    }
                    break;
                case 2: // Delete a User
                    String userToDelete = JOptionPane.showInputDialog("Enter username to DELETE:");
                    if (userToDelete != null) {
                        boolean deleted = accountManager.deleteUser(userToDelete);
                        JOptionPane.showMessageDialog(null, deleted ? "User deleted." : "User not found.");
                    }
                    break;
                case 3: // Display All Messages
                    showScrollableMessage("All Sent Messages", messageManager.displayAllSentMessages());
                    break;
                case 4: // Search Messages
                    String term = JOptionPane.showInputDialog("Enter sender username or keyword to search:");
                    if(term != null) {
                        showScrollableMessage("Search Results", messageManager.searchMessages(term));
                    }
                    break;
                case 5: // Delete a Message
                    String idStr = JOptionPane.showInputDialog("Enter ID of message to DELETE:");
                    try {
                        int id = Integer.parseInt(idStr);
                        boolean deleted = messageManager.deleteMessage(id);
                        JOptionPane.showMessageDialog(null, deleted ? "Message deleted." : "Message with that ID not found.");
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid ID format.");
                    }
                    break;
                case 6: // Back to Main Menu
                default:
                    return;
            }
        }
    }
    
    // A helper method to show long text (like reports) in a scrollable pane.
    public static void showScrollableMessage(String title, String message) {
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setText(message);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
