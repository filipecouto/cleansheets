package csheets.ext.db;

public class DatabaseExportHelper {

    public static String PrepareColumnName(String adaptee, int position) {
	for (int i = 0; i < adaptee.length(); i++) {
	    char at = adaptee.charAt(i);
	    if (!((at >= 'A' && at <= 'Z') || (at >= '0' && at <= '9') || (at >= 'a' && at <= 'z'))) {
		return "Column" + (position + 1);
	    }
	}
	return adaptee;
    }
    
}
