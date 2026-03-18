package lab1;
import java.io.*;
import java.net.*;
public class lab1_client {
	public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1234);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            
            System.out.print("Nhap chuoi: ");
            String userInput = stdIn.readLine();
            out.println(userInput);

            String serverRes;
            while (!(serverRes = in.readLine()).equals("END")) {
                System.out.println(serverRes);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
