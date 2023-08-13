package nl.jiankai.refactoringplugin.git;

public interface GitRepositoryFactory {
    GitRepository createRepository(String directory);
}
