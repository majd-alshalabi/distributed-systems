import core.service.MyCallback;
import core.service.MyRemoteInterface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.util.Scanner;
import java.util.UUID;

public class Client {
    static  String clientID = UUID.randomUUID().toString();

    public static void main(String[] args) {
        try {
            MyRemoteInterface server = (MyRemoteInterface) Naming.lookup("//localhost/MyRemoteServer");
            server.registerUser(new MyCallback() {
                @Override
                public byte[] onCameraImage() {
                    return getCameraImage();
                }
                @Override
                public byte[] onScreenshot() {
                    return getScreenshot();
                }
            }, clientID);
            while (true){
                showOption();
                Scanner scanner = new Scanner(System.in);
                int choice = scanner.nextInt();
                if(choice == 0){
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void showOption(){
        System.out.println("0- end connection with server\n");
    }
    static private byte[] getScreenshot(){
        try {
            Thread.sleep(120);
            Robot r = new Robot();
            Rectangle capture =
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage Image = r.createScreenCapture(capture);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(Image, "jpg", baos);
            return baos.toByteArray();
        }
        catch (AWTException | InterruptedException ex) {
            System.out.println(ex);
            return  null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static  private byte[] getCameraImage(){
        System.out.println(clientID + ": onCameraImage");
        return null;
    }
}