package thltm_lab2.bai1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class StringClient {
    private static void send(DatagramSocket socket, InetAddress host, int port, String msg) throws IOException {
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
        socket.send(packet);
    }

    public static void main(String[] args) {
        int port = 7000;
        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader kb = new BufferedReader(new InputStreamReader(System.in))) {

            InetAddress host = InetAddress.getByName("localhost");
            while (true) {
                System.out.print("Nhap chuoi (nhap 'q' de thoat): ");
                String st = kb.readLine();
                if (st == null) {
                    break;
                }

                send(socket, host, port, st);
                if (st.equalsIgnoreCase("q")) {
                    break;
                }

                for (int i = 0; i < 3; i++) {
                    byte[] buffer = new byte[2048];
                    DatagramPacket resp = new DatagramPacket(buffer, buffer.length);
                    socket.receive(resp);
                    String line = new String(resp.getData(), 0, resp.getLength(), StandardCharsets.UTF_8);
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
