package csheets.ext.rtc.ui;

import java.awt.Frame;
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

// REFACTOR: this name doesn't make much sense either
public class ConnectionDialog extends JDialog {

    private JTextField ipAddress;
    private JTextField userName;
    private JButton buttonCancel;
    private JButton buttonAccept;
    private OnIPSelectListener listener;
    private JPanel panelButtons;

    public ConnectionDialog() {
	super((JFrame) null, "Connection Form",
		true);

	setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


	// creates textfield for insertion
	userName = new JTextField();
	userName.setText("Insert username");
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
	ipAddress.setText("Insert IP address or URL");
	ipAddress.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		ConnectionDialog.this.ipAddress.selectAll();
	    }
	});

	buttonAccept = new JButton("Accept");
	buttonAccept.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		listener.onIPSelected(ConnectionDialog.this.ipAddress.getText() , ConnectionDialog.this.userName.getText());
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
	panelButtons = new JPanel();
	panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
	panelButtons.add(buttonAccept);
	panelButtons.add(buttonCancel);
	add(panelButtons);
	setLocationRelativeTo(null);
	
	pack();
    }

    public void setOnIpSelectedListener(OnIPSelectListener listener) {
	this.listener = listener;
    }
}
