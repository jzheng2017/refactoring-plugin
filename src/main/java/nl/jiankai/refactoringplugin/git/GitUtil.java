package nl.jiankai.refactoringplugin.git;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class GitUtil {
    private static final Logger LOGGER = Logger.getLogger(GitUtil.class.getName());
    public static boolean validGitRepository(String url) {
        try {
            return new URIish(url).isRemote();
        } catch (URISyntaxException e) {
            LOGGER.warning("Invalid url: %s".formatted(url));
            return false;
        }
    }

    public static GitRepository clone(String url, File repositoryDirectory) {
        CloneCommand cloneCommand = new CloneCommand();
        cloneCommand.setURI(url);
        cloneCommand.setDirectory(repositoryDirectory);
        try {
            return new JGitRepository(cloneCommand.call());
        } catch (GitAPIException e) {
            LOGGER.warning("Could not clone the git repository: %s".formatted(e.getMessage()));
            throw new GitOperationException("Could not clone the git repository", e);
        }
    }
}
