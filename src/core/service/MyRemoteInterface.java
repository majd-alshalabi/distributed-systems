package core.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// تعريف الواجهة RMI
public interface MyRemoteInterface extends Remote {
    // طريقة بسيطة لإرجاع رسالة نصية
    String sayHello() throws RemoteException;
}

