package thltm_lab1.bai1;

import java.io.*;
import java.net.*;

public class StringServer {

		private static int countWords(String s) {
	        String trimmed = s.trim();
	        if (trimmed.isEmpty()) {
	            return 0;
	        }
	        return trimmed.split("\\s+").length;
	    }

		public static void main(String[] args) {
		    try (ServerSocket ss = new ServerSocket(7000)) {
		        System.out.println("Server san sang...");

		        while (true) {
		            try (Socket s = ss.accept();
		                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		                 PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
		                System.out.println("Client connected");

		                String st;
		                while ((st = in.readLine()) != null) {
		                    if (st.equalsIgnoreCase("exit")) {
		                        break;
		                    }

		                    out.println("In Hoa: " + st.toUpperCase());
		                    out.println("In thuong: " + st.toLowerCase());
		                    out.println("So tu: " + countWords(st));
		                }
		                System.out.println("Client ngat ket noi");
		            }
		        }

		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	

}
