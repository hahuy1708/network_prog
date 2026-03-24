package lab2.server;
import java.io.*;
import java.net.*;
import javax.swing.JTextArea;

public class ClientHandler extends Thread {
    private Socket socket;
    private JTextArea log;

    public ClientHandler(Socket s, JTextArea log) {
        this.socket = s;
        this.log = log;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            String expression = in.readLine();
            if (expression != null) {
                log.append("Đang tính: " + expression + "\n");
                try {
                    double result = evaluate(expression);
                    out.println("Kết quả: " + result);
                } catch (Exception e) {
                    out.println("Lỗi: Biểu thức không hợp lệ!");
                }
            }
        } catch (IOException e) {
            log.append("Lỗi ClientHandler: " + e.getMessage() + "\n");
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    // Logic tính toán cơ bản cho các toán tử +, -, *, / và dấu ngoặc [1]
    private double evaluate(String expression) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < expression.length()) ? expression.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() { nextChar(); double x = parseExpression(); return x; }
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
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else throw new RuntimeException("Ký tự lạ: " + (char)ch);
                return x;
            }
        }.parse();
    }
}