package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Class ServerApp has main method and is responsible 
 * for starting server
 * @author bemben
 *
 */
public class ServerApp {
	ServerGUI serverGUI;
	Server server;

	/**
	 * Method main start application 
	 * @param args
	 */
	public static void main(String[] args) {
		new ServerApp();
	}
	
	/**
	 * Constructor
	 */
	public ServerApp() {
		serverGUI = new ServerGUI();
		server = new Server(serverGUI);
		serverGUI.addActionListenerToStartJB(new StartButtonListener());
		serverGUI.addActionListenerToStopJB(new StopButtonListener());
		serverGUI.disableStopButton();
	}

	///////////// Listener classes///////////
	/**
	 * Class StartButtonListener starts server thread 
	 * @author bemben
	 *
	 */
	private class StartButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// START server as a separate thread
			serverGUI.disableStartButton();
			serverGUI.addLog("Server START");
			Thread t = new Thread(server);
			t.start();
			serverGUI.enabelStopButton();
		}
	}
	/**
	 * Class StopButtonListener stops the server
	 * @author bemben
	 *
	 */
	public class StopButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// STOP server
			serverGUI.addLog("Server STOP");
			server.stopServer();
			serverGUI.disableStopButton();
		}
	}
}
