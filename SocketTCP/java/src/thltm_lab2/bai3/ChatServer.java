package thltm_lab2.bai3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final int PORT = 5000;
    private static final Map<SocketAddress, String> clients = new ConcurrentHashMap<>();

    private static String safeName(String name) {
        if (name == null) {
            return "Khach";
        }
        String trimmed = name.trim();
        return trimmed.isEmpty() ? "Khach" : trimmed;
    }

    private static void sendTo(DatagramSocket socket, String msg, SocketAddress address) throws IOException {
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length);
        packet.setSocketAddress(address);
        socket.send(packet);
    }

    private static void sendToAll(DatagramSocket socket, String msg) throws IOException {
        for (SocketAddress address : clients.keySet()) {
            sendTo(socket, msg, address);
        }
    }

    private static void sendToAllExcept(DatagramSocket socket, String msg, SocketAddress except) throws IOException {
        for (SocketAddress address : clients.keySet()) {
            if (!address.equals(except)) {
                sendTo(socket, msg, address);
            }
        }
    }

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("Chat Room UDP san sang tai cong " + PORT);

            Thread serverInputThread = new Thread(() -> {
                try (BufferedReader kb = new BufferedReader(new InputStreamReader(System.in))) {
                    String line;
                    while ((line = kb.readLine()) != null) {
                        if (line.equalsIgnoreCase("exit")) {
                            break;
                        }
                        sendToAll(socket, "Server: " + line);
                    }
                } catch (IOException e) {
                }
            });
            serverInputThread.setDaemon(true);
            serverInputThread.start();

            byte[] buffer = new byte[4096];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                SocketAddress clientAddr = packet.getSocketAddress();

                if (msg.startsWith("JOIN|")) {
                    String name = safeName(msg.substring(5));
                    clients.put(clientAddr, name);
                    sendToAll(socket, "Server: " + name + " da tham gia phong");
                    continue;
                }

                if (msg.equalsIgnoreCase("EXIT")) {
                    String name = clients.remove(clientAddr);
                    if (name == null) {
                        name = "Khach";
                    }
                    sendToAll(socket, "Server: " + name + " da roi phong");
                    continue;
                }

                String name = clients.getOrDefault(clientAddr, "Khach");
                System.out.println("Nhan: " + name + ": " + msg);
                sendToAllExcept(socket, name + ": " + msg, clientAddr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
