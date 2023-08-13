package nl.jiankai.refactoringplugin.git;

public interface GitRepositoryListener {

    void onAdded(GitRepositoryEvent event);

    record GitRepositoryEvent(GitRepository affected){}
}
