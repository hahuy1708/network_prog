package thltm_lab2.bai3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    private static void send(DatagramSocket socket, InetAddress host, int port, String msg) throws IOException {
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
        socket.send(packet);
    }

    public static void main(String[] args) {
        int port = 5000;
        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader kb = new BufferedReader(new InputStreamReader(System.in))) {

            InetAddress host = InetAddress.getByName("localhost");
            System.out.print("Nhap ten: ");
            String name = kb.readLine();
            if (name == null || name.trim().isEmpty()) {
                name = "Khach";
            }

            send(socket, host, port, "JOIN|" + name.trim());
            System.out.println("Da ket noi phong chat UDP! (Go 'q' de thoat)");

            Thread receiver = new Thread(() -> {
                byte[] buffer = new byte[4096];
                while (true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                        System.out.println("\n" + msg);
                        System.out.print("Toi: ");
                    } catch (IOException e) {
                        break;
                    }
                }
            });
            receiver.setDaemon(true);
            receiver.start();

            String line;
            while (true) {
                System.out.print("Toi: ");
                line = kb.readLine();
                if (line == null || line.equalsIgnoreCase("q")) {
                    send(socket, host, port, "EXIT");
                    break;
                }
                send(socket, host, port, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
