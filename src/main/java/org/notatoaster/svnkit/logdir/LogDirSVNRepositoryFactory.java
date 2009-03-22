package org.notatoaster.svnkit.logdir;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNSession;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class LogDirSVNRepositoryFactory extends SVNRepositoryFactory {

	public static final String PROTOCOL_NAME = "logdir";
	
	protected SVNRepository createRepositoryImpl(SVNURL url, ISVNSession session) {
		return new LogDirSVNRepository(url, session);
	}

	public static void setup() {
		SVNRepositoryFactory.registerRepositoryFactory("^" + PROTOCOL_NAME + "://.*$", new LogDirSVNRepositoryFactory());
		SVNURL.registerProtocol(PROTOCOL_NAME, 0);
	}

}
