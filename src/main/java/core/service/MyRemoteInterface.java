package core.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// تعريف الواجهة RMI
public interface MyRemoteInterface extends Remote {
    // طريقة بسيطة لإرجاع رسالة نصية
    void registerUser(MediaCallback callback, String clientId) throws RemoteException;
    List<String> getClientIds() throws RemoteException;
    byte[] getScreenshot(String clientId) throws RemoteException;
    byte[] getCameraImage(String clientId) throws RemoteException;
    List<MessageModel> getMessage(String clientId) throws RemoteException;
    void sendMessage(String clientId,MessageModel message) throws RemoteException;
}

