package nl.jiankai.refactoringplugin.git;

public interface GitRepositoryListener<T> {
    void onAdded(GitRepositoryEvent<T> event);
    void onRemoved(GitRepositoryEvent<T> event);

    record GitRepositoryEvent<T>(T affected){}
}
