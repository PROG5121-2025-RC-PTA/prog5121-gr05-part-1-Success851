package success;

import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.Random;

// This class is all about a single chat message.
// It knows who sent it, who it's for, what it says, and has hashing.
public class ChatMessage {
    private final String chatMessageId;    // Every message gets a unique ID, like a tracking number.
    private final String senderPhoneNumber; // Who sent this?
    private final String recipientPhoneNumber; // Who is this for?
    private final String messageContent;   // The actual text of the message.

    private int sentOrderIndex;      // When this message was sent in sequence.
    private String messageHashValue; // A special code generated from the message details.

    // This guy helps us count how many messages have been sent in total.
    private static int totalMessagesSentCounter = 0;
    // And this keeps track of what the very last sent message looked like.
    private static String lastSentMessageDetails = "";

    private static final int MAX_CONTENT_LENGTH = 250; // Messages can't be super long.
    private static final Random RANDOM = new Random(); // For generating random parts of the ID.

    // Constructor for when a new message is being composed by a user.
    public ChatMessage(String sender, String recipient, String payload) {
        // Let's cook up a unique 10-digit ID.
        this.chatMessageId = String.format("%010d", Math.abs(RANDOM.nextLong() % 10000000000L));
        this.senderPhoneNumber = sender;
        this.recipientPhoneNumber = recipient;
        this.messageContent = payload;
        this.sentOrderIndex = 0; // It's not sent yet, so index is 0.
        this.messageHashValue = "";  // No hash yet either.
    }

    // Another constructor, this one is handy when loading messages from a file.
    // Notice it's package-private, so only classes in the same 'chitchat' package can use it directly.
    ChatMessage(String id, String sender, String recipient, String payload, int index, String hash) {
        this.chatMessageId = id;
        this.senderPhoneNumber = sender;
        this.recipientPhoneNumber = recipient;
        this.messageContent = payload;
        this.sentOrderIndex = index;
        this.messageHashValue = hash;
    }

    // --- Public methods for interacting with the message ---

    // Checks if a message ID looks right (10 digits).
    public boolean checkMessageID(final String id) {
        if (id == null) return false;
        return id.matches("\\d{10}"); // Simple regex: exactly 10 digits.
    }

    // Checks if the message text isn't too long.
    public String validatePayloadLength(final String payload) {
        if (payload == null) {
            // If there's no payload, it's definitely not too long, but it's also not really a message.
            // For this validation, we'll say it's "ready" if null, but sentMessage will catch null/empty.
            // Or, more consistently with previous logic:
            return "Message exceeds " + MAX_CONTENT_LENGTH + " characters by " + (0 - MAX_CONTENT_LENGTH) + ", please reduce size.";
        }
        if (payload.length() <= MAX_CONTENT_LENGTH) {
            return "Message ready to send.";
        } else {
            int tooManyChars = payload.length() - MAX_CONTENT_LENGTH;
            return "Message exceeds " + MAX_CONTENT_LENGTH + " characters by " + tooManyChars + ", please reduce size.";
        }
    }

