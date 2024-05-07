import core.service.MyRemoteInterface;

import java.rmi.Naming;

// الكلاس الذي يحتوي على العميل
public class Client {

    public static void main(String[] args) {
        try {
            // الاتصال بالخادم عبر الريجستري
            MyRemoteInterface server = (MyRemoteInterface) Naming.lookup("//localhost/MyRemoteServer");

            // استدعاء الطريقة المعرفة في الواجهة وطباعة الرسالة
            System.out.println(server.sayHello());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}