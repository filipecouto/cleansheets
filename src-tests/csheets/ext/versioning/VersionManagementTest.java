package csheets.ext.versioning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.io.XMLCodec;
import csheets.io.versioning.VersionInfo;

/**
 * <p>
 * This test class will test the following situations related to version
 * management:
 * </p>
 * <li>
 * <ul>
 * Counting the number of saved versions in a file (expected 3)
 * </ul>
 * <ul>
 * Checking a specific version (will check version 2, expects "2" in cell B1)
 * </ul>
 * <ul>
 * Reverting to a specific version (will revert to the first version, cell B1
 * should contain "1")
 * </ul>
 * <ul>
 * Deleting a specific version from the file (will delete the third version)
 * </ul>
 * </li>
 * <p>
 * This test will start by creating a file with three versions, cell B1 will
 * tell which version it is.
 * </p>
 * 
 * @author Gil Castro (gil_1110484)
 */
public class VersionManagementTest {
	private static Workbook book;

	@BeforeClass
	public static void setUp() throws Exception {
		// create workbook
		book = new Workbook(1);
		final Spreadsheet sheet = book.getSpreadsheet(0);

		File existing = new File("versiontest.xml");
		if (existing.exists()) {
			existing.delete();
		}

		// make first version
		sheet.getCell(0, 0).setContent("This is version:");
		sheet.getCell(1, 0).setContent("1");
		XMLCodec codec = new XMLCodec();
		codec.clearDatabase();

		// version 1
		codec.write(book, new FileOutputStream("versiontest.xml"));

		// version 2
		sheet.getCell(1, 0).setContent("2");
		VersioningController.addVersion(book, null, book);

		// version 3
		sheet.getCell(1, 0).setContent("3");
		VersioningController.addVersion(book, null, book);
	}

	@Test
	public void countAvailableVersions() {
		assertEquals(
				"Available versions count doesn't match the expected value.", 3,
				book.getVersionController().getVersions().size());
	}

	@Test
	public void testSecondVersion() {
		// get the second version
		VersionInfo versionInfo = getVersion(1);
		// build this version in a new Workbook instance
		Workbook secondVersion = versionInfo.loadVersion(new Workbook(1));

		assertEquals("Second version doesn't seem to be what was expected.", "2",
				secondVersion.getSpreadsheet(0).getCell(1, 0).getContent());
	}

	@Test
	public void revertToInitialVersion() {
		// get the first version
		VersionInfo versionInfo = getVersion(2);
		// build this version in a new Workbook instance and add it to the file
		VersioningController.addVersion(book, null,
				VersioningController.getVersion(new Workbook(), versionInfo));

		// get the last version (which is supposed to be like the first one)
		versionInfo = getVersion(0);
		// build this version in a new Workbook instance
		Workbook lastVersion = versionInfo.loadVersion(new Workbook(1));

		assertEquals("Reverted version doesn't seem to be what was expected.",
				"1", lastVersion.getSpreadsheet(0).getCell(1, 0).getContent());
	}

	@Test
	public void removeThirdVersion() {
		// get the third version
		VersionInfo versionInfo = getVersion(1);
		// build this version just to check if it is what we expect
		Workbook thirdVersion = versionInfo.loadVersion(new Workbook(1));

		assertEquals("Third version isn't even what it should.", "3",
				thirdVersion.getSpreadsheet(0).getCell(1, 0).getContent());

		// remove this version
		VersioningController.removeVersion(versionInfo);

		// get the new third version (previously fourth)
		versionInfo = getVersion(1);
		// build this new version to check if it isn't what we don't expect
		thirdVersion = versionInfo.loadVersion(new Workbook(1));

		assertNotSame("Third version seems to still be here.", "3", thirdVersion
				.getSpreadsheet(0).getCell(1, 0).getContent());
	}

	private VersionInfo getVersion(int index) {
		return book.getVersionController().getVersions().get(index);
	}

	@AfterClass
	public static void saveFile() throws Exception {
		((XMLCodec) book.getVersionController()).write(book,
				new FileOutputStream("versiontest.xml"));
	}
}
