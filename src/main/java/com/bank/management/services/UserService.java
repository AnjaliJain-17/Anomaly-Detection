package com.bank.management.services;

import com.bank.management.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final ObjectMapper mapper;

    public UserService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<User> getAllUsers() {
        log.info("Fetching ALL user details...");
        JSONArray userDetails = new JSONArray();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("user.json"));
            JSONObject jsonObject = (JSONObject) obj;
            userDetails = (JSONArray) jsonObject.get("data");

        } catch (IOException | ParseException e) {
            log.error("Error occurred in reading JSON file... => {}", e.getMessage());
            e.printStackTrace();
        }
        return userDetails;
    }

    public Optional<User> getUserById(Long id) {
        log.info("Fetching user details for user id...");
        try {
            File file = new File("user.json");
            JsonNode rootNode = mapper.readTree(file);

            JsonNode dataArray = rootNode.path("data");
            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    if (node.get("id").asLong() == id) {
                        User user = mapper.treeToValue(node, User.class);
                        log.info("User found: {}", user);
                        return Optional.of(user);
                    }
                }
            }
            log.info("User not found with id {}", id);
            return Optional.empty();
        } catch (IOException e) {
            log.error("Error occurred in fetching user details for user id... => {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public User createUser(User user) {
        log.info("Inside create user...");
        try {
            // Read the existing JSON data from the file into a JsonNode tree model
            JsonNode rootNode = mapper.readTree(new File("user.json"));

            // Get the "data" array node from the tree
            ArrayNode dataArray = (ArrayNode) rootNode.get("data");

            // Convert the new user object to a JsonNode object
            JsonNode userNode = mapper.valueToTree(user);

            // Append the new user object to the "data" array
            dataArray.add(userNode);

            // Write the updated tree back to the file
            mapper.writeValue(new File("user.json"), rootNode);

        } catch (Exception e) {
            log.error("Error occurred while creating user... => {}", e.getMessage());
            e.printStackTrace();
        }
        return user;
    }


    public User updateUser(Long id, User user) {
        log.info("Inside update user...");
        try {
            // Read user.json file
            JSONParser parser = new JSONParser();
            File file = new File("user.json");
            Object obj = parser.parse(new FileReader(file));

            // Get the user object with the given ID and update it
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            for (Object o : jsonArray) {
                JSONObject userObj = (JSONObject) o;
                if ((long) userObj.get("id") == id) {
                    userObj.put("firstName", user.getFirstName());
                    userObj.put("lastName", user.getLastName());
                    userObj.put("email", user.getEmail());
                    userObj.put("password", user.getPassword());
                    break;
                }
            }

            // Write the updated user.json file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();
            log.info("User object with ID " + id + " has been updated successfully.");
        } catch (Exception e) {
            log.error("Error occurred while update user... => {}", e.getMessage());
            e.printStackTrace();
        }
        return user;
    }


    public Object deleteUser(Long id)  {
        log.info("Inside delete user...");
        try {
            // Read the contents of the user.json file
            FileReader fileReader = new FileReader("user.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            // Remove the user object with the specified ID
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject userObject = (JSONObject) jsonArray.get(i);
                int userId = ((Long) userObject.get("id")).intValue();
                if (userId == id) {
                    jsonArray.remove(i);
                    break;
                }
            }

            // Write the updated JSON data to the user.json file
            FileWriter fileWriter = new FileWriter("user.json");
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();

            log.info("User with ID " + id + " deleted successfully.");
            return "user deleted successfully!";
        } catch (Exception e) {
            log.error("Error occurred while delete user... => {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
