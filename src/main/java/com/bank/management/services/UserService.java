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
            log.error("Error occurred in reading JSON file");
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
            log.error("Error occurred in fetching user details for user id");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public User createUser(User user) {
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
            log.error("Error occurred while creating user");
            e.printStackTrace();
        }
        return user;
    }

//
//    public User updateUser(Long id, User user) throws Exception {
//        Optional<User> optionalUser = userRepository.findById(id);
//        if (optionalUser.isPresent()) {
//            user.setId(id);
//            return userRepository.save(user);
//        } else {
//            throw new Exception("User not found with id " + id);
//        }
//    }
//
//    public void deleteUser(Long id) throws Exception {
//        Optional<User> optionalUser = userRepository.findById(id);
//        if (optionalUser.isPresent()) {
//            userRepository.deleteById(id);
//        } else {
//            throw new Exception("User not found with id " + id);
//        }
//    }
}
