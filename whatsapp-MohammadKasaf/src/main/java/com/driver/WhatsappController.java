package com.driver;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("whatsapp")
public class WhatsappController {

    @Autowired
    private WhatsappService whatsappService;

    @PostMapping("/add-user")
    public ResponseEntity<String> createUser(@RequestParam String name, @RequestParam String mobile) {
        try {
            String response = whatsappService.createUser(name, mobile);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add-group")
    public ResponseEntity<Group> createGroup(@RequestBody List<User> users) {
        Group group = whatsappService.createGroup(users);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping("/add-message")
    public ResponseEntity<Integer> createMessage(@RequestParam String content) {
        int messageId = whatsappService.createMessage(content);
        return new ResponseEntity<>(messageId, HttpStatus.OK);
    }

    @PutMapping("/send-message")
    public ResponseEntity<Integer> sendMessage(
            @RequestParam int messageId,
            @RequestParam String content,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date timestamp,
            @RequestBody User sender,
            @RequestBody Group group) {
        try {
            Message message = new Message(messageId, content, timestamp);
            int messageCount = whatsappService.sendMessage(message, sender, group);
            return new ResponseEntity<>(messageCount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/change-admin")
    public ResponseEntity<String> changeAdmin(
            @RequestBody User approver,
            @RequestBody User user,
            @RequestBody Group group) {
        try {
            String response = whatsappService.changeAdmin(approver, user, group);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/remove-user")
    public ResponseEntity<Integer> removeUser(@RequestBody User user) {
        try {
            int response = whatsappService.removeUser(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/find-messages")
    public ResponseEntity<String> findMessage(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date start,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date end,
            @RequestParam int K) {
        try {
            String response = whatsappService.findMessage(start, end, K);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
