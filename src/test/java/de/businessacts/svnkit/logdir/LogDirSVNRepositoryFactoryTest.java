package de.businessacts.svnkit.logdir;

import junit.framework.TestCase;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

public class LogDirSVNRepositoryFactoryTest extends TestCase 
{

	public void setUp() {
		LogDirSVNRepositoryFactory.setup();
	}
	
	public void testCreate() throws SVNException {
		SVNRepository repo = LogDirSVNRepositoryFactory.create(SVNURL.parseURIDecoded("logdir:///ignored"));
		assertNotNull(repo);
	}
	
}
