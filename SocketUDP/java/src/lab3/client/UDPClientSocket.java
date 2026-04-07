package lab3.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class UDPClientSocket {
    private static final int BUFFER_SIZE = 4096;

    private final String host;
    private final int port;
    private final String username;
    private final Consumer<String> messageConsumer;

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private volatile boolean running;

    public UDPClientSocket(String host, int port, String username, Consumer<String> messageConsumer) {
        this.host = host;
        this.port = port;
        this.username = (username == null || username.trim().isEmpty()) ? "Anonymous" : username.trim();
        this.messageConsumer = messageConsumer;
    }

    public boolean connect() {
        try {
            this.serverAddress = InetAddress.getByName(host);
            this.socket = new DatagramSocket();
            this.running = true;

            sendRaw("JOIN|" + username);
            startReceiverThread();
            return true;
        } catch (IOException e) {
            close();
            return false;
        }
    }

    public void sendMessage(String message) {
        if (!running || message == null || message.trim().isEmpty()) {
            return;
        }
        try {
            sendRaw("MSG|" + message);
        } catch (IOException e) {
            messageConsumer.accept("[System] Send failed: " + e.getMessage());
        }
    }

    public void close() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private void startReceiverThread() {
        Thread receiverThread = new Thread(() -> {
            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(packet);
                    String message = readPacketLine(packet);
                    if (!message.isEmpty()) {
                        messageConsumer.accept(message);
                    }
                } catch (IOException e) {
                    if (running) {
                        messageConsumer.accept("[System] Connection lost: " + e.getMessage());
                    }
                    running = false;
                }
            }
        }, "lab3-udp-client-listener");

        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    private void sendRaw(String payload) throws IOException {
        byte[] data = encodeLine(payload);
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, port);
        socket.send(packet);
    }

    private byte[] encodeLine(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        writer.println(text);
        writer.flush();
        return out.toByteArray();
    }

    private String readPacketLine(DatagramPacket packet) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(packet.getData(), 0, packet.getLength()),
                        StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            return line == null ? "" : line;
        }
    }
}
