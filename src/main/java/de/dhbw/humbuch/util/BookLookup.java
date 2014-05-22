package de.dhbw.humbuch.util;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Util class for looking up a book by its isbn
 * <ul>
 * <li>Standard ISBN API is isbndb.com</li>
 * <li>Document retrieval, validation and parsing can be overridden in subclass</li>
 * </ul>
 * 
 * @author davherrmann
 */
public class BookLookup {
	private final static String KEY = "CONBNUOZ";
	private final static String LOOKUP_URL = "http://isbndb.com/api/v2/xml/"
			+ KEY + "/book/";

	/**
	 * Look up a book by its ISBN
	 * 
	 * @param isbn
	 *            {@link String} containing the ISBN - all non-numerical
	 *            characters are ignored
	 * @return {@link Book} containing the book data
	 * @throws BookNotFoundException
	 *             when a book is not found
	 */
	public static Book lookup(String isbn) throws BookNotFoundException {
		Document document = retrieveDocument(buildLookupURL(processISBN(isbn)));
		validateDocument(document);
		return parseDocument(document);
	}

	/**
	 * Removes all non-numerical characters from the isbn
	 * 
	 * @param isbn
	 *            {@link String} containing the ISBN
	 * @return {@link String} without all non-numerical characters
	 */
	protected static String processISBN(String isbn) {
		return isbn.replaceAll("[^\\d]", "");
	}

	/**
	 * Builds the URI for the ISBN API
	 * 
	 * @param isbn
	 *            {@link String} containing the ISBN
	 * @return {@link String} containing the ISBN API URL
	 */
	protected static String buildLookupURL(String isbn) {
		return LOOKUP_URL + isbn;
	}

	/**
	 * Retrieve an document from a given URI
	 * 
	 * @param uri
	 *            {@link String} containing the URI
	 * @return {@link Document} retrieved from the URI
	 * @throws BookNotFoundException
	 *             thrown when an error occurs while retrieving the document
	 */
	protected static Document retrieveDocument(String uri)
			throws BookNotFoundException {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(uri);
			document.getDocumentElement().normalize();
			return document;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new BookNotFoundException(
					"Error during retrieving the XML document...", e);
		}
	}

	/**
	 * Checks if the document contains valid data for a book
	 * 
	 * @param document
	 *            {@link Document} to be validated
	 * @throws BookNotFoundException
	 *             thrown when the document contains no valid book data
	 */
	protected static void validateDocument(Document document)
			throws BookNotFoundException {
		Object error = getNodeValue(document, "error");
		if (error != null) {
			throw new BookNotFoundException("No book found...");
		}
	}

	/**
	 * Parse a document and extract the book data
	 * 
	 * @param document
	 *            {@link Document} containing the book data
	 * @return {@link Book} with the extracted data
	 */
	protected static Book parseDocument(Document document) {
		return new Book.Builder(getNodeValue(document, "title"))
				.author(getNodeValue(document, "name"))
				.isbn10(getNodeValue(document, "isbn10"))
				.isbn13(getNodeValue(document, "isbn13"))
				.publisher(getNodeValue(document, "publisher_name")).build();
	}

	/**
	 * Extract the value of the first node of an element
	 * 
	 * @param document
	 *            the {@link Document}
	 * @param elementName
	 *            name of the element of which the value should be extracted
	 * @return {@link String} containing the content of the element if it
	 *         exists, otherwise <code>null</code>
	 */
	private static String getNodeValue(Document document, String elementName) {
		NodeList data = document.getElementsByTagName(elementName);
		Element element = (Element) data.item(0);
		if (element != null) {
			Node node = element.getChildNodes().item(0);
			if (node != null) {
				return node.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Exception indicating a book was not found
	 */
	public static class BookNotFoundException extends Exception {
		private static final long serialVersionUID = -644882332116172763L;

		public BookNotFoundException() {
			super();
		}

		public BookNotFoundException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public BookNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}

		public BookNotFoundException(String message) {
			super(message);
		}

		public BookNotFoundException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * POJO holding information about the book
	 */
	public static class Book {
		public final String author;
		public final String isbn10;
		public final String isbn13;
		public final String publisher;
		public final String title;

		private Book(Builder builder) {
			this.author = builder.author;
			this.isbn10 = builder.isbn10;
			this.isbn13 = builder.isbn13;
			this.publisher = builder.publisher;
			this.title = builder.title;
		}

		public static class Builder {
			private String author;
			private String isbn10;
			private String isbn13;
			private String publisher;
			private String title;

			public Builder(String title) {
				this.title = title;
			}

			public Builder author(String author) {
				this.author = author;
				return this;
			}

			public Builder isbn10(String isbn10) {
				this.isbn10 = isbn10;
				return this;
			}

			public Builder isbn13(String isbn13) {
				this.isbn13 = isbn13;
				return this;
			}

			public Builder publisher(String publisher) {
				this.publisher = publisher;
				return this;
			}

			public Book build() {
				return new Book(this);
			}
		}
	}
}
