package success;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// This class manages all user account operations.
public class AccountManager {

    private ArrayList<User> users = new ArrayList<>();
    private static final String USER_FILE = "users.json";

    // Constructor loads all users from the JSON file when the app starts.
    public AccountManager() {
        loadUsers();
    }
    
    public ArrayList<User> getUsers() {
        return users;
    }

    // Part 1: Check if username is valid (contains '_' and <= 5 chars).
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    // Part 1: Check if password meets complexity rules.
    public boolean checkPasswordComplexity(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()].*");
    }

    // Part 1: Register a new user.
    public boolean registerUser(User newUser) {
        // Check if user already exists
        for (User existingUser : users) {
            if (existingUser.getUsername().equalsIgnoreCase(newUser.getUsername())) {
                return false; // User already exists
            }
        }
        users.add(newUser);
        saveUsers(); // Save the updated list to the file.
        return true;
    }

    // Part 1: Log a user in.
    public User loginUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user; // Return the user object on successful login.
            }
        }
        return null; // Return null if login fails.
    }
    
    // --- Part 3: Report and Management Methods ---

    // Finds a user by their username (case-insensitive).
    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }
    
    // Generates a report of all users.
    public String displayAllUsers() {
        StringBuilder report = new StringBuilder("--- All Registered Users ---\n");
        if (users.isEmpty()) {
            report.append("No users registered yet.");
        } else {
            for (User user : users) {
                report.append("Name: ").append(user.getFirstName())
                      .append(" ").append(user.getLastName())
                      .append(" (Username: ").append(user.getUsername()).append(")\n");
            }
        }
        return report.toString();
    }

    // Deletes a user by their username.
    public boolean deleteUser(String username) {
        User userToDelete = findUser(username);
        if (userToDelete != null) {
            users.remove(userToDelete);
            saveUsers(); // Save the change.
            return true;
        }
        return false;
    }

    // --- JSON File Handling ---

    // Saves the list of users to users.json.
    private void saveUsers() {
        JSONArray userList = new JSONArray();
        for (User user : users) {
            JSONObject userDetails = new JSONObject();
            userDetails.put("username", user.getUsername());
            userDetails.put("password", user.getPassword());
            userDetails.put("firstName", user.getFirstName());
            userDetails.put("lastName", user.getLastName());
            userList.add(userDetails);
        }

        try (FileWriter file = new FileWriter(USER_FILE)) {
            file.write(userList.toJSONString());
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }
    
    // Loads users from users.json.
    private void loadUsers() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(USER_FILE)) {
            JSONArray userList = (JSONArray) parser.parse(reader);
            users.clear();
            for (Object obj : userList) {
                JSONObject userJson = (JSONObject) obj;
                users.add(new User(
                    (String) userJson.get("username"),
                    (String) userJson.get("password"),
                    (String) userJson.get("firstName"),
                    (String) userJson.get("lastName")
                ));
            }
        } catch (IOException | ParseException e) {
            // File might not exist on first run, that's okay.
            users = new ArrayList<>();
        }
    }
}
