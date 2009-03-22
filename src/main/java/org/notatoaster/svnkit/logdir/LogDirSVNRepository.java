package org.notatoaster.svnkit.logdir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNFileRevisionHandler;
import org.tmatesoft.svn.core.io.ISVNLocationEntryHandler;
import org.tmatesoft.svn.core.io.ISVNLockHandler;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.ISVNSession;
import org.tmatesoft.svn.core.io.ISVNWorkspaceMediator;
import org.tmatesoft.svn.core.io.SVNRepository;

public class LogDirSVNRepository extends SVNRepository {

	private static Logger log = Logger.getLogger(LogDirSVNRepository.class);

	private File directory = null;

	private static final String FILENAME_PREFIX = "commit.r";

	private static final String FILENAME_SUFFIX = ".xml";

	private long myRev = 0;

	protected LogDirSVNRepository(SVNURL url, ISVNSession session) {
		super(url, session);
		this.directory = new File(url.getPath());
		log.warn("logdir is " + directory);
		File uuidFile = new File(directory, "uuid");
		if (uuidFile.isFile() && uuidFile.canRead()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(uuidFile));
				this.myRepositoryUUID = br.readLine();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public SVNLogEntry getLogEntry(long revision) {
		String filename = buildFileName(revision);
		File file = new File(directory, filename);
		try {
			// log.warn("getting rev " + revision + " with filename " + file.getPath());
			return new LogFile(file).getLogEntry(revision);
		} catch (Exception e) {
			e.printStackTrace(); // TODO: error handling
			return null;
		}
	}

	private String buildFileName(long revision) {
		// return MessageFormat.format(FILENAME_FORMAT, new Object[] { new Long(revision) });
		return FILENAME_PREFIX + revision + FILENAME_SUFFIX;
	}

	public long getLatestRevision() {
		long rev = myRev + 1;
		if (logFileExists(rev)) { // new files available
			// will perform badly with large (i.e., many commits) repositories:
			do {
				rev++;
			} while (logFileExists(rev));
			myRev = rev - 1;
		}
		log.info("latest revision is: " + myRev);
		return myRev;
	}

	private boolean logFileExists(long rev) {
		return new File(directory, buildFileName(rev)).canRead();
	}

	public void testConnection() throws SVNException {
		if (directory == null)
			throw new SVNException(SVNErrorMessage.UNKNOWN_ERROR_MESSAGE);
		if (!directory.isDirectory())
			throw new SVNException(SVNErrorMessage.UNKNOWN_ERROR_MESSAGE);
		if (!directory.canRead())
			throw new SVNException(SVNErrorMessage.UNKNOWN_ERROR_MESSAGE);
	}

	public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPaths,
			boolean strictNode, long limit, ISVNLogEntryHandler handler) throws SVNException {
		long result = 0;
		for (long rev = startRevision; rev <= endRevision; rev++) {
			result++;
			SVNLogEntry logEntry = getLogEntry(rev);
			if (logEntry != null) {
				handler.handleLogEntry(logEntry);
			}
		}
		return result;
	}

	// The following methods are not implemented and throw an UnsupportedOperationException!!!!

	public long getDatedRevision(Date date) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public Map getRevisionProperties(long revision, Map arg1) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void setRevisionPropertyValue(long arg0, String arg1, String arg2) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public String getRevisionPropertyValue(long revision, String propertyName) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public SVNNodeKind checkPath(String path, long arg1) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public long getFile(String arg0, long arg1, Map arg2, OutputStream arg3) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public long getDir(String arg0, long arg1, Map arg2, ISVNDirEntryHandler arg3) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public int getFileRevisions(String arg0, long arg1, long arg2, ISVNFileRevisionHandler arg3) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public int getLocations(String arg0, long arg1, long[] arg2, ISVNLocationEntryHandler arg3) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public SVNDirEntry getDir(String arg0, long arg1, boolean arg2, Collection arg3) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void diff(SVNURL arg0, long arg1, long arg2, String arg3, boolean arg4, boolean arg5,
			ISVNReporterBaton arg6, ISVNEditor arg7) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void diff(SVNURL arg0, long arg1, String arg2, boolean arg3, boolean arg4, ISVNReporterBaton arg5,
			ISVNEditor arg6) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void update(long arg0, String arg1, boolean arg2, ISVNReporterBaton arg3, ISVNEditor arg4)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void status(long arg0, String arg1, boolean arg2, ISVNReporterBaton arg3, ISVNEditor arg4)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void update(SVNURL arg0, long arg1, String arg2, boolean arg3, ISVNReporterBaton arg4, ISVNEditor arg5)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	public SVNDirEntry info(String arg0, long arg1) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public ISVNEditor getCommitEditor(String arg0, Map arg1, boolean arg2, ISVNWorkspaceMediator arg3)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	public SVNLock getLock(String arg0) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public SVNLock[] getLocks(String arg0) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void lock(Map arg0, String arg1, boolean arg2, ISVNLockHandler arg3) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void unlock(Map arg0, boolean arg1, ISVNLockHandler arg2) throws SVNException {
		throw new UnsupportedOperationException();
	}

	public void closeSession() {
		throw new UnsupportedOperationException();
	}

	public void diff(SVNURL arg0, long arg1, long arg2, String arg3, boolean arg4, boolean arg5, boolean arg6,
			ISVNReporterBaton arg7, ISVNEditor arg8) throws SVNException {
		throw new UnsupportedOperationException();		
	}

	public void replay(long arg0, long arg1, boolean arg2, ISVNEditor arg3) throws SVNException {
		throw new UnsupportedOperationException();
	}

}
