package csheets.io.versioning;

import java.util.Date;

import csheets.core.Workbook;

public class VersionInfo {
	private Object id;
	private String name;
	private Date date;
	private int spreadsheetCount;

	private VersionControllerCodec codec;

	public VersionInfo(Object id, String name, Date date, int spreadsheetCount, VersionControllerCodec codec) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.spreadsheetCount = spreadsheetCount;
		this.codec = codec;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	public int getSpreadsheetCount() {
		return spreadsheetCount;
	}

	public Workbook loadVersion(Workbook target) {
		return codec.loadVersion(id, target);
	}
}
