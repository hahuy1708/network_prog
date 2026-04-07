package lab1.client;

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
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UDPClientSocket {
    private static final int BUFFER_SIZE = 8192;

    private final InetAddress serverAddress;
    private final int serverPort;
    private final DatagramSocket socket;

    public UDPClientSocket(String host, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(host);
        this.serverPort = port;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(5000);
    }

    public synchronized String sendAndReceive(String message) {
        try {
            byte[] sendData = encodeOneLine(message);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);

            byte[] receiveData = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            return decodeMultiline(receivePacket);
        } catch (SocketTimeoutException e) {
            return "Error: server timeout (5s).";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    public synchronized void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    private byte[] encodeOneLine(String message) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        writer.println(message);
        writer.flush();
        return out.toByteArray();
    }

    private String decodeMultiline(DatagramPacket packet) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(packet.getData(), 0, packet.getLength()),
                        StandardCharsets.UTF_8));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (response.length() > 0) {
                response.append('\n');
            }
            response.append(line);
        }
        return response.toString();
    }
}
