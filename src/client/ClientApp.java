package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class ClientApp extends JFrame {
	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	Socket socket;
	String selectedUsername = "";
	String selectedIpAddress = "";
	String selectedIcon = "boy.png";
	JPanel panelLS;

	public static void main(String[] args) {
		ClientApp test = new ClientApp();
	}

	public ClientApp() {
		super("Chat Client");
		clientSelectConnection();
	}
	
	/**
	 * Method to create and show a DialogBox which collects the user's input to start a connection.
	 */
	public void clientSelectConnection() {
		// Initializing fields for the dialog box.
		Object[] icons = {new ImageIcon(getClass().getResource("boy.png")), new ImageIcon(getClass().getResource("girl.png")), new ImageIcon(getClass().getResource("monster.png"))};
		JLabel userLabel = new JLabel("Username: ");
		JLabel addressLabel = new JLabel("IP Address: ");
		JTextField username = new JTextField();
		JTextField address = new JTextField("127.0.0.1");
		JLabel iconsLabel = new JLabel("Select your icon");
		JComboBox<Object> iconsCombo = new JComboBox<Object>(icons);
		// Action Listener for the icons combo box
		iconsCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int icon = iconsCombo.getSelectedIndex();
				switch(icon){
				case 0: selectedIcon = "boy.png"; break;
				case 1: selectedIcon = "girl.png"; break;
				case 2: selectedIcon = "monster.png"; break;
				}
			}
		});
		// Adding all fields to the panel
		JPanel panel = new JPanel(new GridLayout(0,1));
		panel.add(userLabel);
		panel.add(username);
		panel.add(addressLabel);
		panel.add(address);
		panel.add(iconsLabel);
		panel.add(iconsCombo);
		// Storing the result of the dialog box
		int result = JOptionPane.showConfirmDialog(null, panel, "Connect", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		// If the user clicks ok, store the information and initialize the chat application, else close the program.
		if(result == JOptionPane.OK_OPTION) {
			if(username.getText().equals("")){
				selectedUsername = "Anonymous";
			} else {
				selectedUsername = username.getText();
			}
			selectedIpAddress = address.getText();
			initFrame();
		}
		else {
			System.exit(0);;
		}
	}
	
	/**
	 * Method to initialize and show the Client chat GUI
	 */
	public void initFrame() {
		JPanel panelC = new JPanel();
		panelLS = new JPanel();
		// Setting up the incoming messages TextArea
		incoming = new JTextArea(15, 50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		
		JScrollPane scroller = new JScrollPane(incoming);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// Setting up the outgoing messages area
		outgoing = new JTextField(20);
		outgoing.requestFocusInWindow();
		outgoing.addActionListener(new SendButtonListener());
		
		JButton sendJB = new JButton("Send");
		sendJB.addActionListener(new SendButtonListener());
		
		// Adding elements to the panel
		panelC.add(scroller);
		panelC.add(outgoing);
		panelC.add(sendJB);
		panelC.setPreferredSize(new Dimension(600, 250));
		panelLS.setPreferredSize(new Dimension(150, 250));
		panelLS.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		// Establish a connection with the server
		setUpNetworkin();
		
		Thread readT = new Thread(new IncomingReader());
		readT.start();
		
		// Styling the elements in the JFrame
		Color backgroundColor = new Color(64,64,64);
		Color textFieldColor = new Color(160, 160, 160);
		Color textFieldFontColor = new Color(0, 0, 153);
		Font chatFont = new Font("SansSerif", Font.BOLD, 12);
		
		getContentPane().setBackground(backgroundColor);
		panelC.setBackground(backgroundColor);
		panelLS.setBackground(backgroundColor);
		incoming.setBackground(textFieldColor);
		outgoing.setBackground(textFieldColor);
		incoming.setForeground(textFieldFontColor);
		outgoing.setForeground(textFieldFontColor);
		incoming.setFont(chatFont);
		
		// Adding the panels to the JFrame
		getContentPane().add(panelC, BorderLayout.LINE_END);
		getContentPane().add(panelLS, BorderLayout.LINE_START);
		
		// JFrame set up
		setTitle("Chat Client - " + selectedUsername);
		addWindowListener(new WindowCloseListener());
		setSize(800,350);
		setLocation(500,300);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Method to establish a user connection with the server
	 */
	private void setUpNetworkin() {
		try {
			socket = new Socket(selectedIpAddress, 4545);
			InputStreamReader sr = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(sr);
			writer = new PrintWriter(socket.getOutputStream());
			System.out.println("Network OK\n");
			writer.println("login" + "+$$+" + selectedUsername + "+$$+" + selectedIcon);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send Button Listener class
	 * @author Igor
	 *
	 */
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				writer.println("msg" + "+$$+" + selectedUsername + "+$$+" + outgoing.getText());
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocusInWindow();
		}
	} // end of class SendButtonListener
	
	/**
	 * Window Listener class
	 * @author Igor
	 *
	 */
	public class WindowCloseListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			int result = JOptionPane.showConfirmDialog(null, "You will be disconnected from the chat.", "Close Connection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
			if(result == JOptionPane.OK_OPTION) {
				try {
					writer.println("logout" + "+$$+" + selectedUsername + "+$$+" + selectedIcon);
					writer.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	} // end of class WindowCloseListener
	
	/**
	 * Class IncomingReader, collects the message sent by the server and filters it in order to display the correct information
	 * @author Igor
	 *
	 */
	public class IncomingReader implements Runnable {
		@Override
		public void run() {
			String msg;
			try {
				while ((msg = reader.readLine()) != null) {
					// If the message contains a delimiter, tokenize the message and turn the information into a list of active users. 
					// Only the user list updates will be sent with a delimiter.
					// Else, print the message as a chat message
					if(msg.indexOf("+$$+") != -1) {
						panelLS.removeAll();
						StringTokenizer st = new StringTokenizer(msg, "+$$+");
						while(st.hasMoreTokens()) {
							JLabel userLabel = new JLabel(st.nextToken());
							userLabel.setIcon(new ImageIcon(getClass().getResource(st.nextToken())));
							userLabel.setForeground(Color.GREEN);
							panelLS.add(userLabel);
							
						}
						panelLS.revalidate();
						panelLS.repaint();
					}
					else {
						System.out.println("read " + msg);
						incoming.append(msg + "\n");
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
