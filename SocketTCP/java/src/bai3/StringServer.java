package bai3;
import java.io.*;
import java.net.*;

public class StringServer {

		private static String Mixed(String s) {
	        String res = "";
	        for (int i = 0; i < s.length(); i++) {
	            char c = s.charAt(i);
	            if (c >= 'A' && c <= 'Z') c = (char)(c + 32);
	            else if (c >= 'a' && c <= 'z') c = (char)(c - 32);
	            res += c;
	        }
	        return res;
	    }

	    public static void main(String[] args) {
	        try {
	            ServerSocket ss = new ServerSocket(7000);
	            System.out.println("Server ready...");
	            Socket s = ss.accept();
	            
	            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

	            String st = in.readLine();
	            if (st != null) {
	                out.println("Upper case: " + st.toUpperCase());
	                out.println("Mixed ase: " + Mixed(st));
	                int count = st.trim().isEmpty() ? 0 : st.trim().split("\\s+").length;
	                out.println("Letter count: " + count);
	            }
	            s.close();
	        } catch (IOException e) { e.printStackTrace(); }
	    }
	

}
