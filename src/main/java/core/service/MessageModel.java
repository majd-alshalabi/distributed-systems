package core.service;

import com.google.gson.Gson;

import java.io.*;
import java.net.DatagramPacket;

public class MessageModel implements Serializable {
    public  final String name ;
    public  final String message ;
    public final String senderId;
    public  final boolean registertionMessage;
    public MessageModel(String name, String message, String senderId, boolean registertionMessage) {
        this.name = name;
        this.message = message;
        this.senderId = senderId;
        this.registertionMessage = registertionMessage;
    }
    public static MessageModel deserialize(String data) throws IOException, ClassNotFoundException {
        Gson gson = new Gson();
        return gson.fromJson(data,MessageModel.class);
    }

    public static String serialize(MessageModel message) throws IOException {
        Gson gson = new Gson();
        return gson.toJson(message);
    }
}
