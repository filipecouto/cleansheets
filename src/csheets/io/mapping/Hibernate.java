package csheets.io.mapping;

import org.h2.command.ddl.DeallocateProcedure;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;


/**
 * 
 * @author Filipe Couto (Filipe_1110688)
 *
 */


public class Hibernate {
    
    private static final SessionFactory sessionFactory;
    
    static {
	try {
	    sessionFactory = new AnnotationConfiguration().configure("\\hibernate.cfg.xml").buildSessionFactory();
	} catch (Throwable e) {
	    throw new ExceptionInInitializerError(e);
	}
    }
    
    public static SessionFactory getSessionFactory() {
	return sessionFactory;
    }
}
