package nl.jiankai.refactoringplugin.git;

public interface GitRepositoryObservable<T> {

    void addListener(GitRepositoryListener<T> listener);
    void removeListener(GitRepositoryListener<T> listener);
}
