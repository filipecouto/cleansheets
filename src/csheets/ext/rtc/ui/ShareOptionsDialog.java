package csheets.ext.rtc.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * 
 * @author Rita Nogueira; Filipe Couto
 *
 */
public class ShareOptionsDialog extends JDialog {
    private JRadioButton selectWhole;
    private JRadioButton selectSelected;
    private JButton buttonAccept;
    private JButton buttonCancel;
    private OnChooseExportListener listener;
    private ButtonGroup group;
    private JTextField userName;
    private JTextField connectionPort;
    private JTextField shareName;

    public ShareOptionsDialog() {
	super((JFrame) null, "Sharing Options", true);

	setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	group = new ButtonGroup();
	JPanel infoPanel = new JPanel();
	JLabel lblShareName = new JLabel("Share Name");

	shareName = new JTextField();
	shareName.setColumns(5);
	shareName.setText("Share" + (int)(Math.random() * 250));
	shareName.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		shareName.selectAll();
	    }
	});
	
	JLabel lblUserName = new JLabel("User Name");
	userName = new JTextField();
	userName.setColumns(5);
	userName.setText("User" + (int)(Math.random() * 250));
	userName.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		userName.selectAll();
	    }
	});
	JLabel lblPort = new JLabel("Port");
	connectionPort = new JTextField();
	connectionPort.setText("33334");
	connectionPort.setColumns(5);
	connectionPort.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		connectionPort.selectAll();
	    }
	});

	selectWhole = new JRadioButton();
	selectWhole.setText("Export whole spreadsheet");

	selectSelected = new JRadioButton();
	selectSelected.setText("Export selected cells");
	selectSelected.setSelected(true);

	group.add(selectWhole);
	group.add(selectSelected);

	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	getContentPane().add(panel, BorderLayout.SOUTH);

	buttonAccept = new JButton();
	buttonAccept.setText("Share");
	buttonAccept.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		/**
		 * true for whole , false for selected
		 */
		try {
		    boolean choice = true;
		    if (ShareOptionsDialog.this.selectSelected.isSelected()) {
			choice = false;
		    } else if (ShareOptionsDialog.this.selectWhole.isSelected()) {
			choice = true;
		    }
		    int port = Integer
			    .valueOf(ShareOptionsDialog.this.connectionPort
				    .getText());
		    String userName = ShareOptionsDialog.this.userName
			    .getText();
		    String shareName = ShareOptionsDialog.this.shareName
			    .getText();
		    ShareOptionsDialog.this.listener.onChoosedExport(choice,
			    userName, shareName, port);
		    ShareOptionsDialog.this.setVisible(false);
		} catch (NumberFormatException e) {
		    JOptionPane.showMessageDialog(getParent(),
			    "The port is supposed to be a number.",
			    "Can't connect", JOptionPane.ERROR_MESSAGE);
		}
	    }

	});

	buttonCancel = new JButton();
	buttonCancel.setText("Cancel");
	buttonCancel.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		ShareOptionsDialog.this.setVisible(false);
	    }
	});
	GroupLayout gl_panel = new GroupLayout(infoPanel);
	gl_panel.setHorizontalGroup(gl_panel
		.createParallelGroup(Alignment.LEADING)
		.addGroup(
			gl_panel.createSequentialGroup()
				.addGroup(
					gl_panel.createParallelGroup(
						Alignment.LEADING)
						.addGroup(
							gl_panel.createSequentialGroup()
								.addGap(5)
								.addComponent(
									lblShareName))
						.addGroup(
							gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(
									gl_panel.createParallelGroup(
										Alignment.LEADING)
										.addGroup(
											gl_panel.createSequentialGroup()
												.addGap(5)
												.addComponent(
													lblUserName))
										.addComponent(
											shareName,
											GroupLayout.DEFAULT_SIZE,
											25,
											Short.MAX_VALUE)))
						.addGroup(
							gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(
									gl_panel.createParallelGroup(
										Alignment.LEADING)
										.addGroup(
											gl_panel.createSequentialGroup()
												.addGap(5)
												.addComponent(
													lblPort))
										.addComponent(
											userName,
											GroupLayout.DEFAULT_SIZE,
											25,
											Short.MAX_VALUE)))
						.addGroup(
							Alignment.TRAILING,
							gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(
									connectionPort,
									GroupLayout.DEFAULT_SIZE,
									25,
									Short.MAX_VALUE)))
				.addContainerGap()));
	gl_panel.setVerticalGroup(gl_panel.createParallelGroup(
		Alignment.LEADING).addGroup(
		gl_panel.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblShareName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(shareName, GroupLayout.PREFERRED_SIZE,
				GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblUserName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(userName, GroupLayout.PREFERRED_SIZE,
				GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblPort)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(connectionPort, GroupLayout.PREFERRED_SIZE,
				GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE)
			.addContainerGap(GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE)));

	infoPanel.setLayout(gl_panel);
	panel.add(buttonAccept);
	panel.add(buttonCancel);
	add(selectSelected);
	add(selectWhole);
	infoPanel.add(lblShareName);
	infoPanel.add(shareName);
	infoPanel.add(lblUserName);
	infoPanel.add(userName);
	infoPanel.add(lblPort);
	infoPanel.add(connectionPort);
	add(infoPanel);
	add(panel);
	pack();
	setLocationRelativeTo(null);
    }

    public void setHasInterestingSelection(boolean has) {
	if (has) {
	    selectSelected.setSelected(true);
	    selectSelected.setEnabled(true);
	} else {
	    selectWhole.setSelected(true);
	    selectSelected.setEnabled(false);
	}
    }

    public void setOnChooseExportListener(OnChooseExportListener listener) {
	this.listener = listener;
    }
}