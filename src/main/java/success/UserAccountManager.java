package success;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List; // Using List interface

// This class is like the librarian for user accounts.
// It keeps a list of all users, loads them from a file, and saves them back.
public class UserAccountManager {
    private List<UserAccount> users; // A list to hold all our UserAccount objects.
    private static final String USERS_DATA_FILE = "user_accounts.json"; // File where user data is kept.

    public UserAccountManager() {
        this.users = new ArrayList<>();
        loadUserAccounts(); // Try to load any existing users when we start up.
    }

    // Tries to register a new user. If successful, adds them to our list and saves.
    public String registerNewUser(UserAccount userToRegister, String username, String password,
                                 String cellphone, String firstName, String lastName) {
        // First, let the UserAccount object do its own validation and registration.
        String registrationFeedback = userToRegister.registerUser(username, password, cellphone, firstName, lastName);

        // If the UserAccount says registration was a success...
        if (userToRegister.isRegistered()) {
            // Check if username already exists to prevent duplicates
            for (UserAccount existingUser : users) {
                if (existingUser.getUserName().equals(username)) {
                    return "Registration aborted: Username '" + username + "' already exists.";
                }
            }
            this.users.add(userToRegister); // Add to our list.
            saveUserAccounts(); // And save the updated list to the file.
        }
        return registrationFeedback; // Return the feedback (could be success or failure messages).
    }

    // Looks for a user by their username.
    public UserAccount findUserByUsername(String username) {
        for (UserAccount user : this.users) {
            if (user.getUserName() != null && user.getUserName().equals(username)) {
                return user; // Found them!
            }
        }
        return null; // No such user found.
    }

    // Loads all user accounts from the JSON file.
    @SuppressWarnings("unchecked") // To keep the compiler happy about generic types with JSONSimple.
    private void loadUserAccounts() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(USERS_DATA_FILE)) {
            // Read the file and try to parse it as a JSON array.
            JSONArray usersJsonArray = (JSONArray) parser.parse(reader);
            this.users.clear(); // Clear current list before loading

            // Go through each JSON object in the array.
            for (Object userObj : usersJsonArray) {
                JSONObject userJson = (JSONObject) userObj;
                UserAccount user = new UserAccount();
                // We're re-using the registerUser method to populate the UserAccount object.
                // This is a bit of a shortcut; a dedicated "loadUser" method in UserAccount might be cleaner.
                user.registerUser(
                        (String) userJson.get("username"),
                        (String) userJson.get("password"),
                        (String) userJson.get("cellphone"),
                        (String) userJson.get("firstName"),
                        (String) userJson.get("lastName")
                );
                // Only add if it was successfully "re-registered" (i.e., data was valid).
                if (user.isRegistered()) {
                    this.users.add(user);
                }
            }
            System.out.println("User accounts loaded successfully from " + USERS_DATA_FILE);
        } catch (IOException e) {
            // This usually means the file doesn't exist yet (e.g., first run). That's okay.
            System.out.println("Could not read user accounts file: " + USERS_DATA_FILE + ". A new one will be created if users are registered.");
        } catch (ParseException e) {
            // This means the file is there, but it's not valid JSON. Uh oh.
            System.err.println("Error parsing user accounts file: " + USERS_DATA_FILE + ". File might be corrupted.");
            e.printStackTrace();
        }
    }

    // Saves the current list of user accounts to the JSON file.
    @SuppressWarnings("unchecked")
    private void saveUserAccounts() {
        JSONArray usersJsonArray = new JSONArray();
        // Go through each user and turn them into a JSON object.
        for (UserAccount user : this.users) {
            if (user.isRegistered()) { // Only save users who are actually registered.
                JSONObject userJson = new JSONObject();
                userJson.put("username", user.getUserName());
                userJson.put("password", user.getPassword()); // Note: Storing passwords in plain text is bad for real apps!
                userJson.put("cellphone", user.getCellPhoneNumber());
                userJson.put("firstName", user.getFirstName());
                userJson.put("lastName", user.getLastName());
                usersJsonArray.add(userJson);
            }
        }

        // Write the JSON array to the file.
        try (FileWriter fileWriter = new FileWriter(USERS_DATA_FILE)) {
            fileWriter.write(usersJsonArray.toJSONString());
            fileWriter.flush(); // Make sure it's all written out.
            System.out.println("User accounts saved successfully to " + USERS_DATA_FILE);
        } catch (IOException e) {
            // Something went wrong trying to write the file.
            System.err.println("Error saving user accounts to file: " + USERS_DATA_FILE);
            e.printStackTrace();
        }
    }
}
