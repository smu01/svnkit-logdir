package de.businessacts.svnkit.logdir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFilter;
import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNMergeInfoInheritance;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNFileRevisionHandler;
import org.tmatesoft.svn.core.io.ISVNLocationEntryHandler;
import org.tmatesoft.svn.core.io.ISVNLocationSegmentHandler;
import org.tmatesoft.svn.core.io.ISVNLockHandler;
import org.tmatesoft.svn.core.io.ISVNReplayHandler;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.ISVNSession;
import org.tmatesoft.svn.core.io.ISVNWorkspaceMediator;
import org.tmatesoft.svn.core.io.SVNCapability;
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

	private SVNLogEntry getLogEntry(long revision) {
		File file = getLogEntryFile(revision);
		try {
			// log.warn("getting rev " + revision + " with filename " + file.getPath());
			return new LogFile(file).getLogEntry(revision);
		} catch (Exception e) {
			e.printStackTrace(); // TODO: error handling
			return null;
		}
	}

	public File getLogEntryFile(long revision) {
		String filename = buildFileName(revision);
		File file = new File(directory, filename);
		return file;
	}

	private String buildFileName(long revision) {
		// return MessageFormat.format(FILENAME_FORMAT, new Object[] { new Long(revision) });
		return FILENAME_PREFIX + revision + FILENAME_SUFFIX;
	}

	@Override
	public long getLatestRevision() {
//		long rev = myRev + 1;
//		if (logFileExists(rev)) { // new files available
//			// will perform badly with large (i.e., many commits) repositories:
//			do {
//				rev++;
//			} while (logFileExists(rev));
//			myRev = rev - 1;
//		}
//		log.info("latest revision is: " + myRev);
//		return myRev;
		Collection files = FileUtils.listFiles(directory, new WildcardFilter(FILENAME_PREFIX + "*" + FILENAME_SUFFIX), null);
		Iterator iter = files.iterator();
		while(iter.hasNext()) {
			File file = (File) iter.next();
			try {
				long fileRev = getRevisionFromFilename(file.getName());
				if(fileRev>myRev) {
					myRev = fileRev;
				}
			} catch (Exception ignored) {
			}
		}
		return myRev;
	}

	private boolean logFileExists(long rev) {
		return getLogEntryFile(rev).canRead();
	}
	
	private long getRevisionFromFilename(String filename) throws Exception {
		if(filename==null) throw new Exception("filename must not be null"); // TODO: better exception type
		
		if(!filename.startsWith(FILENAME_PREFIX) || !filename.endsWith(FILENAME_SUFFIX)) {
			throw new Exception("filename does not match expected format");
		}
		
		String numPart = filename.substring(FILENAME_PREFIX.length(), filename.length()-FILENAME_SUFFIX.length());
		return Long.parseLong(numPart);
	}

	@Override
	public void testConnection() throws SVNException {
		if (directory == null)
			throw new SVNException(SVNErrorMessage.UNKNOWN_ERROR_MESSAGE);
		if (!directory.isDirectory())
			throw new SVNException(SVNErrorMessage.UNKNOWN_ERROR_MESSAGE);
		if (!directory.canRead())
			throw new SVNException(SVNErrorMessage.UNKNOWN_ERROR_MESSAGE);
	}

	@Override
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
	
	@Override
	public SVNNodeKind checkPath(String path, long revision)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void closeSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void diff(SVNURL url, long targetRevision, long revision,
			String target, boolean ignoreAncestry, SVNDepth depth,
			boolean getContents, ISVNReporterBaton reporter, ISVNEditor editor)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISVNEditor getCommitEditor(String logMessage, Map locks,
			boolean keepLocks, ISVNWorkspaceMediator mediator)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected ISVNEditor getCommitEditorInternal(Map locks, boolean keepLocks,
			SVNProperties revProps, ISVNWorkspaceMediator mediator)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getDatedRevision(Date date) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getDir(String path, long revision, SVNProperties properties,
			ISVNDirEntryHandler handler) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVNDirEntry getDir(String path, long revision,
			boolean includeCommitMessages, Collection entries)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getDir(String path, long revision, SVNProperties properties,
			int entryFields, ISVNDirEntryHandler handler) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getFile(String path, long revision, SVNProperties properties,
			OutputStream contents) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int getFileRevisionsImpl(String path, long startRevision,
			long endRevision, boolean includeMergedRevisions,
			ISVNFileRevisionHandler handler) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected long getLocationSegmentsImpl(String path, long pegRevision,
			long startRevision, long endRevision,
			ISVNLocationSegmentHandler handler) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int getLocationsImpl(String path, long pegRevision,
			long[] revisions, ISVNLocationEntryHandler handler)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVNLock getLock(String path) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVNLock[] getLocks(String path) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Map getMergeInfoImpl(String[] paths, long revision,
			SVNMergeInfoInheritance inherit, boolean includeDescendants)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVNProperties getRevisionProperties(long revision,
			SVNProperties properties) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVNPropertyValue getRevisionPropertyValue(long revision,
			String propertyName) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasCapability(SVNCapability capability) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVNDirEntry info(String path, long revision) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lock(Map pathsToRevisions, String comment, boolean force,
			ISVNLockHandler handler) throws SVNException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	protected long logImpl(String[] targetPaths, long startRevision,
			long endRevision, boolean changedPath, boolean strictNode,
			long limit, boolean includeMergedRevisions,
			String[] revisionProperties, ISVNLogEntryHandler handler)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void replay(long lowRevision, long revision, boolean sendDeltas,
			ISVNEditor editor) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void replayRangeImpl(long startRevision, long endRevision,
			long lowRevision, boolean sendDeltas, ISVNReplayHandler handler)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRevisionPropertyValue(long revision, String propertyName,
			SVNPropertyValue propertyValue) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void status(long revision, String target, SVNDepth depth,
			ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unlock(Map pathToTokens, boolean force, ISVNLockHandler handler)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(SVNURL url, long revision, String target,
			SVNDepth depth, ISVNReporterBaton reporter, ISVNEditor editor)
			throws SVNException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(long revision, String target, SVNDepth depth,
			boolean sendCopyFromArgs, ISVNReporterBaton reporter,
			ISVNEditor editor) throws SVNException {
		throw new UnsupportedOperationException();
	}

}
