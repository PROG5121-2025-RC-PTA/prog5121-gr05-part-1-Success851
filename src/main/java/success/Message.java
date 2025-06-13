package success;

import java.util.concurrent.ThreadLocalRandom;

// This class is a blueprint for a single chat message.
public class Message {
    
    private int messageId;
    private String sender;
    private String recipient;
    private String content;

    // Constructor for creating a brand new message.
    // It assigns a random 4-digit ID as required.
    public Message(String sender, String recipient, String content) {
        this.messageId = ThreadLocalRandom.current().nextInt(1000, 10000);
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }
    
    // Constructor for loading a message from a file (it already has an ID).
    public Message(int id, String sender, String recipient, String content) {
        this.messageId = id;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    // Getters to access the message's details.
    public int getMessageId() {
        return messageId;
    }

    public String getSender() {
        return sender;
    }
    
    public String getRecipient(){
        return recipient;
    }

    public String getContent() {
        return content;
    }
    
    // A handy way to print the message details.
    @Override
    public String toString() {
        return "ID: " + messageId + ", From: " + sender + ", To: " + recipient + "\nMessage: " + content;
    }
}
