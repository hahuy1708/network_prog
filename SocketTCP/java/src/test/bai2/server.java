package test.bai2;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class server {
	private static final int PORT = 5001;
	private static final int BUFFER_SIZE = 1024;

	private JFrame frame;
	private JTextArea logArea;
	private volatile boolean running = true;
	private DatagramSocket socket;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new server().start());
	}

	private void start() {
		buildUi();
		startServer();
	}

	private void buildUi() {
		frame = new JFrame("UDP Server - Bai 2");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		logArea = new JTextArea(14, 52);
		logArea.setEditable(false);

		frame.add(new JScrollPane(logArea), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				stopServer();
			}

			@Override
			public void windowClosed(WindowEvent event) {
				stopServer();
			}
		});
	}

	private void startServer() {
		new Thread(() -> {
			try (DatagramSocket ds = new DatagramSocket(PORT)) {
				socket = ds;
				appendLog("UDP server listening on port " + PORT);
				byte[] buffer = new byte[BUFFER_SIZE];
				while (running) {
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					ds.receive(packet);

					String text = new String(packet.getData(), 0, packet.getLength()).trim();
					if (text.isEmpty()) {
						continue;
					}

					String reply;
					try {
						long n = Long.parseLong(text);
						if (n <= 0) {
							reply = "INVALID";
						} else if (isFibonacci(n)) {
							reply = "STOP";
							appendLog("Stop condition met: " + n);
						} else {
							reply = "CONTINUE";
						}
					} catch (NumberFormatException ex) {
						reply = "INVALID";
					}

					byte[] replyBytes = reply.getBytes();
					DatagramPacket replyPacket = new DatagramPacket(
							replyBytes,
							replyBytes.length,
							packet.getAddress(),
							packet.getPort()
					);
					ds.send(replyPacket);
					appendLog("Received " + text + " -> " + reply);
				}
				appendLog("Server stopped.");
			} catch (SocketException ex) {
				if (running) {
					appendLog("Socket error: " + ex.getMessage());
				}
			} catch (IOException ex) {
				if (running) {
					appendLog("Server error: " + ex.getMessage());
				}
			}
		}).start();
	}

	private void stopServer() {
		running = false;
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}

	private void appendLog(String text) {
		SwingUtilities.invokeLater(() -> {
			logArea.append(text + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	private static boolean isFibonacci(long n) {
		long a = 0;
		long b = 1;
		while (b < n && b > 0) {
			long next = a + b;
			a = b;
			b = next;
		}
		return n == a || n == b;
	}
}
