package nl.jiankai.refactoringplugin.refactoring;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class RefactoringEventHandler implements RefactoringEventListener {
    private static final Logger LOGGER = Logger.getInstance(RefactoringEventHandler.class);
    @Override
    public void refactoringStarted(@NotNull String refactoringId, @Nullable RefactoringEventData beforeData) {
        LOGGER.info("Refactoring started: %s".formatted(refactoringId));
    }

    @Override
    public void refactoringDone(@NotNull String refactoringId, @Nullable RefactoringEventData afterData) {
        LOGGER.info("Refactoring done: %s".formatted(refactoringId));
    }

    @Override
    public void conflictsDetected(@NotNull String refactoringId, @NotNull RefactoringEventData conflictsData) {
        LOGGER.info("Refactoring conflicts detected: %s".formatted(refactoringId));
    }

    @Override
    public void undoRefactoring(@NotNull String refactoringId) {
        LOGGER.info("Undoing refactoring: %s".formatted(refactoringId));

    }
}
