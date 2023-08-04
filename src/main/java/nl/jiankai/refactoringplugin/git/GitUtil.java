package nl.jiankai.refactoringplugin.git;

import org.eclipse.jgit.transport.URIish;
import org.slf4j.LoggerFactory;

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
}
