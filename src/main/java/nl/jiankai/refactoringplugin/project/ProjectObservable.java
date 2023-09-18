package nl.jiankai.refactoringplugin.project;

public interface ProjectObservable<T> {

    void addListener(ProjectListener<T> listener);
    void removeListener(ProjectListener<T> listener);
}
