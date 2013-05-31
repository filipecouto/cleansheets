package csheets.ext.rtc.ui;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import csheets.ext.rtc.ClientInfo;

public class ClientInfoItem extends JPanel {

    public ClientInfoItem(ClientInfo info, JLabel label) {
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	System.out
		.println("ClientInfoListRenderer.getListCellRendererComponent("
			+ info + ")");
	// label
	label.setName(info.getName());
	// color square
	JLabel coloredLabel = new JLabel();
	coloredLabel.setBackground(info.getColor());
	coloredLabel.setSize(24, 24);
	add(coloredLabel);
	add(label);
    }
}
