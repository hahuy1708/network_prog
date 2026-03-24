package lab4.client;
import java.io.*;
import java.net.*;

public class ClientSocket {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientSocket(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public Object[] getStudentData() throws Exception {
        out.writeObject("GET_ALL_STUDENTS");
        out.flush();
        
        String[] columns = (String[]) in.readObject();
        String[][] data = (String[][]) in.readObject();
        return new Object[]{columns, data};
    }

    public void close() throws IOException { socket.close(); }
}