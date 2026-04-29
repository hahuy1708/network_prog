package thltm_lab1.bai1;


import java.io.*;
import java.net.*;

public class StringClient {
    public static void main(String[] args) {
        try (Socket s = new Socket("localhost", 7000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             BufferedReader kb = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                System.out.print("Nhap chuoi (nhap 'q' de thoat): ");
                String st = kb.readLine();
                if (st == null) {
                    break;
                }

                out.println(st);
                if (st.equalsIgnoreCase("q")) {
                    break;
                }

                System.out.println(in.readLine());
                System.out.println(in.readLine());
                System.out.println(in.readLine());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
