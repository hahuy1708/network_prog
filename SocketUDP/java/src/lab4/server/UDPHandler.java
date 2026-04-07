package lab4.server;

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.function.Consumer;

public final class UDPHandler {
    private static final int RECEIVE_BUFFER_SIZE = 2048;
    private static final int SEND_BUFFER_SIZE = 65507;

    private UDPHandler() {
    }

    public static void startServer(int port, Consumer<String> logger) {
        Thread serverThread = new Thread(() -> runLoop(port, logger), "lab4-udp-server");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private static void runLoop(int port, Consumer<String> logger) {
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            logger.accept("UDP DB server started on port " + port);
            byte[] receiveBuffer = new byte[RECEIVE_BUFFER_SIZE];

            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(requestPacket);

                String command = readCommand(requestPacket);
                logger.accept("Request from " + requestPacket.getAddress().getHostAddress()
                        + ":" + requestPacket.getPort() + " -> " + command);

                byte[] response = handleCommand(command, logger);
                DatagramPacket responsePacket = new DatagramPacket(
                        response,
                        response.length,
                        requestPacket.getAddress(),
                        requestPacket.getPort());
                serverSocket.send(responsePacket);
            }
        } catch (IOException e) {
            logger.accept("Server error: " + e.getMessage());
        }
    }

    private static byte[] handleCommand(String command, Consumer<String> logger) {
        if (!"GET_ALL_STUDENTS".equalsIgnoreCase(command.trim())) {
            return encodeLine("ERROR|Unsupported command: " + command);
        }

        try {
            byte[] response = buildStudentResponse();
            if (response.length > SEND_BUFFER_SIZE) {
                return encodeLine("ERROR|Result too large for one UDP packet.");
            }
            return response;
        } catch (Exception e) {
            logger.accept("DB error: " + e.getMessage());
            return encodeLine("ERROR|" + safeErrorMessage(e.getMessage()));
        }
    }

    private static byte[] buildStudentResponse() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);

        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM sv")) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            writer.println("OK");
            writer.print("COLUMNS");
            for (int i = 1; i <= cols; i++) {
                writer.print("|");
                writer.print(escapeField(meta.getColumnName(i)));
            }
            writer.println();

            while (rs.next()) {
                writer.print("ROW");
                for (int i = 1; i <= cols; i++) {
                    writer.print("|");
                    writer.print(escapeField(rs.getString(i)));
                }
                writer.println();
            }
            writer.println("END");
            writer.flush();
            return out.toByteArray();
        }
    }

    private static String readCommand(DatagramPacket packet) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(packet.getData(), 0, packet.getLength()),
                        StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            return line == null ? "" : line;
        }
    }

    private static byte[] encodeLine(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        writer.println(text);
        writer.flush();
        return out.toByteArray();
    }

    private static String escapeField(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private static String safeErrorMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Unknown database error";
        }
        return value.replace('\n', ' ').replace('\r', ' ');
    }
}
