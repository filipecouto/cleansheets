/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csheets.io;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.ext.style.StylableCell;
import csheets.ext.style.StyleExtension;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.swing.text.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A codec for xml files.
 * @author Filipe Silva & Rita Nogueira
 */
public class XMLCodec implements Codec{

    @Override
    public Workbook read(InputStream stream) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(Workbook workbook, OutputStream stream) throws IOException {
        System.out.println("Writing!");
        // Wraps stream
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(stream)));

        // Writes content of rows
        Spreadsheet sheet = workbook.getSpreadsheet(0);
        int textAlign, cellAlign;
        String textAlignString, cellAlignString, styleBold="False",styleItalic="False";
        for (int row = 0; row < sheet.getRowCount(); row++) {
                for (int column = 0; column < sheet.getColumnCount(); column++)
                        if (column + 1 < sheet.getColumnCount()){
                            StylableCell stylableCell = (StylableCell)sheet.getCell(column, row).getExtension(StyleExtension.NAME);
                            textAlign=stylableCell.getHorizontalAlignment();
                            switch(textAlign){
                                case 0: textAlignString="Center"; break;
                                case 2: textAlignString="Left"; break;
                                case 4: textAlignString="Right"; break;
                                default: textAlignString="Left"; break;
                            }
                            cellAlign=stylableCell.getVerticalAlignment();
                            switch(cellAlign){
                                case 0: cellAlignString="Center"; break;
                                case 1: cellAlignString="Top"; break;
                                case 3: cellAlignString="Bottom"; break;
                                default: cellAlignString="Center"; break;
                            }
                            if(stylableCell.getFont().isPlain()){
                                styleBold="False";
                                styleItalic="False";
                            }
                            else if(stylableCell.getFont().isBold() && stylableCell.getFont().isItalic()){
                                styleBold="True";
                                styleItalic="True";
                            }
                            else if(stylableCell.getFont().isBold()){
                                styleBold="True";
                                styleItalic="False";
                            }
                            else if(stylableCell.getFont().isItalic()){
                                styleBold="False";
                                styleItalic="True";
                            }
                            writer.print("<Cell Column=" + column
                            + "Row=" + row
                            + "BackgroundColor=" + stylableCell.getBackgroundColor().getRGB()
                            + "BorderTop=" + stylableCell.getBorder().toString()
                            + "BorderBottom=" + "BA"
                            + "BorderLeft=" + "BA"
                            + "BorderRight=" + "BA"
                            + "BorderColor=" + "BA"
                            + "TextAlign=" + textAlignString
                            + "CellAlign=" + cellAlignString
                            + "ForegroundColor=" + stylableCell.getForegroundColor().getRGB()
                            + "FontFamily=" + stylableCell.getFont().getFamily()
                            + "FontSize=" + stylableCell.getFont().getSize()
                            + "Bold=" + styleBold
                            + "Italic=" + styleItalic
                            + "Underline=" + "False" 
                            + ">" + sheet.getCell(column, row).getContent());
                        }
                if (row + 1 < sheet.getRowCount())
                        writer.println();
        }


        // Frees resources
        writer.close();
        stream.close();
        System.out.println("Done!");
    }
    
}
