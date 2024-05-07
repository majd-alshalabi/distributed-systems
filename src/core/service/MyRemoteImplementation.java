package core.service;

import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRemoteImplementation extends UnicastRemoteObject implements MyRemoteInterface {

    public MyRemoteImplementation() throws RemoteException {
        super();
    }
    static Map<String , MyCallback> map = new HashMap<>();
    @Override
    public void registerUser(MyCallback callback,String clientId) throws RemoteException {
        map.put(clientId, callback);
    }

    @Override
    public List<String> getClientIds() throws RemoteException {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public byte[] getScreenshot(String clientId) throws RemoteException {
        return map.get(clientId).onScreenshot();
    }

    @Override
    public byte[] getCameraImage(String clientId) throws RemoteException {
        return map.get(clientId).onCameraImage();
    }
}
