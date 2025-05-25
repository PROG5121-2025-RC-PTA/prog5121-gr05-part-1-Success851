package success;

import javax.swing.JOptionPane;
import java.util.List; // For handling lists of messages

// This class manages all the pop-up interactions for sending and viewing messages.
public class ChatMessagingUI {

    private UserAccount currentUser; // We need to know who is sending/receiving.
    // ChatMessageManager is static, so we can call its methods directly.

    public ChatMessagingUI(UserAccount loggedInUser) {
        this.currentUser = loggedInUser;
    }

    // The main loop for messaging actions.
    public void startMessagingSession() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Error: No user logged in for messaging.", "Messaging Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null,
                "Welcome to SimpleChat Messaging, " + currentUser.getFirstName() + "!",
                "SimpleChat Messaging", JOptionPane.INFORMATION_MESSAGE);

        boolean inMessagingLoop = true;
        while (inMessagingLoop) {
            String[] menuOptions = {"Compose & Send Message", "View My Messages (Inbox/Sent)", "Show Last Sent Details", "Exit Messaging"};
            int menuChoice = JOptionPane.showOptionDialog(null,
                    "What would you like to do?",
                    "Messaging Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, menuOptions, menuOptions[0]);

            switch (menuChoice) {
                case 0: // Compose & Send Message
                    handleComposeAndSend();
                    break;
                case 1: // View My Messages
                    handleViewMessages();
                    break;
                case 2: // Show Last Sent Details
                    JOptionPane.showMessageDialog(null, ChatMessage.printMessages(), "Last Sent Message", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 3: // Exit Messaging
                default: // Also handles closing the dialog
                    inMessagingLoop = false;
                    break;
            }
        }
        JOptionPane.showMessageDialog(null, "Exiting messaging session.", "Messaging", JOptionPane.INFORMATION_MESSAGE);
    }

    // Handles getting details for one or more messages to send/store.
    private void handleComposeAndSend() {
        String numMessagesStr = JOptionPane.showInputDialog(null,
                "How many messages do you want to process now?",
                "Number of Messages", JOptionPane.QUESTION_MESSAGE);

        if (numMessagesStr == null || numMessagesStr.trim().isEmpty()) return; // User cancelled or entered nothing.

        int numMessages;
        try {
            numMessages = Integer.parseInt(numMessagesStr);
            if (numMessages <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "That wasn't a valid number. Try again!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < numMessages; i++) {
            JOptionPane.showMessageDialog(null, "Composing Message " + (i + 1) + " of " + numMessages, "Message Progress", JOptionPane.INFORMATION_MESSAGE);

            String recipientPhone = JOptionPane.showInputDialog(null, "Recipient's phone number (e.g., +27xxxxxxxxx):", "Recipient", JOptionPane.PLAIN_MESSAGE);
            if (recipientPhone == null) continue; // User cancelled this message.

            String messageText = JOptionPane.showInputDialog(null, "Your message content:", "Message Text", JOptionPane.PLAIN_MESSAGE);
            if (messageText == null) continue;

            // Create the message object. The sender is our currently logged-in user.
            ChatMessage newMessage = new ChatMessage(currentUser.getCellPhoneNumber(), recipientPhone, messageText);
            JOptionPane.showMessageDialog(null, newMessage.getGeneratedIdNotification(), "Message ID Created", JOptionPane.INFORMATION_MESSAGE);

            // Validate before offering actions.
            String recipientValidation = newMessage.validateRecipientNumber(recipientPhone); // Or use newMessage.getRecipient()
            if (!recipientValidation.equals("Cell phone number successfully captured.")) {
                JOptionPane.showMessageDialog(null, "Message " + (i + 1) + " Error:\n" + recipientValidation, "Recipient Invalid", JOptionPane.ERROR_MESSAGE);
                continue; // Next message
            }
            String payloadValidation = newMessage.validatePayloadLength(messageText); // Or use newMessage.getPayload()
            if (!payloadValidation.equals("Message ready to send.")) {
                JOptionPane.showMessageDialog(null, "Message " + (i + 1) + " Error:\n" + payloadValidation, "Content Invalid", JOptionPane.ERROR_MESSAGE);
                continue; // Next message
            }


            String[] actions = {"Send Now", "Store as Draft", "Discard"};
            int action = JOptionPane.showOptionDialog(null,
                    "Action for this message to " + recipientPhone + "?",
                    "Choose Action",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, actions, actions[0]);

            String outcome;
            switch (action) {
                case 0: // Send Now
                    outcome = newMessage.sentMessage(); // This also validates internally.
                    JOptionPane.showMessageDialog(null, outcome, "Send Attempt", JOptionPane.INFORMATION_MESSAGE);
                    if (outcome.equals("Message successfully sent.")) {
                        // If sent, also store it.
                        String storeOutcome = newMessage.storeMessage();
                        // Optionally notify about the storage of the sent message.
                        System.out.println("Sent message storage status: " + storeOutcome);
                    }
                    break;
                case 1: // Store as Draft
                    outcome = newMessage.storeMessage(); // Index will be 0 for drafts.
                    JOptionPane.showMessageDialog(null, outcome, "Store Draft", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 2: // Discard
                default:
                    JOptionPane.showMessageDialog(null, "Message discarded.", "Discarded", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        }
        JOptionPane.showMessageDialog(null, "Finished processing batch. Total messages ever sent: " + ChatMessage.returnTotalMessages(), "Batch Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    // Shows messages related to the current user.
    private void handleViewMessages() {
        // Load all messages for the current user (both sent by them and received by them).
        List<ChatMessage> userMessages = ChatMessageManager.loadUserMessages(currentUser.getCellPhoneNumber());

        if (userMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have no messages in your inbox or sent items.", "No Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder messageDisplay = new StringBuilder("Your Messages:\n--------------------------\n");
        for (ChatMessage msg : userMessages) {
            String direction = msg.getSender().equals(currentUser.getCellPhoneNumber()) ? "To: " + msg.getRecipient() : "From: " + msg.getSender();
            String status = msg.getIndex() == 0 ? "(Draft/Stored)" : "(Sent/Received - Index: " + msg.getIndex() + ")";
            messageDisplay.append(String.format("[%s] %s\nContent: %s\nID: %s, Hash: %s\n--------------------------\n",
                    status, direction, msg.getPayload(), msg.getId(), msg.getHash()));
        }

        // JOptionPane has limits on how much text it can show well.
        // For very long lists, a JTextArea in a JScrollPane is better, but we're sticking to JOptionPane.
        if (messageDisplay.length() > 1000) { // Simple check for very long string
            JOptionPane.showMessageDialog(null, "You have many messages. Showing the first part.\n\n" + messageDisplay.substring(0,1000) + "...", "Your Messages", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, messageDisplay.toString(), "Your Messages", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
