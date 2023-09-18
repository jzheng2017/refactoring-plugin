package nl.jiankai.refactoringplugin.project;

public interface ProjectListener<T> {
    void onAdded(ProjectEvent<T> event);
    void onRemoved(ProjectEvent<T> event);

    record ProjectEvent<T>(T affected){}
}
