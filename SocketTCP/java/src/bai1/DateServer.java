package bai1;
import java.io.*;
import java.net.*;
import java.util.Date;


public class DateServer {

	public static void main(String[] args) {
		try {
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Server waiting for client...");
            
            while(true) {
                Socket s = ss.accept();
                
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                
                pw.println(new Date().toString());
                
                s.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    

	}

}
