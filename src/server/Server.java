package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import client.User;

/**
 * 
 */

/**
 * Class ServerApp is creates server for group chat
 * 
 *
 */
public class Server implements Runnable {
	/**
	 * Stores list of all writers to chat users
	 */
	private ArrayList<PrintWriter> writersList;
	/**
	 * user list is to store users in corresponding list to writers list
	 */
	private Vector<User> userList;

	private boolean isServerRunning;
	private ServerSocket serverSocet;
	private int serverPort;
	private ServerGUI serverGUI;
	
	/**
	 * Constructor
	 * @param serverGUI
	 */
	public Server(ServerGUI serverGUI) {
		this.serverGUI = serverGUI;
		isServerRunning = true;
		serverPort = 4545;
		writersList = new ArrayList<PrintWriter>();
		userList = new Vector<User>();
		
		serverGUI.addLog("Server was created - press Start to run it");
		System.out.println("Server was created");
	}

	/**
	 * 
	 */
	public void run() {
		try {
			serverSocet = new ServerSocket(serverPort);
			
			serverGUI.addLog("Server start listening on - "+ serverSocet.getLocalSocketAddress());
			while (isServerRunning) {
				
				// for each incoming connection is created separate socket
				Socket clientSocket = serverSocet.accept();
				serverGUI.addLog("New client conected from "+ clientSocket.getLocalSocketAddress());
				// create PrintWirter object for new connection
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				
				// add client writer to the list
				writersList.add(writer);
				
				// EXTERA TODO CREATE reader for each client to have log info
				Thread t = new Thread(new ClientServer(clientSocket));
				t.start();
			}
			serverSocet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		writeToAllClients("log:Server was turned off!");
		setServerRunning(false);
		// System.exit(0);
	}

	private void writeToAllClients(String msg) {
		Iterator<PrintWriter> it = writersList.iterator();
		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(msg);
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Process User Message before is send
	 * first token flag: login, logout, msg 
	 * @param msg String
	 */
	private void processMsg(String msg) {
		String forwardMessage = msg;
		StringTokenizer st = new StringTokenizer(forwardMessage, "+$$+");
		String parameter;
		String username;
		String chatMessage;
		String icon;
		while (st.hasMoreTokens()){
			parameter = st.nextToken();
			username = st.nextToken();

			if (parameter.equals("msg")){ 
				chatMessage = st.nextToken();
				forwardMessage = username + ": " + chatMessage;
			} else if (parameter.equals("login")){
				icon = st.nextToken();
				//System.out.println(icon);
				userList.add(new User(username, icon));
				forwardMessage = "\t\tUser " + username + " has joinned the chat.";
				serverGUI.addLog("User " + username + " has joinned the chat!");
				sendUserListUpdate();
				
			} else if (parameter.equals("logout")){
				icon = st.nextToken();
				userList.removeElement(findUserInVector(username));
				forwardMessage = "\t\tUser " + username + " disconnected.";
				serverGUI.addLog("User " + username + " disconnected.");
				sendUserListUpdate();
			}  
		}
		writeToAllClients(forwardMessage);
	}
	
	public User findUserInVector(String username) {
		User foundUser = null;
		for(int i = 0; i < userList.size(); i++) {
			if(userList.elementAt(i).getName().equals(username)) {
				foundUser = userList.elementAt(i);
			}
		}
		return foundUser;
	}
	
	public void sendUserListUpdate() {
		String activeUsers = "";
		for(int i = 0; i < userList.size(); i++) {
			activeUsers += userList.elementAt(i).getName() + "+$$+" + userList.elementAt(i).getIconName() + "+$$+";
		}
		System.out.println(activeUsers);
		writeToAllClients(activeUsers);
	}

	public void setServerRunning(boolean val) {
		this.isServerRunning = val;
	}

	public boolean isServerRunning() {
		return isServerRunning;
	}

	/**
	 * 
	 *
	 */
	public class ClientServer implements Runnable {

		BufferedReader reader;
		Socket clientSocket;

		public ClientServer(Socket clientSocket) {
			this.clientSocket = clientSocket;
			try {
				// create reader to read messages form chat user
				InputStreamReader isr;
				isr = new InputStreamReader(clientSocket.getInputStream());
				reader = new BufferedReader(isr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String msg;
			try {
				while ((msg = reader.readLine()) != null) {
					processMsg(msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	} // end of class ClientServer


}
