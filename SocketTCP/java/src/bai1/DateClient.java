package bai1;
import java.io.*;
import java.net.*;

public class DateClient {

	public static void main(String[] args) {
		try {
            Socket client = new Socket("localhost", 5000);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            String time = in.readLine();
            System.out.println("Ngày tháng năm từ Server: " + time);
            
            client.close();
        } catch (IOException e) {
            System.err.println(e);
        }

	}

}
