package thltm_lab2.bai2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class CalculatorClient {
    private static void send(DatagramSocket socket, InetAddress host, int port, String msg) throws IOException {
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
        socket.send(packet);
    }

    public static void main(String[] args) {
        int port = 1234;
        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader kb = new BufferedReader(new InputStreamReader(System.in))) {

            InetAddress host = InetAddress.getByName("localhost");
            while (true) {
                System.out.print("Nhap phep tinh (VD: 5+13-(12-4*6), go 'q' de thoat): ");
                String msg = kb.readLine();
                if (msg == null) {
                    break;
                }

                send(socket, host, port, msg);
                if (msg.equalsIgnoreCase("q")) {
                    break;
                }

                byte[] buffer = new byte[2048];
                DatagramPacket resp = new DatagramPacket(buffer, buffer.length);
                socket.receive(resp);
                String response = new String(resp.getData(), 0, resp.getLength(), StandardCharsets.UTF_8);
                System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
