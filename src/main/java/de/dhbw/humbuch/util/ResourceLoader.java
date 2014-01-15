package de.dhbw.humbuch.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoader {
	private final static Logger LOG = LoggerFactory
			.getLogger(ResourceLoader.class);

	private String name = "";

	/**
	 * Sets the name of the resource to load
	 * 
	 * @param name
	 *            filename of the resource including the relative or absolute
	 *            path
	 */
	public ResourceLoader(String name) {
		this.name = name;
	}

	/**
	 * Provides a stream to the specified resource
	 * 
	 * @return {@link InputStream} to the resource
	 */
	public InputStream getStream() {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		return classLoader.getResourceAsStream(name);
	}

	/**
	 * Read content from the specified file
	 * 
	 * @return content of the file as {@link String} if no error occurred,
	 *         otherwise an empty {@link String}
	 */
	public String getContent() {
		StringBuilder stringBuilder = new StringBuilder();

		if (getStream() == null) {
			LOG.error("stream to file '" + name + "' is null");
		} else {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(getStream()));

			try {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}
				return stringBuilder.toString();
			} catch (IOException ex) {
				LOG.error("couldn't read " + name);
			}
		}

		return null;
	}

	/**
	 * Load properties from the specified file
	 * 
	 * @return {@link Properties}
	 */
	public Properties loadProperties() {
		Properties properties = new Properties();
		try {
			properties.load(getStream());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return properties;
	}
}
