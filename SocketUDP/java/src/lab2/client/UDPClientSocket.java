package lab2.client;

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
    private static final int BUFFER_SIZE = 2048;

    private final InetAddress serverAddress;
    private final int serverPort;
    private final DatagramSocket socket;

    public UDPClientSocket(String host, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(host);
        this.serverPort = port;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(5000);
    }

    public synchronized String sendExpression(String expression) {
        try {
            byte[] request = encodeLine(expression);
            DatagramPacket sendPacket = new DatagramPacket(request, request.length, serverAddress, serverPort);
            socket.send(sendPacket);

            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            return readSingleLine(receivePacket);
        } catch (SocketTimeoutException ex) {
            return "Error: server timeout (5s).";
        } catch (IOException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public synchronized void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    private byte[] encodeLine(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        writer.println(text);
        writer.flush();
        return out.toByteArray();
    }

    private String readSingleLine(DatagramPacket packet) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(packet.getData(), 0, packet.getLength()),
                        StandardCharsets.UTF_8));
        String line = reader.readLine();
        return line == null ? "" : line;
    }
}
