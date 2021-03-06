package csheets.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import csheets.core.Workbook;
import csheets.io.mapping.Hibernate;
import csheets.io.mapping.MappedWorkbook;
import csheets.io.versioning.VersionControllerCodec;
import csheets.io.versioning.VersionInfo;

/**
 * A codec for read and write XML files.
 * 
 * @author Filipe Silva & Rita Nogueira & Filipe Couto & Gil Castro
 *         (gil_1110484)
 */
public class XMLCodec implements Codec, VersionControllerCodec {

	/**
	 * Creates a new XML codec.
	 */
	public XMLCodec() {
	}

	private Session getSession() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Hibernate.getSession();
	}

	@Override
	public Workbook read(InputStream stream) {
		try {
			Session session = getSession();

			Connection jdbcConnection = DriverManager.getConnection(
					"jdbc:hsqldb:mem:DBNAME", "sa", "");
			IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

			// script
			PreparedStatement preparedStatement;
			preparedStatement = jdbcConnection
					.prepareStatement("drop table Cell if exists");
			preparedStatement.execute();
			preparedStatement = jdbcConnection
					.prepareStatement("drop table Spreadsheet if exists");
			preparedStatement.execute();
			preparedStatement = jdbcConnection
					.prepareStatement("drop table Workbook if exists");
			preparedStatement.execute();
			preparedStatement = jdbcConnection
					.prepareStatement("create table Cell ("
							+ "id integer generated by default as identity (start with 1), "
							+ "address varbinary(255), "
							+ "backgroundColor varbinary(255), "
							+ "content varchar(255), " + "font varbinary(255), "
							+ "foregroundColor varbinary(255), "
							+ "horizontalAlignment integer not null, "
							+ "verticalAlignment integer not null, "
							+ "spreadsheet_id integer not null, "
							+ "primary key (id))");
			preparedStatement.execute();
			preparedStatement = jdbcConnection
					.prepareStatement("create table Spreadsheet (id integer generated by default as identity (start with 1), title varchar(255), workbook_id integer not null, primary key (id))");
			preparedStatement.execute();
			preparedStatement = jdbcConnection
					.prepareStatement("create table Workbook (id integer generated by default as identity (start with 1), version timestamp, primary key (id))");
			preparedStatement.execute();

			connection.getConfig().setProperty(
					DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, false);
			connection.getConfig().setProperty(
					DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

			loadStreamIntoDatabase(stream, connection);

			preparedStatement = jdbcConnection
					.prepareStatement("alter table Cell add constraint FK_41d8uvfegxcnvxrbqoi1cwjjs foreign key (spreadsheet_id) references Spreadsheet");
			preparedStatement.execute();
			preparedStatement = jdbcConnection
					.prepareStatement("alter table Spreadsheet add constraint FK_bdm961fgbyp7vku1dybsvajat foreign key (workbook_id) references Workbook");
			preparedStatement.execute();

			// get the latest version from the database
			Query query = session.createQuery(
					"from Workbook order by version desc").setMaxResults(1);

			final Workbook workbook = ((MappedWorkbook) query.uniqueResult())
					.makeWorkbook();
			workbook.setVersionController(this);
			return workbook;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void clearDatabase() {
		Session session = getSession();

		Transaction transaction = session.beginTransaction();
		session.createQuery("DELETE FROM Cell").executeUpdate();
		session.createQuery("DELETE FROM Spreadsheet").executeUpdate();
		session.createQuery("DELETE FROM Workbook").executeUpdate();
		transaction.commit();
	}

	private void loadStreamIntoDatabase(InputStream stream,
			IDatabaseConnection connection) throws DataSetException,
			DatabaseUnitException, SQLException {
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setCaseSensitiveTableNames(false);
		builder.setColumnSensing(true);
		builder.setDtdMetadata(false);
		IDataSet ds = builder.build(stream);
		DatabaseOperation.CLEAN_INSERT.execute(connection, ds);
	}

	@Override
	public void write(Workbook workbook, OutputStream stream)
			throws IOException, TransformerException, ParserConfigurationException {
		saveVersion(null, workbook);

		workbook.setVersionController(this);

		// export this database to an XML file through the stream
		try {
			Connection jdbcConnection = DriverManager.getConnection(
					"jdbc:hsqldb:mem:DBNAME", "sa", "");
			IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
			IDataSet fullSet = connection.createDataSet();
			FlatXmlDataSet.write(fullSet, stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MappedWorkbook getVersion(int versionId) {
		Session session = getSession();
		return (MappedWorkbook) session.get(MappedWorkbook.class, versionId);
	}

	@Override
	public Workbook loadVersion(Object versionId, Workbook target) {
		final MappedWorkbook mappedWorkbook = getVersion((Integer) versionId);
		final Workbook workbook = target == null ? mappedWorkbook.makeWorkbook()
				: mappedWorkbook.makeWorkbook(target);
		workbook.setVersionController(this);
		return workbook;
	}

	@Override
	public void saveVersion(VersionInfo version, Workbook book) {
		Session session = getSession();

		// add this new version to the database
		Transaction transaction = session.beginTransaction();
		session.persist(new MappedWorkbook(book));
		transaction.commit();
	}

	@Override
	public List<VersionInfo> getVersions() {
		try {
			Session session = getSession();
			List<VersionInfo> result = new ArrayList<VersionInfo>();
			List<?> queryResult = session
					.createQuery(
							"SELECT book.id, book.version, count(sheet.id) FROM Workbook book, Spreadsheet sheet "
									+ "WHERE book.id=sheet.workbook GROUP BY book.id ORDER BY version DESC")
					.list();
			for (Object item : queryResult) {
				Object[] version = (Object[]) item;
				result.add(new VersionInfo(version[0], null,
						(Timestamp) version[1], (int) (long) (Long) version[2], this));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void removeVersion(Object versionId) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		session.delete(getVersion((Integer) versionId));
		transaction.commit();
	}
}
