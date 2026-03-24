package lab4.server;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JTextArea;

public class ClientHandler extends Thread {
    private Socket socket;
    private JTextArea log;

    public ClientHandler(Socket s, JTextArea log) {
        this.socket = s;
        this.log = log;
    }

    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            String command = (String) in.readObject();
            if ("GET_ALL_STUDENTS".equals(command)) {
                fetchData(out);
            }
        } catch (Exception e) {
            log.append("Lỗi Client: " + e.getMessage() + "\n");
        }
    }

    private void fetchData(ObjectOutputStream out) throws Exception {
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM sv")) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            
            
            String[] columnNames = new String[cols];
            for (int i = 1; i <= cols; i++) columnNames[i-1] = meta.getColumnName(i);

           
            ArrayList<String[]> dataList = new ArrayList<>();
            while (rs.next()) {
                String[] row = new String[cols];
                for (int i = 1; i <= cols; i++) row[i-1] = rs.getString(i);
                dataList.add(row);
            }
            String[][] data = dataList.toArray(new String[0][]);

           
            out.writeObject(columnNames);
            out.writeObject(data);
            out.flush();
        }
    }
}