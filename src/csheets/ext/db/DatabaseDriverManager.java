package csheets.ext.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import csheets.CleanSheets;
import csheets.ext.ExtensionManager;

public class DatabaseDriverManager {
    private static DatabaseDriverManager INSTANCE;

    private static final String DRIVERS_FILENAME = "drivers.props";

    private List<DatabaseExportInterface> drivers;

    /**
     * Creates an instance if it's null otherwise just returns it
     * @return the instance of the DatabaseDriverManager
     */
    public static DatabaseDriverManager getInstance() {
	if (INSTANCE == null)
	    INSTANCE = new DatabaseDriverManager();
	return INSTANCE;
    }

    /**
     * Default constructor for the DriverManager
     */
    private DatabaseDriverManager() {
	drivers = new ArrayList<DatabaseExportInterface>();

	Properties driversList = new Properties();
	InputStream stream = CleanSheets.class
		.getResourceAsStream("ext/db/res/" + DRIVERS_FILENAME);
	if (stream != null) {
	    try {
		driversList.load(stream);
	    } catch (IOException e) {
		System.err.println("Could not load available drivers from: "
			+ DRIVERS_FILENAME);
	    } finally {
		try {
		    if (stream != null)
			stream.close();
		} catch (IOException e) {
		}
	    }
	}

	for (Map.Entry<Object, Object> driver : driversList.entrySet()) {
	    String classPathProp = (String) driver.getValue();
	    URL classPath = null;
	    if (classPathProp.length() > 0) {
		// Looks for resource
		classPath = ExtensionManager.class.getResource(classPathProp);
		if (classPath == null) {
		    // Looks for file
		    File classPathFile = new File(classPathProp);
		    if (classPathFile.exists())
			try {
			    classPath = classPathFile.toURL();
			} catch (MalformedURLException e) {
			}
		}
	    }

	    String className = (String) driver.getKey();
	    if (classPath == null)
		load(className);
	    // else
	    // load(className, classPath);
	}
    }
    
    /**
     * @return a list of the supported drivers
     */

    public List<DatabaseExportInterface> getAvailableDrivers() {
	return drivers;
    }
    
    /**
     * loads extension class and detects if anything goes wrong
     * @param className the name of the class
     */
    private void load(String className) {
	try {
	    Class extensionClass = Class.forName(className);
	    load(extensionClass);
	} catch (Exception e) {
	    System.err.println("Failed to load extension class " + className
		    + ".");
	}
    }
    
    /**
     * loads extension class and detects if anything goes wrong
     * @param extensionClass the class
     */

    public void load(Class extensionClass) {
	try {
	    DatabaseExportInterface driver = (DatabaseExportInterface) extensionClass
		    .newInstance();
	    drivers.add(driver);
	} catch (IllegalAccessException iae) {
	    System.err.println("Could not access extension "
		    + extensionClass.getName() + ".");
	} catch (InstantiationException ie) {
	    System.err.println("Could not load extension from "
		    + extensionClass.getName() + ".");
	    ie.printStackTrace();
	}
    }
}
