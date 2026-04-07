package lab3.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class UDPHandler {
    private static final int BUFFER_SIZE = 4096;

    private final Consumer<String> logger;
    private final Set<SocketAddress> clients = ConcurrentHashMap.newKeySet();
    private final Map<SocketAddress, String> clientNames = new ConcurrentHashMap<>();

    private volatile boolean running;
    private DatagramSocket serverSocket;

    public UDPHandler(Consumer<String> logger) {
        this.logger = logger;
    }

    public void start(int port) {
        Thread serverThread = new Thread(() -> runLoop(port), "lab3-udp-server");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void broadcastFromServer(String message) {
        broadcast(message, null);
    }

    private void runLoop(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            this.serverSocket = socket;
            this.running = true;
            logger.accept("UDP chat server started on port " + port);

            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            while (running) {
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);

                SocketAddress clientAddress = packet.getSocketAddress();
                String payload = readPacketLine(packet);
                handlePayload(clientAddress, payload);
            }
        } catch (SocketException ignored) {
        } catch (IOException e) {
            logger.accept("Server error: " + e.getMessage());
        } finally {
            running = false;
        }
    }

    private void handlePayload(SocketAddress clientAddress, String payload) {
        if (payload.startsWith("JOIN|")) {
            String user = payload.substring(5).trim();
            if (user.isEmpty()) {
                user = defaultClientName(clientAddress);
            }

            clients.add(clientAddress);
            clientNames.put(clientAddress, user);
            logger.accept(user + " joined from " + clientAddress);
            broadcast("[System] " + user + " joined the room.", clientAddress);
            return;
        }

        clients.add(clientAddress);
        String senderName = clientNames.computeIfAbsent(clientAddress, this::defaultClientName);

        String content = payload;
        if (payload.startsWith("MSG|")) {
            content = payload.substring(4);
        }
        if (content.trim().isEmpty()) {
            return;
        }

        String fullMessage = senderName + ": " + content;
        logger.accept(fullMessage);
        broadcast(fullMessage, clientAddress);
    }

    private void broadcast(String message, SocketAddress excludeAddress) {
        byte[] data = encodeLine(message);
        for (SocketAddress clientAddress : clients) {
            if (excludeAddress != null && excludeAddress.equals(clientAddress)) {
                continue;
            }

            if (!(clientAddress instanceof InetSocketAddress) || serverSocket == null) {
                continue;
            }

            InetSocketAddress target = (InetSocketAddress) clientAddress;
            DatagramPacket packet = new DatagramPacket(data, data.length, target.getAddress(), target.getPort());
            try {
                serverSocket.send(packet);
            } catch (IOException e) {
                logger.accept("Send error to " + clientAddress + ": " + e.getMessage());
            }
        }
    }

    private String defaultClientName(SocketAddress address) {
        return "User@" + address;
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

    private byte[] encodeLine(String message) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        writer.println(message);
        writer.flush();
        return out.toByteArray();
    }
}
