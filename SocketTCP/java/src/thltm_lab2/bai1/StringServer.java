package thltm_lab2.bai1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class StringServer {
    private static int countWords(String s) {
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }

    private static void sendResponse(DatagramSocket socket, InetAddress address, int port, String msg) throws IOException {
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket resp = new DatagramPacket(data, data.length, address, port);
        socket.send(resp);
    }

    public static void main(String[] args) {
        int port = 7000;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("String UDP Server san sang tai cong " + port);

            byte[] buffer = new byte[2048];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String st = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                if (st.equalsIgnoreCase("exit")) {
                    continue;
                }

                sendResponse(socket, packet.getAddress(), packet.getPort(), "In Hoa: " + st.toUpperCase());
                sendResponse(socket, packet.getAddress(), packet.getPort(), "In thuong: " + st.toLowerCase());
                sendResponse(socket, packet.getAddress(), packet.getPort(), "So tu: " + countWords(st));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
