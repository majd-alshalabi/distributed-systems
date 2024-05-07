import core.service.MyRemoteImplementation;
import core.service.MyRemoteInterface;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            // إنشاء ريجستري RMI
            LocateRegistry.createRegistry(1099);

            // إنشاء الخادم وربطه بالريجستري
            MyRemoteInterface server = new MyRemoteImplementation();
            Naming.rebind("//localhost/MyRemoteServer", server);

            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}