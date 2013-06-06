package csheets.io.mapping;

import java.awt.Color;
import java.awt.Font;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.swing.border.Border;

import csheets.core.Address;

@Entity(name = "Cell")
public class MappedCell {
	@Id
	private int id;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private MappedSpreadsheet spreadsheet;

	private Address address;

	private String content;

	private Color backgroundColor;
	private Border border;
	private Font font;
	private Color foregroundColor;
	private int horizontalAlignment;
	private int verticalAlignment;
}
