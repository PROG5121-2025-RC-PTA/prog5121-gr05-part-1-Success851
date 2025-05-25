package success;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// This class helps us manage all the ChatMessage objects, especially loading them from files.
public class ChatMessageManager {

    // Finds all message files in the current directory and loads the ones
    // that belong to the given user (either as sender or recipient).
    public static List<ChatMessage> loadUserMessages(String userCellPhoneNumber) {
        List<ChatMessage> userMessages = new ArrayList<>();
        JSONParser parser = new JSONParser();
        File currentDirectory = new File("."); // Look in the same folder as the app.

        // Let's find all files that look like our message files.
        File[] messageFiles = currentDirectory.listFiles((dir, name) ->
                name.startsWith("message_") && name.endsWith(".json"));

        if (messageFiles != null) {
            for (File msgFile : messageFiles) {
                try (FileReader reader = new FileReader(msgFile)) {
                    JSONObject jsonMessage = (JSONObject) parser.parse(reader);

                    // Pull out all the details from the JSON.
                    String id = (String) jsonMessage.get("MESSAGE_ID");
                    String sender = (String) jsonMessage.get("MESSAGE_SENDER");
                    String recipient = (String) jsonMessage.get("MESSAGE_RECIPIENT");
                    String payload = (String) jsonMessage.get("MESSAGE_PAYLOAD");

                    // JSONSimple often reads numbers as Long, so we need to handle that.
                    long indexLong = (Long) jsonMessage.getOrDefault("MESSAGE_INDEX", 0L);
                    int index = (int) indexLong;

                    String hash = (String) jsonMessage.get("MESSAGE_HASH");

                    // Important bit: Only add this message if it's relevant to our user.
                    if (userCellPhoneNumber != null &&
                        (userCellPhoneNumber.equals(sender) || userCellPhoneNumber.equals(recipient))) {
                        // Use the package-private constructor of ChatMessage to recreate the object.
                        ChatMessage message = new ChatMessage(id, sender, recipient, payload, index, hash);
                        userMessages.add(message);
                    }
                } catch (IOException | ParseException e) {
                    System.err.println("Yikes! Trouble loading message from file " + msgFile.getName() + ": " + e.getMessage());
                } catch (Exception e) { // Catch any other weird errors.
                    System.err.println("Something unexpected happened with file " + msgFile.getName() + ": " + e.getMessage());
                    e.printStackTrace(); // Good to see the full error for debugging.
                }
            }
        }
        // Give back the list of messages we found for this user.
        return userMessages;
    }
}
