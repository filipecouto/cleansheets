package csheets.core.formula.lang;

import csheets.core.Spreadsheet;
import csheets.core.Value;
import csheets.core.Workbook;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Filipe Silva
 */
public class AttributionTest {

    /**
     * Test of applyTo method, of class Attribution.
     */
    @Test
    public void testApplyTo() throws Exception {
        System.out.println("applyTo");
        // new workbook
        Workbook workbook = new Workbook(3); 
        // new spreadsheet
        Spreadsheet spreadsheet = workbook.getSpreadsheet(0);
        // assigns the formula to the cell A1
        spreadsheet.getCell(0, 0).setContent("#A2:=3+4");
        // expected result for the A1 cell
        String expResultCurrentCell = "7";
        // expected result for the A2 cell
        String expResultSecundarieCell = "7";
        // the actual result of the A1 cell
        String resultCurrentCell = spreadsheet.getCell(0, 0).getValue().toString();
        // the actual result of the A2 cell
        String resultSecundarieCell = spreadsheet.getCell(0, 1).getContent();
        // comparisons
        assertEquals(expResultCurrentCell, resultCurrentCell);
        assertEquals(expResultSecundarieCell, resultSecundarieCell);
    }

    /**
     * Test of getIdentifier method, of class Attribution.
     */
    @Test
    public void testGetIdentifier() {
        System.out.println("getIdentifier");
        Attribution instance = new Attribution();
        String expResult = ":=";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOperandValueType method, of class Attribution.
     */
    @Test
    public void testGetOperandValueType() {
        System.out.println("getOperandValueType");
        Attribution instance = new Attribution();
        Value.Type expResult = Value.Type.NUMERIC;
        Value.Type result = instance.getOperandValueType();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Attribution.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Attribution instance = new Attribution();
        String expResult = ":=";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
}