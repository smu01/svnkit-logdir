Push SVN commit message to JIRA
-------------------------------
Version: 0.1.1

What is it?
-----------
This modification to the Atlassian JIRA Subversion Plugin adds the ability to
push SVN commit messages from a private/internal SVN repository to a public
JIRA server. It consists of a simple SVNKit protocol implementation, a small
patch to the original JIRA Subversion Plugin (which adds the activation of the
new protocol implementation), and a set of shell scripts which transfer the
commit messages from the private SVN server to the public JIRA server.


Build Instruction
-----------------

1. Get 

2. Get Atlassian JIRA Subversion Plugin source code (see Atlassian JIRA Subversion Plugin page)

3. Patch source code (patch available at http://www.notatoaster.org/svnkit-logdir/foo.patch)
 
Install Instructions
--------------------
1. Additionally to the plugin's original install instructions, copy svnkit-logdir-0.1.1.jar to WEB-INF/lib.

2. Edit for your installation: subversion-jira-plugin.properties


More
----
Atlassian JIRA Subversion Plugin page:

    http://confluence.atlassian.com/display/JIRAEXT/JIRA+Subversion+plugin