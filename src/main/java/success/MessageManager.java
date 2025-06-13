package success;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// This class manages all chat message operations.
public class MessageManager {
    
    private ArrayList<Message> sentMessages = new ArrayList<>();
    private ArrayList<Message> draftMessages = new ArrayList<>();
    
    private static final String SENT_FILE = "sentMessages.json";
    private static final String DRAFT_FILE = "draftMessages.json";
    
    // Constructor loads messages from files when the app starts.
    public MessageManager() {
        loadMessages(SENT_FILE, sentMessages);
        loadMessages(DRAFT_FILE, draftMessages);
    }
    
    // Part 2: Add a message to the sent list and save.
    public void sendMessage(Message message) {
        sentMessages.add(message);
        saveMessages(SENT_FILE, sentMessages);
    }

    // Part 2: Add a message to the drafts list and save.
    public void saveDraft(Message message) {
        draftMessages.add(message);
        saveMessages(DRAFT_FILE, draftMessages);
    }

    // --- Part 3: Report and Management Methods ---

    // Generates a report of all sent messages.
    public String displayAllSentMessages() {
        StringBuilder report = new StringBuilder("--- All Sent Messages ---\n\n");
        if (sentMessages.isEmpty()) {
            report.append("No messages have been sent.");
        } else {
            for (Message msg : sentMessages) {
                report.append(msg.toString()).append("\n\n");
            }
        }
        return report.toString();
    }
    
    // Searches messages by sender or content.
    public String searchMessages(String searchTerm) {
        StringBuilder results = new StringBuilder("--- Search Results for '").append(searchTerm).append("' ---\n\n");
        boolean found = false;
        for (Message msg : sentMessages) {
            // Check if sender username matches or content contains the search term.
            if (msg.getSender().equalsIgnoreCase(searchTerm) || msg.getContent().toLowerCase().contains(searchTerm.toLowerCase())) {
                results.append(msg.toString()).append("\n\n");
                found = true;
            }
        }
        if (!found) {
            results.append("No messages found matching your search.");
        }
        return results.toString();
    }
    
    // Deletes a message by its ID.
    public boolean deleteMessage(int messageId) {
        // Use removeIf for a clean way to find and delete the message.
        boolean removed = sentMessages.removeIf(msg -> msg.getMessageId() == messageId);
        if (removed) {
            saveMessages(SENT_FILE, sentMessages); // Save if a change was made.
        }
        return removed;
    }

    // --- JSON File Handling ---

    private void saveMessages(String filePath, ArrayList<Message> messages) {
        JSONArray messageList = new JSONArray();
        for (Message msg : messages) {
            JSONObject msgDetails = new JSONObject();
            msgDetails.put("id", msg.getMessageId());
            msgDetails.put("sender", msg.getSender());
            msgDetails.put("recipient", msg.getRecipient());
            msgDetails.put("content", msg.getContent());
            messageList.add(msgDetails);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(messageList.toJSONString());
        } catch (IOException e) {
            System.out.println("Error saving messages to " + filePath);
        }
    }
    
    private void loadMessages(String filePath, ArrayList<Message> messages) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            JSONArray messageList = (JSONArray) parser.parse(reader);
            messages.clear();
            for (Object obj : messageList) {
                JSONObject msgJson = (JSONObject) obj;
                // JSON-simple reads numbers as Long, so we need to cast it properly.
                long idLong = (long) msgJson.get("id");
                messages.add(new Message(
                    (int) idLong,
                    (String) msgJson.get("sender"),
                    (String) msgJson.get("recipient"),
                    (String) msgJson.get("content")
                ));
            }
        } catch (IOException | ParseException e) {
            // File might not exist on first run, it's fine.
            messages.clear();
        }
    }
}
