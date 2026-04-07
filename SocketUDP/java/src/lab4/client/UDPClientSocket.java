package lab4.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UDPClientSocket {
    private static final int BUFFER_SIZE = 65507;

    private final InetAddress serverAddress;
    private final int serverPort;
    private final DatagramSocket socket;

    public UDPClientSocket(String host, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(host);
        this.serverPort = port;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(7000);
    }

    public synchronized DataResponse getStudentData() {
        try {
            byte[] request = encodeLine("GET_ALL_STUDENTS");
            DatagramPacket sendPacket = new DatagramPacket(request, request.length, serverAddress, serverPort);
            socket.send(sendPacket);

            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String payload = decodeText(receivePacket);
            return parsePayload(payload);
        } catch (SocketTimeoutException ex) {
            return DataResponse.error("Server timeout (7s).");
        } catch (Exception ex) {
            return DataResponse.error(ex.getMessage());
        }
    }

    public synchronized void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    private DataResponse parsePayload(String payload) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(payload));
        String firstLine = reader.readLine();
        if (firstLine == null) {
            return DataResponse.error("Empty response from server.");
        }

        if (firstLine.startsWith("ERROR|")) {
            return DataResponse.error(firstLine.substring("ERROR|".length()));
        }
        if (!"OK".equals(firstLine)) {
            return DataResponse.error("Unexpected response: " + firstLine);
        }

        String headerLine = reader.readLine();
        if (headerLine == null || !headerLine.startsWith("COLUMNS")) {
            return DataResponse.error("Missing columns metadata.");
        }

        List<String> headerTokens = splitEscaped(headerLine);
        String[] columns = headerTokens.size() <= 1
                ? new String[0]
                : headerTokens.subList(1, headerTokens.size()).toArray(new String[0]);

        List<String[]> rows = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if ("END".equals(line)) {
                break;
            }

            if (!line.startsWith("ROW")) {
                continue;
            }

            List<String> tokens = splitEscaped(line);
            String[] row = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                if (i + 1 < tokens.size()) {
                    row[i] = tokens.get(i + 1);
                } else {
                    row[i] = "";
                }
            }
            rows.add(row);
        }

        return DataResponse.success(columns, rows.toArray(new String[0][]));
    }

    private List<String> splitEscaped(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (escaping) {
                if (ch == 'n') {
                    current.append('\n');
                } else {
                    current.append(ch);
                }
                escaping = false;
                continue;
            }

            if (ch == '\\') {
                escaping = true;
                continue;
            }

            if (ch == '|') {
                result.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(ch);
        }
        result.add(current.toString());
        return result;
    }

    private byte[] encodeLine(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        writer.println(text);
        writer.flush();
        return out.toByteArray();
    }

    private String decodeText(DatagramPacket packet) throws IOException {
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

    public static final class DataResponse {
        private final String[] columns;
        private final String[][] rows;
        private final String errorMessage;

        private DataResponse(String[] columns, String[][] rows, String errorMessage) {
            this.columns = columns;
            this.rows = rows;
            this.errorMessage = errorMessage;
        }

        public static DataResponse success(String[] columns, String[][] rows) {
            return new DataResponse(columns, rows, null);
        }

        public static DataResponse error(String errorMessage) {
            return new DataResponse(new String[0], new String[0][0], errorMessage);
        }

        public boolean hasError() {
            return errorMessage != null;
        }

        public String[] getColumns() {
            return columns;
        }

        public String[][] getRows() {
            return rows;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
