package bai3;

import java.io.*;
import java.net.*;

public class StringClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 7000);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter string st: ");
            String st = kb.readLine();
            out.println(st);

            System.out.println(in.readLine());
            System.out.println(in.readLine());
            System.out.println(in.readLine());

            s.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
