package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ServerGUI extends JFrame {
	/**
	 * Place to show server logs
	 */
	private JTextArea logingArea;

	/**
	 * Button to stop server
	 */

	private JButton stopJB;
	/**
	 * Button to start server
	 */
	private JButton startJB;

	public ServerGUI() {
		super("Chat Server");
		Color backgroundColor = new Color(64,64,64);
		logingArea = new JTextArea(20, 50);
		stopJB = new JButton("Stop Server");
		startJB = new JButton("Start Server");
		logingArea.append("*** This is loggin area ****");
		// Panels for layout
		JPanel panelC = new JPanel();
		panelC.setBackground(backgroundColor);
		panelC.add(logingArea);
		JPanel panelPE = new JPanel();
		panelPE.setBackground(backgroundColor);
		panelPE.add(startJB);
		panelPE.add(stopJB);

		// add panels to the content pane
		getContentPane().add(panelC, BorderLayout.CENTER);
		getContentPane().add(panelPE, BorderLayout.PAGE_END);

		// frame properties
		setSize(600, 300);
		setLocation(200, 200);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * Append log to Login Area
	 * 
	 * @param log String
	 */
	public void addLog(String log) {
		this.logingArea.append("\n");
		this.logingArea.append(log);
	}

	/**
	 * clear Login Area
	 */
	public void clearLog() {
		this.logingArea.setText("");
	}

	/**
	 * Add ActionListener to Start button
	 * 
	 * @param actionListener ActionListener
	 */
	public void addActionListenerToStartJB(ActionListener actionListener) {
		this.startJB.addActionListener(actionListener);
	}
	public void disableStopButton() {
		stopJB.setEnabled(false);
	}
	public void enabelStopButton() {
		stopJB.setEnabled(true);
	}
	public void disableStartButton() {
		startJB.setEnabled(false);
	}
	public void enableStartButton() {
		startJB.setEnabled(true);
	}

	/**
	 * Add ActionListener to Stop button
	 * 
	 * @param actionListener
	 */
	public void addActionListenerToStopJB(ActionListener actionListener) {
		this.stopJB.addActionListener(actionListener);
	}
}
