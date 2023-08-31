package nl.jiankai.refactoringplugin.git;

import org.eclipse.jgit.api.Git;

public class JGitRepository implements GitRepository {
    private final Git git;

    public JGitRepository(Git git) {
        this.git = git;
    }

    @Override
    public String getId() {
        return git.getRepository().getDirectory().getName();
    }

    @Override
    public String getLocalPath() {
        String path = git.getRepository().getDirectory().getAbsolutePath();
        if (path.endsWith("/.git")) {
            return path.substring(0, path.length() - "/.git".length());
        } else {
            return path;
        }
    }
}
