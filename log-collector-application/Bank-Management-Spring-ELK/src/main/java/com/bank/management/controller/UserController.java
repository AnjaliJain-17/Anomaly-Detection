package com.bank.management.controller;

import com.bank.management.domain.User;
import com.bank.management.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<User> getAllUsers() {
        log.info("Inside getAllUsers Controller");
        return userService.getAllUsers();
    }

    @GetMapping("/error")
    public List<User> getAllUsersErrorResponse() {
        log.info("Inside getAllUsers Controller");
        return userService.getAllUsersErrorResponse();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        log.info("Inside getUserById Controller");
        return userService.getUserById(id);
    }

    @PostMapping("")
    public User createUser(@RequestBody User user) {
        log.info("Inside createUser Controller");
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("Inside updateUser Controller");
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public Object deleteUser(@PathVariable Long id) {
        log.info("Inside deleteUser Controller");
        return userService.deleteUser(id);
    }
}

