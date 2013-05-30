package csheets.ext.rtc.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import csheets.ext.rtc.ClientInfo;

public class ClientInfoListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
	    int index, boolean isSelected, boolean cellHasFocus) {
	ClientInfoItem item;
	JComponent label = (JComponent) super.getListCellRendererComponent(
		list, value, index, isSelected, cellHasFocus);
	if (value instanceof ClientInfo) {
	    ClientInfo info = (ClientInfo) value;
	    item = new ClientInfoItem(info, (JLabel) label);
	    return item;
	}
	return label;
    }

}
