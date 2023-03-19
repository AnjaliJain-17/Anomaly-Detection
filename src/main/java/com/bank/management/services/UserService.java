package com.bank.management.services;

import com.bank.management.domain.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

//    @Autowired
//    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);


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

//    public User getUserById(Long id) throws Exception {
//        Optional<User> optionalUser = userRepository.findById(id);
//        if (optionalUser.isPresent()) {
//            return optionalUser.get();
//        } else {
//            throw new Exception("User not found with id " + id);
//        }
//    }

//    public User createUser(User user) {
//        return userRepository.save(user);
//    }
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
