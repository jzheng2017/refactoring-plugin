package nl.jiankai.refactoringplugin.git;

import org.eclipse.jgit.api.Git;

public class JGitRepository implements GitRepository{
    private final Git git;
    public JGitRepository(Git git) {
        this.git = git;
    }
}
