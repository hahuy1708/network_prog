package thltm_lab2.bai2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class CalculatorServer {
    private static String process(String expression) {
        try {
            double result = eval(expression);
            return "Ket qua: " + result;
        } catch (Exception e) {
            return "Loi: Bieu thuc khong hop le.";
        }
    }

    public static void main(String[] args) {
        int port = 1234;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Calculator UDP Server dang cho tai cong " + port);

            byte[] buffer = new byte[2048];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String expression = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                if (expression.equalsIgnoreCase("exit")) {
                    continue;
                }

                String result = process(expression);
                byte[] data = result.getBytes(StandardCharsets.UTF_8);
                DatagramPacket resp = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
                socket.send(resp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() { nextChar(); return parseExpression(); }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;
                if (eat('(')) { x = parseExpression(); eat(')'); }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else throw new RuntimeException();
                return x;
            }
        }.parse();
    }
}
