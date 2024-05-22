package core.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ChatUtils{
    private  final int SERVER_PORT = 9876;
    private  Map<String, String> clientNames = new HashMap<>();
    public final String name;
    DatagramSocket socket = null;
    InetAddress serverAddress = null;
    public ChatUtils(String name) {
        this.name = name;
    }

    public void initial() {
        try {
            socket = new DatagramSocket();

            serverAddress = InetAddress.getByName("localhost");
            if(this.name == null) {
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter your name: ");
                String name = scanner.nextLine();
                registerWithName(socket, serverAddress, SERVER_PORT, name);
            }
            else {
                registerWithName(socket, serverAddress, SERVER_PORT, name);
            }

            // Start a thread to listen for incoming messages
            Thread listenerThread = getThread();
            listenerThread.start();

            // Main loop to send messages
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Thread getThread() {
        DatagramSocket finalSocket = socket;
        Thread listenerThread = new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    finalSocket.receive(receivePacket);
                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    if (receivedMessage.startsWith("ClientList:")) {
                        updateClientList(receivedMessage);
                    } else {
                        System.out.println("Message received: " + receivedMessage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return listenerThread;
    }

    public void sendMessage() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Connected clients: " + clientNames.values());
        System.out.print("Enter Receiver name: ");
        String receiverName = scanner.nextLine();
        while (true) {
            System.out.println("Enter 0 to Exit");
            System.out.print("Enter message: ");
            String message = scanner.nextLine();
            if(message.equals("0")){
                break;
            }
            message = receiverName + ":" + message;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);
        }
    }

    public void sendMessageToManager() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter 0 to Exit");
            System.out.print("Enter message: ");
            String message = scanner.nextLine();
            if(message.equals("0")){
                break;
            }
            message = "Manager" + ":" +message;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);
        }
    }

    private  void registerWithName(DatagramSocket socket, InetAddress serverAddress, int serverPort, String name) {
        try {
            String registrationMessage = "Register:" + name;
            byte[] sendData = registrationMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void updateClientList(String message) {
        String[] parts = message.substring("ClientList:".length()).split(",");
        clientNames.clear();
        for (String clientInfo : parts) {
            String[] infoParts = clientInfo.split("\\(");
            if (infoParts.length >= 2) {
                String name = infoParts[0].trim();
                String id = infoParts[1].replace(")", "").trim();
                clientNames.put(id, name);
            }
        }
    }
}
