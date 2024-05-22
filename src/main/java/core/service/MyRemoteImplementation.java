package core.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRemoteImplementation extends UnicastRemoteObject implements MyRemoteInterface {

    private final Map<String, MediaCallback> map = new HashMap<>();
    private final Map<String, List<MessageModel>> chats = new HashMap<>();

    public MyRemoteImplementation() throws RemoteException {
        super();
    }

    @Override
    public void registerUser(MediaCallback callback, String clientId) throws RemoteException {
        synchronized (this) {
            map.put(clientId, callback);
            chats.put(clientId, new ArrayList<>());
        }
    }

    @Override
    public List<String> getClientIds() throws RemoteException {
        synchronized (this) {
            return new ArrayList<>(map.keySet());
        }
    }

    @Override
    public byte[] getScreenshot(String clientId) throws RemoteException {
        synchronized (this) {
            MediaCallback callback = map.get(clientId);
            return (callback != null) ? callback.onScreenshot() : null;
        }
    }

    @Override
    public byte[] getCameraImage(String clientId) throws RemoteException {
        synchronized (this) {
            MediaCallback callback = map.get(clientId);
            return (callback != null) ? callback.onCameraImage() : null;
        }
    }

    @Override
    public List<MessageModel> getMessage(String clientId) throws RemoteException {
        synchronized (this) {
            List<MessageModel> messages = chats.get(clientId);
            return (messages != null) ? messages : new ArrayList<>();
        }
    }

    @Override
    public void sendMessage(String clientId, MessageModel message) throws RemoteException {
        synchronized (this) {
            List<MessageModel> messages = chats.get(clientId);
            if (messages != null) {
                messages.add(message);
            }
        }
    }
}

