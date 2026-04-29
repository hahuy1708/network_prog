package thltm_lab1.bai3;

import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Da ket noi phong chat! (Go 'q' de thoat)");

            // Luong phu: Luon lang nghe tin nhan tu Server gui ve
            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        System.out.println("\n" + fromServer);
                        System.out.print("Toi: ");
                    }
                } catch (IOException e) { System.out.println("Mat ket noi Server."); }
            }).start();

            // Luong chinh: Đoc du lieu tu ban phim va gui di
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (true) {
                System.out.print("Toi: ");
                line = kb.readLine();
                if (line == null || line.equalsIgnoreCase("q")) break;
                out.println(line);
            }
            socket.close(); // Dong socket khi ket thuc [4]
        } catch (IOException e) { e.printStackTrace(); }
    }
}