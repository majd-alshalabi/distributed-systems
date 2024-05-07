package core.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.function.Function;

// تعريف الواجهة RMI
public interface MyRemoteInterface extends Remote {
    // طريقة بسيطة لإرجاع رسالة نصية
    void registerUser(MyCallback callback,String clientId) throws RemoteException;
    List<String> getClientIds() throws RemoteException;
    byte[] getScreenshot(String clientId) throws RemoteException;
    byte[] getCameraImage(String clientId) throws RemoteException;
}

