package bai2;
import java.io.*;
import java.net.*;

public class ChatServer {

	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(6000);
			System.out.println("Server chat ready ...");
			Socket s = ss.accept();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
            
            String send, receive;
            while(true){
            	if((receive = in.readLine()) != null) {
            		System.out.println("Client: " + receive);
            		if(receive.equalsIgnoreCase("exit")) break;
            	}
            	System.out.print("Server response: ");
            	send = kb.readLine();
            	out.println(send);
            }
            s.close();
            
		} catch (IOException e) { e.printStackTrace();}

	}

}
