package com.example.server;

import com.example.logic.AssignmentLogic;
import com.example.model.AssignmentResult;
import com.example.model.CanBo;
import com.example.model.PhongThi;
import com.example.util.DatabaseManager;
import com.example.util.ExcelHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ServerGUI gui;

    public ClientHandler(Socket socket, ServerGUI gui) {
        this.socket = socket;
        this.gui = gui;
    }

    @Override
    public void run() {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            String serverIp = gui.getLanIP();
            if (serverIp == null || serverIp.isBlank() || "localhost".equalsIgnoreCase(serverIp)) {
                serverIp = socket.getLocalAddress().getHostAddress();
            }
            dos.writeUTF(serverIp);
            dos.flush();

            int m = dis.readInt();
            int n = dis.readInt();
            gui.log("Nhan yeu cau: m=" + m + " giam thi, n=" + n + " phong");

            byte[] canBoBytes = nhanFile(dis);
            gui.log("Nhan file can bo: " + canBoBytes.length + " bytes");

            byte[] phongBytes = nhanFile(dis);
            gui.log("Nhan file phong thi: " + phongBytes.length + " bytes");

            String tmp = System.getProperty("java.io.tmpdir");
            String canBoPath = tmp + File.separator + "canbo_tmp.xlsx";
            String phongPath = tmp + File.separator + "phong_tmp.xlsx";
            Files.write(Paths.get(canBoPath), canBoBytes);
            Files.write(Paths.get(phongPath), phongBytes);

            List<CanBo> allCanBo = ExcelHandler.docCanBo(canBoPath);
            List<PhongThi> allPhong = ExcelHandler.docPhongThi(phongPath);
            gui.log("File co: " + allCanBo.size() + " can bo, " + allPhong.size() + " phong");

            m = Math.min(m, allCanBo.size());
            n = Math.min(n, allPhong.size());
            List<CanBo> canBoList = allCanBo.subList(0, m);
            List<PhongThi> phongList = allPhong.subList(0, n);
            gui.log("Su dung: " + m + " can bo, " + n + " phong");

            AssignmentResult result = AssignmentLogic.phanCong(
                    canBoList, phongList, new HashMap<>(), 1);
            gui.log("Phan cong xong: "
                    + result.getDanhSachPhanCong().size() + " dong GT, "
                    + result.getGiamSatHanhLang().size() + " can bo hanh lang");

            String pcPath = tmp + File.separator + "DANHSACHPHANCONG.xlsx";
            String gsPath = tmp + File.separator + "DANHSACHGIAMSAT.xlsx";
            ExcelHandler.ghiDanhSachPhanCong(result.getDanhSachPhanCong(), pcPath);
            ExcelHandler.ghiDanhSachGiamSat(result.getGiamSatHanhLang(), phongList, gsPath);

            try {
                DatabaseManager.luuKetQua(result);
                gui.log("Da luu vao MySQL thanh cong");
            } catch (Exception dbEx) {
                gui.log("Loi MySQL: " + dbEx.getMessage());
            }

            guiFile(dos, Files.readAllBytes(Paths.get(pcPath)));
            guiFile(dos, Files.readAllBytes(Paths.get(gsPath)));
            gui.log("Da gui 2 file ket qua ve Client\n" + "-".repeat(60));

        } catch (Exception e) {
            gui.log("Loi xu ly client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
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
