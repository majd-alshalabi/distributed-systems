import core.service.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

public class Client {
    static MyRemoteInterface server ;
    static ChatUtils chatUtils;
    public static void main(String[] args) {
        try {
            server = (MyRemoteInterface) Naming.lookup("//localhost/MyRemoteServer");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
             chatUtils = new ChatUtils(name);
            chatUtils.initial();
            RegisterOnServer(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void RegisterOnServer(String name) throws IOException {
        server.registerUser(new MediaCallback() {
            @Override
            public byte[] onCameraImage() {return getCameraImage();}
            @Override
            public byte[] onScreenshot() {return getScreenshot();}
        },name);
        while (true){
            showOption();
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            if(choice == 0){
                break;
            }else if(choice == 1){
                openChat();
            }
        }
    }
    private static void showOption(){
        System.out.println("1- chat with manager\n" +
                "0- end connection with server\n");
    }

    private static void openChat() throws IOException {
       chatUtils.sendMessageToManager();
    }



    static private byte[] getScreenshot(){
        try {
            Thread.sleep(120);
            Robot r = new Robot();
            Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
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


    public static byte[] getCameraImage() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = null;
        try {
            camera = new VideoCapture(0);
            if (!camera.isOpened()) {
                System.out.println("Error: Camera not opened!");
                return null;
            }
            Mat frame = new Mat();
            if (camera.read(frame)) {
                return matToByteArray(frame);
            } else {
                System.out.println("Error: Failed to capture image!");
                return null;
            }
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
    }
    private static byte[] matToByteArray(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        return matOfByte.toArray();
    }
}
