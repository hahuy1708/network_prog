package lab1.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class UDPHandler {
    private static final int BUFFER_SIZE = 4096;

    private UDPHandler() {
    }

    public static void startServer(int port, Consumer<String> logger) {
        Thread serverThread = new Thread(() -> runLoop(port, logger), "lab1-udp-server");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private static void runLoop(int port, Consumer<String> logger) {
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            logger.accept("UDP server started on port " + port);
            byte[] receiveBuffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                String input = readPacketLine(receivePacket);
                logger.accept("Received from " + receivePacket.getAddress().getHostAddress() + ":"
                        + receivePacket.getPort() + " -> " + input);

                byte[] responseBytes = buildResponseBytes(input);
                DatagramPacket sendPacket = new DatagramPacket(
                        responseBytes,
                        responseBytes.length,
                        receivePacket.getAddress(),
                        receivePacket.getPort());
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            logger.accept("Server error: " + e.getMessage());
        }
    }

    private static String readPacketLine(DatagramPacket packet) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(packet.getData(), 0, packet.getLength()),
                        StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            return line == null ? "" : line;
        }
    }

    private static byte[] buildResponseBytes(String input) {
        int wordCount = countWords(input);
        int vowelCount = countVowels(input);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        writer.println("Original: " + input);
        writer.println("Reverse: " + new StringBuilder(input).reverse());
        writer.println("Uppercase: " + input.toUpperCase());
        writer.println("Lowercase: " + input.toLowerCase());
        writer.println("Toggle case: " + toggleCase(input));
        writer.println("Word count: " + wordCount);
        writer.println("Vowel count: " + vowelCount);
        writer.flush();
        return out.toByteArray();
    }

    private static String toggleCase(String value) {
        StringBuilder result = new StringBuilder(value.length());
        for (char ch : value.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                result.append(Character.toLowerCase(ch));
            } else if (Character.isLowerCase(ch)) {
                result.append(Character.toUpperCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private static int countWords(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }

    private static int countVowels(String value) {
        int count = 0;
        for (char ch : value.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(ch) >= 0) {
                count++;
            }
        }
        return count;
    }
}
