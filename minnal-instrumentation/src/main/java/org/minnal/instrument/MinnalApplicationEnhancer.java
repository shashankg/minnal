/**
 * 
 */
package org.minnal.instrument;

import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;

/**
 * @author ganeshs
 *
 */
public class MinnalApplicationEnhancer extends ApplicationEnhancer {

	/**
	 * @param application
	 * @param namingStrategy
	 */
	public MinnalApplicationEnhancer(Application<ApplicationConfiguration> application, NamingStrategy namingStrategy) {
		super(application.getResourceConfig(), namingStrategy, application.getConfiguration().getDatabaseConfiguration().getPackagesToScan().toArray(new String[0]));
	}

	/**
	 * @param resourceClass
	 */
	protected void addResource(Class<?> resourceClass) {
		((ResourceConfig) getApplication()).register(resourceClass);
	}
}
