package test.bai1;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
	private static final int PORT = 5000;

	private JFrame frame;
	private JTextArea logArea;
	private volatile boolean running = true;
	private ServerSocket serverSocket;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new server().start());
	}

	private void start() {
		buildUi();
		startServer();
	}

	private void buildUi() {
		frame = new JFrame("TCP Server - Bai 1");
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
			try (ServerSocket ss = new ServerSocket(PORT)) {
				serverSocket = ss;
				appendLog("TCP server listening on port " + PORT);
				while (running) {
					try (
							Socket socket = ss.accept();
							BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)
					) {
						appendLog("Client connected: " + socket.getInetAddress());
						String line;
						while (running && (line = in.readLine()) != null) {
							line = line.trim();
							if (line.isEmpty()) {
								continue;
							}

							int n;
							try {
								n = Integer.parseInt(line);
							} catch (NumberFormatException ex) {
								out.println("INVALID");
								appendLog("Invalid input: " + line);
								continue;
							}

							if (n <= 0) {
								out.println("INVALID");
								appendLog("Invalid input: " + line);
								continue;
							}

							if (isBinaryPalindrome(n)) {
								out.println("STOP");
								appendLog("Stop condition met: " + n + " (" + Integer.toBinaryString(n) + ")");
								continue;
							}

							out.println("CONTINUE");
							appendLog("Received " + n + " -> CONTINUE");
						}
						appendLog("Client disconnected.");
					} catch (IOException ex) {
						if (running) {
							appendLog("Client error: " + ex.getMessage());
						}
					}
				}
				appendLog("Server stopped.");
			} catch (IOException ex) {
				if (running) {
					appendLog("Server error: " + ex.getMessage());
				}
			}
		}).start();
	}

	private void stopServer() {
		running = false;
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException ex) {
			appendLog("Close error: " + ex.getMessage());
		}
	}

	private void appendLog(String text) {
		SwingUtilities.invokeLater(() -> {
			logArea.append(text + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	private static boolean isBinaryPalindrome(int n) {
		String bin = Integer.toBinaryString(n);
		int i = 0;
		int j = bin.length() - 1;
		while (i < j) {
			if (bin.charAt(i) != bin.charAt(j)) {
				return false;
			}
			i++;
			j--;
		}
		return true;
	}
}
