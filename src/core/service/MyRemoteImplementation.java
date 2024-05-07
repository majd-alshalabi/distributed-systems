package core.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MyRemoteImplementation extends UnicastRemoteObject implements MyRemoteInterface {

    public MyRemoteImplementation() throws RemoteException {
        super();
    }

    // تنفيذ الطريقة المعرفة في الواجهة
    @Override
    public String sayHello() throws RemoteException {
        return "Hello, this is a simple RMI example!";
    }
}
