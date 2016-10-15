package de.jablab.sebschlicht;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {

	/**
	 * generated UID
	 */
	private static final long serialVersionUID = 8051169162112790889L;

	private final static Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);

	public void loadFromResourcesFile(String resourceName) throws IOException {
		LOG.info("loading configuration \"" + getClass().getName() + "\" from \"" + resourceName + "\"");
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
		if (inputStream == null) {
			LOG.warn("Could not find resource \"" + resourceName + "\".");
			return;
		}
		Properties prop = new Properties();
		prop.load(inputStream);

		LOG.debug(getClass().getDeclaredFields().length + " configuration fields found.");
		for (Field f : getClass().getDeclaredFields()) {
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}

			Class<?> type = f.getType();
			LOG.debug("loading field \"" + f.getName() + "\" of type \"" + type.getName() + "\"");

			String value = prop.getProperty(f.getName());
			if (value == null) {
				LOG.warn("field \"" + f.getName() + "\" is missing.");
				continue;
			}

			try {
				if (type == String.class) {
					f.set(this, prop.getProperty(f.getName()));
				} else if (type == File.class) {
					f.set(this, new File(prop.getProperty(f.getName())));
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
