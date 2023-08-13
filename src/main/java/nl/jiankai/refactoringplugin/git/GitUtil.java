package nl.jiankai.refactoringplugin.git;

import com.intellij.openapi.diagnostic.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;

public class GitUtil {
    private static final Logger LOGGER = Logger.getInstance(GitUtil.class);
    public static boolean validGitRepository(String url) {
        try {
            return new URIish(url).isRemote();
        } catch (URISyntaxException e) {
            LOGGER.warn("Invalid url: %s".formatted(url));
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
            LOGGER.warn("Could not clone the git repository: %s".formatted(e.getMessage()));
            throw new GitOperationException("Could not clone the git repository", e);
        }
    }
}
