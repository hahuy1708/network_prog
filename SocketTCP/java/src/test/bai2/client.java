package test.bai2;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class client {
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 5001;
	private static final int BUFFER_SIZE = 1024;
	private static final int TIMEOUT_MS = 5000;

	private JFrame frame;
	private JTextField inputField;
	private JButton sendButton;
	private JLabel statusLabel;
	private JTextArea logArea;

	private DatagramSocket socket;
	private InetAddress address;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new client().start());
	}

	private void start() {
		buildUi();
		initSocket();
	}

	private void buildUi() {
		frame = new JFrame("UDP Client - Bai 2");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		statusLabel = new JLabel("Not ready");
		inputField = new JTextField();
		sendButton = new JButton("Send");
		sendButton.setEnabled(false);
		sendButton.addActionListener(event -> onSend());

		JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
		inputPanel.add(new JLabel("Positive number:"), BorderLayout.WEST);
		inputPanel.add(inputField, BorderLayout.CENTER);
		inputPanel.add(sendButton, BorderLayout.EAST);

		JPanel topPanel = new JPanel(new BorderLayout(8, 8));
		topPanel.add(statusLabel, BorderLayout.NORTH);
		topPanel.add(inputPanel, BorderLayout.SOUTH);

		logArea = new JTextArea(12, 48);
		logArea.setEditable(false);

		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(new JScrollPane(logArea), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				closeSocket();
			}

			@Override
			public void windowClosed(WindowEvent event) {
				closeSocket();
			}
		});
	}

	private void initSocket() {
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(TIMEOUT_MS);
			address = InetAddress.getByName(HOST);
			updateStatus("Ready for " + HOST + ":" + PORT, true);
		} catch (IOException ex) {
			appendLog("Socket init failed: " + ex.getMessage());
			updateStatus("Not ready", false);
		}
	}

	private void onSend() {
		if (socket == null) {
			appendLog("Socket is not ready.");
			return;
		}

		String input = inputField.getText().trim();
		if (input.isEmpty()) {
			return;
		}

		long n;
		try {
			n = Long.parseLong(input);
		} catch (NumberFormatException ex) {
			appendLog("Invalid number.");
			return;
		}

		if (n <= 0) {
			appendLog("Please enter a positive integer.");
			return;
		}

		inputField.setText("");
		setSendEnabled(false);

		new Thread(() -> {
			try {
				byte[] data = String.valueOf(n).getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);
				socket.send(packet);

				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(responsePacket);
				String response = new String(responsePacket.getData(), 0, responsePacket.getLength()).trim();
				appendLog("Server: " + response);

				if ("STOP".equalsIgnoreCase(response)) {
					appendLog("Stop condition met.");
				}
			} catch (SocketTimeoutException ex) {
				appendLog("No response (timeout).");
			} catch (IOException ex) {
				appendLog("I/O error: " + ex.getMessage());
			}
			setSendEnabled(true);
		}).start();
	}

	private void updateStatus(String status, boolean allowSend) {
		SwingUtilities.invokeLater(() -> {
			statusLabel.setText(status);
			sendButton.setEnabled(allowSend);
		});
	}

	private void setSendEnabled(boolean enabled) {
		SwingUtilities.invokeLater(() -> sendButton.setEnabled(enabled));
	}

	private void appendLog(String text) {
		SwingUtilities.invokeLater(() -> {
			logArea.append(text + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	private void closeSocket() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}
}
