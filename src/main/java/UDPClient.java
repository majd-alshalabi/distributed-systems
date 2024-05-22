import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UDPClient {
    private static final int SERVER_PORT = 9876;
    private static Map<String, String> clientNames = new HashMap<>();

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            registerWithName(socket, serverAddress, SERVER_PORT, name);

            // Start a thread to listen for incoming messages
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
            listenerThread.start();

            // Main loop to send messages
            while (true) {
                System.out.println("Connected clients: " + clientNames.values());
                System.out.print("Enter recipientId:message: ");
                String message = scanner.nextLine();
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private static void registerWithName(DatagramSocket socket, InetAddress serverAddress, int serverPort, String name) {
        try {
            String registrationMessage = "Register:" + name;
            byte[] sendData = registrationMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateClientList(String message) {
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
