package csheets.ext.rtc.ui;

import csheets.ext.rtc.ClientInfo;

public interface DataListener {
    public void onSendData(ClientInfo info, String address);
}
