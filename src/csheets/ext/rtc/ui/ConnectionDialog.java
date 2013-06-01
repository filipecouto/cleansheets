package csheets.ext.rtc.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectionDialog extends JDialog {
    private JTextField ipAddress;
    private JTextField userName;
    private JTextField connectionPort;
    private JButton buttonCancel;
    private JButton buttonAccept;
    private OnIPSelectListener listener;
    private JPanel panelButtons;

    public ConnectionDialog() {
	super((JFrame) null, "Connection Options", true);

	setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

	// creates textfield for insertion
	userName = new JTextField();
	userName.setText("Username");
	userName.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		ConnectionDialog.this.userName.selectAll();
	    }
	});

	ipAddress = new JTextField();
	ipAddress.setText("IP address or URL");
	ipAddress.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		ConnectionDialog.this.ipAddress.selectAll();
	    }
	});

	connectionPort = new JTextField();
	connectionPort.setText("Port");
	connectionPort.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		connectionPort.selectAll();
	    }
	});

	buttonAccept = new JButton("Connect");
	buttonAccept.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		String userName = ConnectionDialog.this.userName.getText();
		String ipAddress = ConnectionDialog.this.ipAddress.getText();
		int port = Integer.valueOf(ConnectionDialog.this.connectionPort
			.getText());
		listener.onIPSelected(ipAddress, userName, port);
		ConnectionDialog.this.setVisible(false);
	    }
	});

	buttonCancel = new JButton("Cancel");
	buttonCancel.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		ConnectionDialog.this.setVisible(false);
	    }
	});
	add(userName);
	add(ipAddress);
	add(connectionPort);
	panelButtons = new JPanel();
	panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
	panelButtons.add(buttonAccept);
	panelButtons.add(buttonCancel);
	add(panelButtons);

	pack();
	setLocationRelativeTo(null);
    }

    public void setOnIpSelectedListener(OnIPSelectListener listener) {
	this.listener = listener;
    }
}