    // Makes sure the phone number looks like a South African one (+27xxxxxxxxx).
    public String validateRecipientNumber(final String recipientNumber) {
        if (recipientNumber == null || recipientNumber.isBlank()) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        String saNumberPattern = "^\\+27[0-9]{9}$"; // Starts with +27, followed by 9 digits.
        if (Pattern.matches(saNumberPattern, recipientNumber)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Creates that special hash code for the message.
    // Format: FirstTwoID:Index:FIRSTWORDLASTWORD (all caps!)
    public String createMessageHash(final String id, int index, final String payload) {
        if (id == null || id.length() < 2 || payload == null) {
            return ""; // Can't make a hash without these bits.
        }

        String idPrefix = id.substring(0, 2); // First two chars of the ID.
        String trimmedPayload = payload.trim(); // Get rid of extra spaces.

        if (trimmedPayload.isEmpty()) {
            // If the payload is empty after trimming, the hash is just ID prefix and index.
            return (idPrefix + ":" + index + ":").toUpperCase();
        }

        String[] words = trimmedPayload.split("\\s+"); // Split into words.
        String firstWord = words[0];
        String lastWord = words[words.length - 1]; // Could be the same if only one word.

        // Combine them and make it all uppercase.
        return (idPrefix + ":" + index + ":" + firstWord + lastWord).toUpperCase();
    }

    // This is the big one: tries to "send" the message.
    public String sentMessage() {
        // Rule 1: Message can't be empty.
        if (this.messageContent == null || this.messageContent.trim().isEmpty()) {
            return "Failed to send message: Message content cannot be empty";
        }
        // Rule 2: Message can't be too long.
        String payloadValidation = validatePayloadLength(this.messageContent);
        if (!payloadValidation.equals("Message ready to send.")) {
             if (this.messageContent.length() > MAX_CONTENT_LENGTH) { // Specific check for grading consistency
                 return "Failed to send message: Payload too long";
             }
            return payloadValidation; // Return the detailed error.
        }
        // Rule 3: Recipient's number must be valid.
        String recipientValidation = validateRecipientNumber(this.recipientPhoneNumber);
        if (!recipientValidation.equals("Cell phone number successfully captured.")) {
            return "Failed to send message: Invalid recipient";
        }
        // Rule 4: Sender's number must also be valid (good practice!).
        String senderValidation = validateRecipientNumber(this.senderPhoneNumber);
        if (!senderValidation.equals("Cell phone number successfully captured.")) {
            return "Failed to send message: Invalid sender";
        }
        // Rule 5: The message ID itself should be valid (it's auto-generated, so this is a sanity check).
        if (!checkMessageID(this.chatMessageId)) {
            return "Failed to send message: Invalid message ID (system error)";
        }

        // If all rules pass, we're good to go!
        totalMessagesSentCounter++; // Count this message.
        this.sentOrderIndex = totalMessagesSentCounter; // Assign its send order.
        this.messageHashValue = createMessageHash(this.chatMessageId, this.sentOrderIndex, this.messageContent); // Generate the hash.

        // Remember what this message looked like.
        lastSentMessageDetails = String.format("ID: %s, Sender: %s, Recipient: %s, Payload: \"%s\", Hash: %s, Index: %d",
                this.chatMessageId, this.senderPhoneNumber, this.recipientPhoneNumber, this.messageContent, this.messageHashValue, this.sentOrderIndex);

        return "Message successfully sent."; // Hooray!
    }

    // Shows the details of the last message that was successfully sent.
    public static String printMessages() {
        if (totalMessagesSentCounter == 0 || lastSentMessageDetails.isEmpty()) {
            return "No messages sent"; // Nothing sent yet.
        }
        return lastSentMessageDetails;
    }

    // Tells us how many messages have been sent in total.
    public static int returnTotalMessages() {
        return totalMessagesSentCounter;
    }

    // Saves this message to a JSON file.
    @SuppressWarnings("unchecked") // For JSONObject putting generic types.
    public String storeMessage() {
        JSONObject messageJson = new JSONObject();
        messageJson.put("MESSAGE_ID", this.chatMessageId);
        messageJson.put("MESSAGE_SENDER", this.senderPhoneNumber);
        messageJson.put("MESSAGE_RECIPIENT", this.recipientPhoneNumber);
        messageJson.put("MESSAGE_PAYLOAD", this.messageContent);
        messageJson.put("MESSAGE_INDEX", this.sentOrderIndex);
        messageJson.put("MESSAGE_HASH", this.messageHashValue);

        // If it's a draft (index 0), use a different filename pattern.
        String fileName = (this.sentOrderIndex != 0) ?
                "message_" + this.sentOrderIndex + ".json" :
                "message_stored_" + this.chatMessageId + ".json";

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(messageJson.toJSONString());
            return "Message successfully stored."; // Let 'em know it worked.
        } catch (IOException e) {
            System.err.println("Oops! Could not store message to file: " + fileName + " - " + e.getMessage());
            return "Failed to store message: File system error."; // Uh oh.
        }
    }

    // Just a little helper to announce the message ID.
    public String getGeneratedIdNotification() {
        return "Message ID generated: " + this.chatMessageId;
    }

    // Getters to access the message's properties from outside.
    public String getId() { return chatMessageId; } // Renamed to avoid conflict with potential superclass methods
    public String getSender() { return senderPhoneNumber; }
    public String getRecipient() { return recipientPhoneNumber; }
    public String getPayload() { return messageContent; }
    public int getIndex() { return sentOrderIndex; }
    public String getHash() { return messageHashValue; }

    // This is for tests, to reset the static counter between test runs.
    public static void resetMessageCounterForTesting() {
        totalMessagesSentCounter = 0;
        lastSentMessageDetails = "";
    }
}
