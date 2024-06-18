package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<>();
        this.groupUserMap = new HashMap<>();
        this.senderMap = new HashMap<>();
        this.adminMap = new HashMap<>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        Group group;
        if (users.size() == 2) {
            group = new Group(users.get(1).getName(), users.size());
        } else {
            customGroupCount++;
            group = new Group("Group " + customGroupCount, users.size());
        }
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }

    public int createMessage(String content){
        messageId++;
        Date timestamp = new Date(); // Current date and time
        Message message = new Message(messageId, content, timestamp);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if (!groupUserMap.containsKey(group)) {
            throw new Exception("Group does not exist");
        }
        if (!groupUserMap.get(group).contains(sender)) {
            throw new Exception("You are not allowed to send message");
        }
        groupMessageMap.putIfAbsent(group, new ArrayList<>());
        groupMessageMap.get(group).add(message);
        senderMap.put(message, sender);
        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if (!groupUserMap.containsKey(group)) {
            throw new Exception("Group does not exist");
        }
        if (!adminMap.get(group).equals(approver)) {
            throw new Exception("Approver does not have rights");
        }
        if (!groupUserMap.get(group).contains(user)) {
            throw new Exception("User is not a participant");
        }
        adminMap.put(group, user);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        for (Group group : groupUserMap.keySet()) {
            List<User> users = groupUserMap.get(group);
            if (users.contains(user)) {
                if (adminMap.get(group).equals(user)) {
                    throw new Exception("Cannot remove admin");
                }
                users.remove(user);
                groupUserMap.put(group, users);

                groupMessageMap.get(group).removeIf(message -> senderMap.get(message).equals(user));
                senderMap.values().removeIf(sender -> sender.equals(user));

                return users.size();
            }
        }
        throw new Exception("User not found");
    }

    public String findMessage(Date start, Date end, int K) throws Exception {
        List<Message> messages = new ArrayList<>();
        for (List<Message> messageList : groupMessageMap.values()) {
            for (Message message : messageList) {
                if (message.getTimestamp().after(start) && message.getTimestamp().before(end)) {
                    messages.add(message);
                }
            }
        }
        if (messages.size() < K) {
            throw new Exception("K is greater than the number of messages");
        }
        messages.sort(Comparator.comparing(Message::getTimestamp).reversed());
        return messages.get(K - 1).getContent();
    }
}
