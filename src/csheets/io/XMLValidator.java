/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csheets.io;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import org.xml.sax.SAXException;
import java.io.*;

/**
 * 
 * @author Filipe Silva & Rita Nogueira
 */
public class XMLValidator {

    /**
     * Creates a new XML validator.
     */
    public XMLValidator() {
    }

/**
 * Method for validating XML file
 * @param stream
 * @throws Exception
 */
    public void validate(InputStream stream) throws Exception {
	Source schemaFile = new StreamSource(new File(
		"src/csheets/io/XMLSchema.xsd"));
	Source xmlFile = new StreamSource(stream);

	SchemaFactory schemaFactory = SchemaFactory
		.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	Schema schema = schemaFactory.newSchema(schemaFile);
	Validator validator = schema.newValidator();

	try {
	    validator.validate(xmlFile);
	    System.out.println(xmlFile.getSystemId() + " is valid");
	} catch (SAXException e) {
	    System.out.println(xmlFile.getSystemId() + " is NOT valid");
	    System.out.println("Reason: " + e.getLocalizedMessage());
	}
    }
}
