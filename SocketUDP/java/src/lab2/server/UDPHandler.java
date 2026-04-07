package lab2.server;

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
    private static final int BUFFER_SIZE = 2048;

    private UDPHandler() {
    }

    public static void startServer(int port, Consumer<String> logger) {
        Thread serverThread = new Thread(() -> runLoop(port, logger), "lab2-udp-server");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private static void runLoop(int port, Consumer<String> logger) {
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            logger.accept("UDP calculator server started on port " + port);
            byte[] receiveBuffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);
                String expression = readPacketLine(receivePacket);

                logger.accept("Expression from " + receivePacket.getAddress().getHostAddress() + ":"
                        + receivePacket.getPort() + " -> " + expression);

                String resultText;
                try {
                    double result = evaluate(expression);
                    resultText = "Result: " + result;
                } catch (RuntimeException ex) {
                    resultText = "Error: invalid expression.";
                }

                byte[] responseBytes = encodeText(resultText);
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

    private static byte[] encodeText(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        writer.println(text);
        writer.flush();
        return out.toByteArray();
    }

    private static double evaluate(String expression) {
        Parser parser = new Parser(expression);
        double value = parser.parseExpression();
        parser.skipSpaces();
        if (!parser.isEnd()) {
            throw new RuntimeException("Unexpected token");
        }
        return value;
    }

    private static final class Parser {
        private final String expression;
        private int pos;

        private Parser(String expression) {
            this.expression = expression == null ? "" : expression;
            this.pos = 0;
        }

        private double parseExpression() {
            double value = parseTerm();
            while (true) {
                skipSpaces();
                if (match('+')) {
                    value += parseTerm();
                } else if (match('-')) {
                    value -= parseTerm();
                } else {
                    return value;
                }
            }
        }

        private double parseTerm() {
            double value = parseFactor();
            while (true) {
                skipSpaces();
                if (match('*')) {
                    value *= parseFactor();
                } else if (match('/')) {
                    value /= parseFactor();
                } else {
                    return value;
                }
            }
        }

        private double parseFactor() {
            skipSpaces();
            if (match('+')) {
                return parseFactor();
            }
            if (match('-')) {
                return -parseFactor();
            }
            if (match('(')) {
                double value = parseExpression();
                if (!match(')')) {
                    throw new RuntimeException("Missing ')' ");
                }
                return value;
            }
            return parseNumber();
        }

        private double parseNumber() {
            skipSpaces();
            int start = pos;
            while (!isEnd() && (Character.isDigit(current()) || current() == '.')) {
                pos++;
            }
            if (start == pos) {
                throw new RuntimeException("Number expected");
            }
            return Double.parseDouble(expression.substring(start, pos));
        }

        private void skipSpaces() {
            while (!isEnd() && Character.isWhitespace(current())) {
                pos++;
            }
        }

        private boolean match(char expected) {
            if (!isEnd() && current() == expected) {
                pos++;
                return true;
            }
            return false;
        }

        private boolean isEnd() {
            return pos >= expression.length();
        }

        private char current() {
            return expression.charAt(pos);
        }
    }
}
