package nl.jiankai.refactoringplugin.project.dependencymanagement;

public class ProjectResolveException extends RuntimeException {
    public ProjectResolveException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
