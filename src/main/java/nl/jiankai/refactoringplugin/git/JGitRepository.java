package nl.jiankai.refactoringplugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class JGitRepository implements GitRepository{
    private final Git git;
    public JGitRepository(Git git) {
        this.git = git;
    }

    @Override
    public String getId() {
        return git.getRepository().getConfig().getString("remote", "origin", "url");
    }
}
