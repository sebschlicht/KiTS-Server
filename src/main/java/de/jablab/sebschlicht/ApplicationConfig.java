package de.jablab.sebschlicht;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for application configuration classes that can be loaded from
 * property files.
 *
 * @author sebschlicht
 *
 */
public abstract class ApplicationConfig {

    /**
     * generated UID
     */
    public static final long serialVersionUID = 8051169162112790889L;

    private final static Logger LOG =
            LoggerFactory.getLogger(ApplicationConfig.class);

    /**
     * Creates an (empty) application configuration.<br>
     * If you want to immediately load its field from any supported resource,
     * use the respective constructor instead.<br>
     * If you create an empty application configuration, you can load its fields
     * via one of the <code>loadFrom*</code> methods.
     */
    public ApplicationConfig() {
    }

    /**
     * Creates an application configuration from a file. (convenience function
     * to )
     *
     * @param file
     *            application configuration file
     * @throws FileNotFoundException
     *             if the file was not found
     * @throws IOException
     *             if the file is inaccessible
     */
    public ApplicationConfig(
            File file) throws IOException {
        loadFromFile(file);
    }

    /**
     * Creates an application configuration from a resource.
     *
     * @param name
     *            name of the application configuration resource
     * @throws IllegalArgumentException
     *             if the resource was not found
     * @throws IOException
     *             if the resource is inaccessible
     */
    public ApplicationConfig(
            String name) throws IOException {
        loadFromResource(name);
    }

    /**
     * Loads the application configuration from a file.
     *
     * @param file
     *            application configuration file
     * @throws IOException
     *             if the file is inaccessible
     */
    public void loadFromFile(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        loadFromInputStream(inputStream);
    }

    /**
     * Loads the application configuration from a resource.
     *
     * @param resourceName
     *            name of the application configuration resource
     * @throws IllegalArgumentException
     *             if the resource was not found
     * @throws IOException
     *             if the resource is inaccessible
     */
    public void loadFromResource(String name) throws IOException {
        InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream(name);
        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "Could not find resource \"" + name + "\"!");
        }
        loadFromInputStream(inputStream);
    }

    /**
     * Loads the application configuration from an input stream.
     *
     * @param inputStream
     *            application configuration input stream
     * @throws IOException
     *             if the stream is inaccessible
     */
    public void loadFromInputStream(InputStream inputStream)
            throws IOException {
        Properties prop = new Properties();
        prop.load(inputStream);

        LOG.trace(getClass().getDeclaredFields().length
                + " configuration fields found.");
        for (Field f : getClass().getDeclaredFields()) {
            // allow fields to be private (pure configuration model)
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }

            Class<?> type = f.getType();
            LOG.trace("loading field \"" + f.getName() + "\" of type \""
                    + type.getName() + "\"");

            String value = prop.getProperty(f.getName());
            if (value == null) {
                LOG.warn("field \"" + f.getName() + "\" is missing.");
                continue;
            }

            try {
                if (type == String.class) {
                    f.set(this, value);
                } else if (type == File.class) {
                    f.set(this, new File(value));
                } else {
                    // unknown field type
                    throw new IllegalArgumentException("Type \""
                            + type.getName() + "\" is not supported!");
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Failed to set field \"" + f.getName() + "\"!", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Failed to set field \"" + f.getName() + "\"!", e);
            }
        }
    }
}
