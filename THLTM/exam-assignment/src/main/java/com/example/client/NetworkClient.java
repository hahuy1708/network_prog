package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

public class NetworkClient {

    private final String host;
    private final int port;
    private String lastServerIp;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public byte[][] guiVaNhan(int m, int n, File fileCanBo, File filePhong)
            throws IOException {

        try (Socket socket = new Socket(host, port);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            lastServerIp = dis.readUTF();

            dos.writeInt(m);
            dos.writeInt(n);
            dos.flush();

            guiFile(dos, Files.readAllBytes(fileCanBo.toPath()));
            guiFile(dos, Files.readAllBytes(filePhong.toPath()));

            byte[] phanCong = nhanFile(dis);
            byte[] giamSat = nhanFile(dis);

            return new byte[][]{phanCong, giamSat};
        }
    }

    public String getLastServerIp() {
        return lastServerIp;
    }

    private void guiFile(DataOutputStream dos, byte[] data) throws IOException {
        dos.writeInt(data.length);
        dos.write(data);
        dos.flush();
    }

    private byte[] nhanFile(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        byte[] data = new byte[len];
        dis.readFully(data);
        return data;
    }
}
