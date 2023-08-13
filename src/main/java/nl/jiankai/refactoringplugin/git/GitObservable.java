package nl.jiankai.refactoringplugin.git;

public interface GitObservable {
    void addListener(GitRepositoryListener listener);
    void removeListener(GitRepositoryListener listener);
}
