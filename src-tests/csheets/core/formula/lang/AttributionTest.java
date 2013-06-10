package csheets.core.formula.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;

/**
 * This class tests Attribution Expressions
 *
 * @author Filipe Silva & Gil Castro (gil_1110484)
 */
public class AttributionTest {

    /**
     * Test of applyTo method, of class Attribution.
     */
    @Test
    public void testApplyTo() throws Exception {
        // new workbook
        Workbook workbook = new Workbook(1); 
        // new spreadsheet
        Spreadsheet spreadsheet = workbook.getSpreadsheet(0);
        // assigns the formula to the cell A1
        spreadsheet.getCell(0, 0).setContent("#A2:=3+4");
        // expected result for both cells
        String expResult = "7";
        // the actual result of the A1 cell
        String resultCurrentCell = spreadsheet.getCell(0, 0).getValue().toString();
        // the actual result of the A2 cell
        String resultSecondaryCell = spreadsheet.getCell(0, 1).getContent();
        // comparisons
        assertEquals(expResult, resultCurrentCell);
        assertEquals(expResult, resultSecondaryCell);
    }

    /**
     * Test of getIdentifier method, of class Attribution.
     */
    @Test
    public void testGetIdentifier() {
        Attribution instance = new Attribution();
        String expResult = ":=";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Attribution.
     */
    @Test
    public void testToString() {
        Attribution instance = new Attribution();
        String expResult = ":=";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
}