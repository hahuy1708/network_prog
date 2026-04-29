package thltm_lab1.bai2;

import java.io.*;
import java.net.*;

public class CalculatorClient {
    public static void main(String[] args) {
        try (Socket s = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             BufferedReader kb = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                System.out.print("Nhap phep tinh (VD: 5+13-(12-4*6), go 'q' de thoat): ");
                String msg = kb.readLine();
                if (msg == null) {
                    break;
                }

                out.println(msg);
                if (msg.equalsIgnoreCase("q")) {
                    break;
                }

                String response = in.readLine();
                if (response == null) {
                    break;
                }
                System.out.println(response);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}