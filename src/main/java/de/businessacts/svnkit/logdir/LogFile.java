package de.businessacts.svnkit.logdir;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class LogFile {

	private File file;

	private static final String LOG_ENTRY_XPATH = "/log/logentry[@revision={0,number,0.######}]";

	public LogFile(File file) throws IOException, DocumentException {
		if (file == null)
			throw new IllegalArgumentException("file must not be null: " + file);
		if (!file.exists())
			throw new IllegalArgumentException("file must exist: " + file);
		if (file.isDirectory())
			throw new IllegalArgumentException("file must not be a directory: " + file);
		if (!file.canRead())
			throw new IllegalArgumentException("file must be readable: " + file);
		this.file = file;
	}

	public SVNLogEntry getLogEntry(long rev) throws MalformedURLException, DocumentException, ParseException,
			DatatypeConfigurationException {
		SAXReader reader = new SAXReader(DocumentFactory.getInstance());
		Document doc = reader.read(file);
		Node node = doc.selectSingleNode(MessageFormat.format(LOG_ENTRY_XPATH, new Object[] { new Long(rev) }));
		if (node != null) {
			Element logEntryElement = (Element) node;
			String author = logEntryElement.elementText("author");
			String dateString = logEntryElement.elementText("date");
			String message = logEntryElement.elementText("msg");
			List pathEntries = logEntryElement.selectNodes("paths/path");
			HashMap changedPaths = new HashMap();
			for (int i = 0; i < pathEntries.size(); i++) {
				Element e = (Element) pathEntries.get(i);
				char action = e.attributeValue("action").charAt(0);
				String path = e.getTextTrim();
				changedPaths.put(path, new SVNLogEntryPath(path, action, null, -1));
			}
			Date date = new Date(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString)
					.toGregorianCalendar().getTimeInMillis());
			// Date date = new
			// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").parse(dateString);
			return new SVNLogEntry(changedPaths, rev, author, date, message);
		} else {
			return null;
		}
	}

}
