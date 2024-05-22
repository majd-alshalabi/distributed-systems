import core.service.MyRemoteImplementation;
import core.service.MyRemoteInterface;

import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int SERVER_PORT = 9876;
    private static final int BROADCAST_INTERVAL = 5000; // 5 seconds
    private static Map<String, InetAddress> clientAddresses = new HashMap<>();
    private static Map<String, Integer> clientPorts = new HashMap<>();
    private static Map<String, String> clientNames = new HashMap<>();
    static DatagramSocket udpSocket;
    public static void main(String[] args) {
        try {
            // إنشاء ريجستري RMI
            LocateRegistry.createRegistry(1099);

            // إنشاء الخادم وربطه بالريجستري
            MyRemoteInterface server = new MyRemoteImplementation();
            Naming.rebind("//localhost/MyRemoteServer", server);
            createSocket();
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void createSocket() throws IOException {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(SERVER_PORT);
            byte[] receiveData = new byte[1024];

            // Thread to broadcast client list periodically
            DatagramSocket finalSocket = socket;
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(BROADCAST_INTERVAL);
                        broadcastClientList(finalSocket);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress senderAddress = receivePacket.getAddress();
                int senderPort = receivePacket.getPort();

                // Register client if not already registered
                String clientId = senderAddress.toString() + ":" + senderPort;
                clientAddresses.put(clientId, senderAddress);
                clientPorts.put(clientId, senderPort);

                // Handle registration with a name
                if (receivedMessage.startsWith("Register:")) {
                    String name = receivedMessage.substring("Register:".length());
                    clientNames.put(clientId, name);
                }

                // Handle client list request
                if (receivedMessage.equals("GetClientList")) {
                    sendClientList(socket, senderAddress, senderPort);
                } else {
                    // Handle regular messages as before
                    System.out.println("Received message from client (" + clientId + "): " + receivedMessage);
                    String[] parts = receivedMessage.split(":", 2);
                    if (parts.length == 2) {
                        String clientName = parts[0];
                        String messageToSend = parts[1];
                        String recipientId = getKeyFromValue(clientNames,clientName);
                        // Forward the message to the recipient client
                        InetAddress recipientAddress = clientAddresses.get(recipientId);
                        Integer recipientPort = clientPorts.get(recipientId);

                        if (recipientAddress != null && recipientPort != null) {
                            byte[] sendData = messageToSend.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, recipientAddress, recipientPort);
                            socket.send(sendPacket);

                            // Display the forwarding action on the server
                            System.out.println("Forwarded message to client (" + recipientId + "): " + messageToSend);
                        } else {
                            System.out.println("Recipient client (" + recipientId + ") not found.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
    private static void broadcastClientList(DatagramSocket socket) {
        try {
            StringBuilder clientList = new StringBuilder("ClientList:");
            for (String clientId : clientAddresses.keySet()) {
                String name = clientNames.getOrDefault(clientId, "Unknown");
                clientList.append(name).append(" (").append(clientId).append("), ");
            }
            byte[] sendData = clientList.toString().getBytes();
            for (Map.Entry<String, InetAddress> entry : clientAddresses.entrySet()) {
                String clientId = entry.getKey();
                InetAddress address = entry.getValue();
                int port = clientPorts.get(clientId);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClientInfo {
        private InetAddress address;
        private int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Server.ClientInfo that = (Server.ClientInfo) o;

            if (port != that.port) return false;
            return address.equals(that.address);
        }

        @Override
        public int hashCode() {
            int result = address.hashCode();
            result = 31 * result + port;
            return result;
        }

        @Override
        public String toString() {
            return address + ":" + port;
        }
    }
    private static void sendClientList(DatagramSocket socket, InetAddress recipientAddress, int recipientPort) {
        try {
            StringBuilder clientList = new StringBuilder("ClientList:");
            for (String clientId : clientAddresses.keySet()) {
                String name = clientNames.getOrDefault(clientId, "Unknown");
                clientList.append(name).append(" (").append(clientId).append("), ");
            }
            byte[] sendData = clientList.toString().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, recipientAddress, recipientPort);
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static <K, V> K getKeyFromValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null; // If value not found in the map
    }

}
