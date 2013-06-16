package csheets.ext.rtc.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import csheets.ext.rtc.MulticastClient;
import csheets.ext.rtc.OnShareFoundListener;
import csheets.ext.rtc.ServerInformation;

/**
 * @author Gil Castro (gil_1110484); Rita Nogueira; Filipe Couto; Filipe Silva
 */
@SuppressWarnings("serial")
public class ConnectionDialog extends JDialog {
	private OnIPSelectListener listener;
	private final JPanel contentPanel = new JPanel();
	private JTextField userNameTextField;
	private JTextField portTextField;
	private JTextField addressTextField;
	private JPasswordField passwordTextField;
	private MulticastClientListAdapter testAdapter;
	private String[] serverSelected;
	private int selectedIndex;
	private MulticastClient searcher;
	private JList<MulticastClientListAdapter> serverList;
	// private Timer time;
	private String shareName;

	// private int port = 33334;

	public ConnectionDialog() {
		super((JFrame) null, "Connection Options", true);

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		// time = new Timer(1000, new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// try {
		// searcher.setPort(port);
		// } catch (NumberFormatException e1) {
		// } catch (UnknownHostException e1) {
		// e1.printStackTrace();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// time.stop();
		// }
		// });

		serverSelected = new String[3];
		serverSelected[0] = "";
		serverSelected[1] = "";
		serverSelected[2] = "";

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("Connect");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String userName = ConnectionDialog.this.userNameTextField.getText();
				String ipAddress = ConnectionDialog.this.addressTextField.getText();
				int port = Integer.valueOf(ConnectionDialog.this.portTextField
						.getText());
				String pass = null;
				try {
					pass = String.valueOf(ConnectionDialog.this.passwordTextField
							.getPassword());
				} catch (NullPointerException e) {
					System.out.println("ConnectionDialog " + e);
				}
				listener.onIPSelected(ipAddress, shareName, userName, port, pass);
				ConnectionDialog.this.setVisible(false);
				searcher.stop();

			}
		});
		okButton.setActionCommand("Connect");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// System.exit(0);
				ConnectionDialog.this.setVisible(false);
			}
		});

		testAdapter = new MulticastClientListAdapter();
		serverList = new JList<MulticastClientListAdapter>(testAdapter);
		serverList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedIndex = serverList.getSelectedIndex();
				if (selectedIndex >= 0) {
					ServerInformation server = testAdapter
							.getElementAt(selectedIndex);
					String ip = server.getIp().toString();
					shareName = server.getShareName();
					addressTextField.setText(ip);
					portTextField.setText(String.valueOf(server.getPort()));
				}
			}

		});
		JLabel userNameLabel = new JLabel("User name");

		userNameTextField = new JTextField();
		userNameTextField.setText("User" + (int) (Math.random() * 1000));
		userNameTextField.setColumns(10);
		userNameTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				userNameTextField.selectAll();
			}
		});

		JLabel addressLabel = new JLabel("Address");

		addressTextField = new JTextField();
		addressTextField.setText(serverSelected[1]);
		addressTextField.setColumns(10);

		JLabel portLabel = new JLabel("Port");

		portTextField = new JTextField();
		portTextField.setText(serverSelected[2]);
		portTextField.setColumns(10);
		// portTextField.getDocument().addDocumentListener(new DocumentListener()
		// {
		// @Override
		// public void removeUpdate(DocumentEvent arg0) {
		// updatePort();
		// }
		//
		// @Override
		// public void insertUpdate(DocumentEvent arg0) {
		// updatePort();
		// }
		//
		// @Override
		// public void changedUpdate(DocumentEvent arg0) {
		// updatePort();
		// }
		//
		// private void updatePort() {
		// searcher.stop();
		// testAdapter.clear();
		// time.restart();
		// }
		// });

		JLabel passwordLabel = new JLabel("Password");

		passwordTextField = new JPasswordField();
		passwordTextField.setColumns(10);

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(gl_contentPanel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_contentPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_contentPanel
										.createParallelGroup(Alignment.LEADING)
										.addComponent(addressLabel)
										.addComponent(userNameLabel)
										.addComponent(portLabel)
										.addComponent(passwordLabel)
										.addComponent(userNameTextField,
												GroupLayout.DEFAULT_SIZE, 113,
												Short.MAX_VALUE)
										.addComponent(addressTextField,
												GroupLayout.DEFAULT_SIZE, 113,
												Short.MAX_VALUE)
										.addComponent(portTextField,
												GroupLayout.DEFAULT_SIZE, 113,
												Short.MAX_VALUE)
										.addComponent(passwordTextField,
												GroupLayout.DEFAULT_SIZE, 113,
												Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED, 18,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(serverList, GroupLayout.PREFERRED_SIZE, 283,
								GroupLayout.PREFERRED_SIZE)));
		gl_contentPanel.setVerticalGroup(gl_contentPanel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_contentPanel
								.createSequentialGroup()
								.addComponent(userNameLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(userNameTextField,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(addressLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(addressTextField,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(portLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(portTextField,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGap(10)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(passwordLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(passwordTextField,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE).addGap(77))
				.addComponent(serverList, GroupLayout.DEFAULT_SIZE, 219,
						Short.MAX_VALUE));
		contentPanel.setLayout(gl_contentPanel);

		searcher = new MulticastClient(new OnShareFoundListener() {
			@Override
			public void onShareFound(ServerInformation shareInfo) {
				testAdapter.addShareInfo(shareInfo);
			}
		});
	}

	public void setOnIpSelectedListener(OnIPSelectListener listener) {
		this.listener = listener;
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			portTextField.setText("");
			addressTextField.setText("");
			testAdapter.clear();
			serverList.clearSelection();
			searcher.start();
		} else {
			searcher.stop();
		}
		super.setVisible(b);
	}
}
