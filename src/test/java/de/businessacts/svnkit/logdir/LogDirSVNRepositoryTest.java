package de.businessacts.svnkit.logdir;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

import junit.framework.TestCase;

public class LogDirSVNRepositoryTest extends TestCase 
{
	
	private String logdirRepositoryUri;
	private String directoryUri;

	protected void setUp() throws Exception {
		LogDirSVNRepositoryFactory.setup();
		
		File svnRepository;

        super.setUp();

        do {
            svnRepository = new File(SystemUtils.JAVA_IO_TMPDIR, "svnkit-logdir-local-svn-repo" + RandomStringUtils.randomAlphanumeric(16));
        } while (svnRepository.exists());

        File directory = svnRepository.getAbsoluteFile();
        directory.mkdir();
		String directoryPath = directory.toURI().getPath();
        directoryUri = new StringBuffer("file://").append(directoryPath).toString();
		logdirRepositoryUri = new StringBuffer("logdir://").append(directoryPath).toString();

	}
	
	protected void tearDown() throws IOException, URISyntaxException {
        File pathToLocalSvnRepository;

        pathToLocalSvnRepository = new File(new URI(directoryUri));
        if (pathToLocalSvnRepository.exists())
            FileUtils.deleteDirectory(pathToLocalSvnRepository);
    }
	
	public void testGetLatestRevisionOfEmptyRepository() throws SVNException {
		SVNRepository repo = LogDirSVNRepositoryFactory.create(SVNURL.parseURIDecoded(logdirRepositoryUri));
		long latestResvision = repo.getLatestRevision();
		assertEquals(0, latestResvision);
	}
	
	public void testGetLatestRevisionWithContinuousFiles() throws SVNException, IOException {
		LogDirSVNRepository repo = (LogDirSVNRepository) LogDirSVNRepositoryFactory.create(SVNURL.parseURIDecoded(logdirRepositoryUri));
		
		int maxRevision = 15;
		for(long i=1; i<=maxRevision;i++) {
			File commitLogFile = repo.getLogEntryFile(i);
			FileUtils.touch(commitLogFile);
		}
		
		long latestResvision = repo.getLatestRevision();
		assertEquals(maxRevision, latestResvision);
	}
	
	public void testGetLatestRevisionWithDiscontinuousFiles() throws SVNException, IOException {
		LogDirSVNRepository repo = (LogDirSVNRepository) LogDirSVNRepositoryFactory.create(SVNURL.parseURIDecoded(logdirRepositoryUri));
		
		int maxRevision = 15;
		for(long i=1; i<=maxRevision;i+=2) {
			File commitLogFile = repo.getLogEntryFile(i);
			FileUtils.touch(commitLogFile);
		}
		
		long latestResvision = repo.getLatestRevision();
		assertEquals(maxRevision, latestResvision);
	}
	
	public void testGetLatestRevisionTwiceWithoutChange() throws SVNException, IOException {
		LogDirSVNRepository repo = (LogDirSVNRepository) LogDirSVNRepositoryFactory.create(SVNURL.parseURIDecoded(logdirRepositoryUri));
		
		int maxRevision = 15;
		for(long i=1; i<=maxRevision;i++) {
			File commitLogFile = repo.getLogEntryFile(i);
			FileUtils.touch(commitLogFile);
		}
		
		long latestRevision = repo.getLatestRevision();
		assertEquals(maxRevision, latestRevision);
		
		long latestRevision2 = repo.getLatestRevision();
		assertEquals(maxRevision, latestRevision2);
	}
	
	public void testGetLatestRevisionTwiceWithChange() throws SVNException, IOException {
		LogDirSVNRepository repo = (LogDirSVNRepository) LogDirSVNRepositoryFactory.create(SVNURL.parseURIDecoded(logdirRepositoryUri));
		
		int maxRevision = 15;
		for(long i=1; i<=maxRevision;i++) {
			File commitLogFile = repo.getLogEntryFile(i);
			FileUtils.touch(commitLogFile);
		}
		
		long latestRevision = repo.getLatestRevision();
		assertEquals(maxRevision, latestRevision);
		
		int maxRevision2 = 25;
		for(long i=maxRevision+1; i<=maxRevision2;i++) {
			File commitLogFile = repo.getLogEntryFile(i);
			FileUtils.touch(commitLogFile);
		}
		
		long latestRevision2 = repo.getLatestRevision();
		assertEquals(maxRevision2, latestRevision2);
	}

}
