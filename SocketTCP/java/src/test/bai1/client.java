package test.bai1;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class client {
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 5000;

	private JFrame frame;
	private JTextField inputField;
	private JButton sendButton;
	private JLabel statusLabel;
	private JTextArea logArea;

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new client().start());
	}

	private void start() {
		buildUi();
		connect();
	}

	private void buildUi() {
		frame = new JFrame("TCP Client - Bai 1");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		statusLabel = new JLabel("Disconnected");
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

	private void connect() {
		appendLog("Connecting to " + HOST + ":" + PORT + "...");
		new Thread(() -> {
			try {
				socket = new Socket(HOST, PORT);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				updateStatus("Connected to " + HOST + ":" + PORT, true);
			} catch (IOException ex) {
				appendLog("Connection failed: " + ex.getMessage());
				updateStatus("Connection failed", false);
			}
		}).start();
	}

	private void onSend() {
		if (out == null) {
			appendLog("Not connected.");
			return;
		}

		String input = inputField.getText().trim();
		if (input.isEmpty()) {
			return;
		}

		int n;
		try {
			n = Integer.parseInt(input);
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
				out.println(n);
				String response = in.readLine();
				if (response == null) {
					appendLog("Server closed.");
					stopClient("Disconnected");
					return;
				}
				appendLog("Server: " + response);
				if ("STOP".equalsIgnoreCase(response)) {
					appendLog("Stop condition met.");
				}
			} catch (IOException ex) {
				appendLog("I/O error: " + ex.getMessage());
				stopClient("Disconnected");
				return;
			}
			if (isConnected()) {
				setSendEnabled(true);
			}
		}).start();
	}

	private boolean isConnected() {
		return socket != null && socket.isConnected() && !socket.isClosed();
	}

	private void stopClient(String status) {
		setSendEnabled(false);
		updateStatus(status, false);
		closeSocket();
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
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException ex) {
			appendLog("Close error: " + ex.getMessage());
		}
	}
}
