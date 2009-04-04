Push SVN commit messages to JIRA
================================

Version: 0.1.1


What is it?
-----------

This modification to the Atlassian JIRA Subversion Plugin adds the ability to push SVN commit messages from a private/internal SVN repository to a public JIRA server. It consists of

    * a simple SVNKit protocol implementation, which I call svnkit-logdir (because it reads SVN commit log messages from a directory)
    * a small patch to the original JIRA Subversion Plugin (which adds the activation of the new protocol implementation)
    * a set of shell scripts which transfer the commit messages from the private SVN server to the public JIRA server via SCP (or whatever you prefer instead)

(Whenever I mention the plugin in this document, I always mean the original Atlassian JIRA Subversion Plugin.)


Build Instruction
-----------------

In order to use svnkit-logdir you have to patch and recompile the plugin.

   1. Get svnkit-logdir-0.1.1.jar and install it into your local Maven repository:

      mvn install:install-file -DgroupId=de.businessacts -DartifactId=svnkit-logdir -Dversion=0.1.1 -Dpackaging=jar -Dfile=/path/to/svnkit-logdir-0.1.1.jar

      Of course you can also compile svnkit-logdir from source (zip, tgz or git).
   2. Get plugin source code (see Atlassian JIRA Subversion Plugin page)
   3. Patch plugin source code with foo.patch, using your favorite patch utility (e.g. TortoiseSVN on Windows)
   4. Compile and package the plugin:

      mvn package


Install Instructions
--------------------

   1. Additionally to the plugin's original install instructions, copy svnkit-logdir-0.1.1.jar to JIRA's WEB-INF/lib directory.
   2. In the same directory, replace the original plugin JAR with the patched one from above.
   3. Add a new repository to JIRA with the repository root set to logdir:///path/to/svnlogs
   4. Configure your Subversion server to copy the commit messages to your JIRA server, e.g. from inside a post-commit hook script. In my experience it is a good idea to separate the generating of the XML log files from the actual transmission to the remote JIRA server, because the Subversion commit gets blocked until the post-commit script is completed. Therefore it may be wise to do the time consuming work outside of the post-commit script. I came up with the following set of scripts:
         1. A Subversion post-commit script, normally located inside the hooks directory of your Subversion repository.

            #!/bin/sh
            /path/to/scripts/svnlogxml $1 $2

         2. A Bash script named svnlogxml, which is called by the above post-commit script and dumps the commit message into a XML file inside a queue directory.

            #!/bin/sh
            REPOS="$1"
            REV="$2"
            PATH="/path/to/queue"
            FILE="commit.r$REV.xml"
            /usr/bin/svn log --xml -v -r $REV file://$REPOS > $PATH/$FILE

         3. A bash script named svnlog-queuerunner, which is called from cron and transfers all new XML files to the JIRA server via SCP (you'll need private/public keys in order to log in from the scripts without a password prompt!).

            #!/bin/sh
            PATH="/path/to/queue"
            for f in `/usr/bin/find $PATH -name commit.r*.xml -printf "%f\n"`;
            do
              /usr/bin/scp $PATH/$f username@jiraserver:./remote/path/to/logs/$f && /bin/rm $PATH/$f
            done

         4. A Bash script named svnlogxml-all, which dumps all existing commit messages to the queue directory (via the above svnlogxml script), so that JIRA also receives all old commit messages (i.e. messages from commits created before the post-commit script was installed).

            #!/bin/sh
            REPOS="$1"
            REV=`/usr/bin/svnlook youngest $REPOS`
            counter=0
            until [ $counter -eq $REV ]
            do
                counter=$(( $counter + 1 ))
                /path/to/scripts/svnlogxml $REPOS $counter
            done

More
----

    * GIT repository: http://github.com/smu01/svnkit-logdir/
    * Atlassian JIRA Subversion Plugin page: http://confluence.atlassian.com/display/JIRAEXT/JIRA+Subversion+plugin