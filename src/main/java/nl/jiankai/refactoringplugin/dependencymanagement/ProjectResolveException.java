package nl.jiankai.refactoringplugin.dependencymanagement;

public class ProjectResolveException extends RuntimeException {
    public ProjectResolveException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
