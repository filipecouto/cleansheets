package csheets.ext.rtc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import csheets.ext.rtc.ClientInfo;
import csheets.ext.rtc.RealTimeCollaboration;
import csheets.ext.rtc.RtcCommunicator;
import csheets.ui.ctrl.UIController;

/**
 * 
 * @author Rita Nogueira
 *
 */
public class RtcSidebar extends JPanel {
    private static final long serialVersionUID = -5795254724858699202L;

    private JButton bShare;
    private JButton bConnect;
    private JButton bDisconnect;
    private JLabel ipAddress;
    private ClientsListAdapter clientAdapter;
    private ServerListAdapter shareAdapter;
    private RealTimeCollaboration extension;

    private JList<ClientInfo> list;

    private JList<RtcCommunicator> serverList;

    public RtcSidebar(RealTimeCollaboration extension,
	    UIController uiController, ShareAction shareAction,
	    ConnectAction connectAction) {
	this.extension = extension;
	setName("Real Time Colaboration");
	setLayout(new BorderLayout());
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

	bShare = new JButton(shareAction);

	bShare.setAlignmentX(JComponent.CENTER_ALIGNMENT);

	buttonPanel.add(bShare);
	bConnect = new JButton(connectAction);
	ipAddress = new JLabel();
	ipAddress.setText("Not connected");
	ipAddress.setAlignmentX(JComponent.CENTER_ALIGNMENT);

	bConnect.setAlignmentX(JComponent.CENTER_ALIGNMENT);

	buttonPanel.add(bConnect);

	bDisconnect = new JButton("Disconnect");
	bDisconnect.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	bDisconnect.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		// ipAddress.setText("Not connected");
		// bShare.setVisible(true);
		// bConnect.setVisible(true);
		// bDisconnect.setVisible(false);
		RtcSidebar.this.extension.disconnect();
	    }
	});
	buttonPanel.add(bDisconnect);
	buttonPanel.add(ipAddress);
	bDisconnect.setVisible(false);
	bShare.setSize(100, 30);
	bConnect.setSize(100, 30);

	JPanel listPanel = new JPanel();

	clientAdapter = new ClientsListAdapter();
	list = new JList<ClientInfo>(clientAdapter);
	list.setCellRenderer(new ClientInfoListRenderer());
	list.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	listPanel.add(list);

	shareAdapter = new ServerListAdapter();
	serverList = new JList<RtcCommunicator>(shareAdapter);
	serverList.setBorder(UIManager.getBorder("CheckBox.border"));
	serverList.setBackground(SystemColor.menu);
	listPanel.add(serverList);
	
	
	serverList.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent l) {
		if(serverList.isEnabled()){
		    
		}
		clientAdapter.update(shareAdapter.getElementAt(
			serverList.getSelectedIndex()).getConnectedUsers());
	    }
	});
	GroupLayout gl_contentPanel = new GroupLayout(listPanel);
	gl_contentPanel.setHorizontalGroup(gl_contentPanel
		.createParallelGroup(Alignment.LEADING)
		.addComponent(serverList, GroupLayout.DEFAULT_SIZE, 300,
			Short.MAX_VALUE)
		.addComponent(list, GroupLayout.DEFAULT_SIZE, 310,
			Short.MAX_VALUE));
	gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(
		Alignment.LEADING).addGroup(
		Alignment.TRAILING,
		gl_contentPanel
			.createSequentialGroup()
			.addComponent(list, GroupLayout.DEFAULT_SIZE, 216,
				Short.MAX_VALUE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(serverList, GroupLayout.PREFERRED_SIZE,
				170, GroupLayout.PREFERRED_SIZE)));

	listPanel.setLayout(gl_contentPanel);
	add(listPanel, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateUsersList(final ClientInfo[] clients) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		clientAdapter.update(clients);
	    }
	});
    }

    public void updateServersList(final RtcCommunicator[] servers) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		shareAdapter.update(servers);
	    }
	});
    }

    public void onConnection(String address) {
	if (ipAddress != null) {
	    // ipAddress.setText("Connected: " + address);
	    ipAddress.setText(address == null ? "Failed" : "Connected");
	    bDisconnect.setVisible(true);
	    bShare.setVisible(true);
	    bConnect.setVisible(false);
	}
    }

    public void onDisconnected() {
	ipAddress.setText("Not connected");
	bShare.setVisible(true);
	bConnect.setVisible(true);
	bDisconnect.setVisible(false);
    }

    public void onConnectionFailed(Exception e) {
	JOptionPane.showMessageDialog(getParent(),
		"There was a problem while connecting: " + e.getMessage(),
		"Can't connect", JOptionPane.ERROR_MESSAGE);
    }
}
