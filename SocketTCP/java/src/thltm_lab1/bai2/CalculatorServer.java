package thltm_lab1.bai2;
import java.io.*;
import java.net.*;

public class CalculatorServer {
    public static void main(String[] args) {
        int port = 1234;
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Calculator Server dang cho tai cong " + port);
            while (true) {
                Socket s = ss.accept(); // Chap nhan ket noi [5]
                new CalcHandler(s).start(); // Xu ly đa tuyen [6]
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}

class CalcHandler extends Thread {
    private Socket socket;
    public CalcHandler(Socket s) { this.socket = s; }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String expression;
            while ((expression = in.readLine()) != null) { 
                if (expression.equalsIgnoreCase("exit")) {
                    break;
                }
                try {
                    double result = eval(expression);
                    out.println("Ket qua: " + result); // Gui ket qua tra ve [8]
                } catch (Exception e) {
                    out.println("Loi: Bieu thuc khong hop le.");
                }
            }
        } catch (IOException e) { }
    }

    // Ham logic tinh toan bieu thuc (ho tro +, -, *, /, ())
    private double eval(final String str) {
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