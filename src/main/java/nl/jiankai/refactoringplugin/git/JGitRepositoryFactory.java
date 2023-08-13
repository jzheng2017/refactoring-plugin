package nl.jiankai.refactoringplugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class JGitRepositoryFactory implements GitRepositoryFactory{
    @Override
    public GitRepository createRepository(String directory) {
        try {
            return new JGitRepository(Git.init().setGitDir(new File(directory)).call());
        } catch (GitAPIException e) {
            throw new GitOperationException("Could not create local git repository", e);
        }
    }
}
