package csheets.io.mapping;

import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * 
 * @author Filipe Couto (Filipe_1110688)
 * 
 */

@SuppressWarnings("deprecation")
public class Hibernate {
	private static final Session session;

	static {
		try {
			session = new AnnotationConfiguration().configure("hibernate.cfg.xml")
					.buildSessionFactory().openSession();
		} catch (Throwable e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static Session getSession() {
		return session;
	}
}
