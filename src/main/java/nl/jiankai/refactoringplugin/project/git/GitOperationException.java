package nl.jiankai.refactoringplugin.project.git;

public class GitOperationException extends RuntimeException {

    public GitOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
