package nl.jiankai.refactoringplugin.refactoring;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.ClassUtil;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.util.keyFMap.KeyFMap;
import nl.jiankai.refactoringplugin.dialogs.RefactoringEventDialog;
import nl.jiankai.refactoringplugin.refactoring.javaparser.JavaParserRefactoringImpactAssessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefactoringEventHandler implements RefactoringEventListener {
    private static final Logger LOGGER = Logger.getInstance(RefactoringEventHandler.class);
    private RefactoringImpactAssessor refactoringImpactAssessor = new JavaParserRefactoringImpactAssessor();
    private RefactoringData beforeRefactoringData;
    @Override
    public void refactoringStarted(@NotNull String refactoringId, @Nullable RefactoringEventData beforeData) {
        beforeRefactoringData = toRefactoringData(refactoringId, beforeData);
        LOGGER.info("Refactoring started: %s".formatted(refactoringId));
    }

    @Override
    public void refactoringDone(@NotNull String refactoringId, @Nullable RefactoringEventData afterData) {
        ProjectImpactInfo impacts = refactoringImpactAssessor.assesImpact(beforeRefactoringData);
        new RefactoringEventDialog(impacts).show();
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

    private RefactoringData toRefactoringData(String refactoringId, RefactoringEventData refactoringEventData) {
        PsiElement refactoredElement = getRefactoredElement(refactoringEventData);

        if (refactoredElement instanceof PsiMethod pm) {
            return new RefactoringData(pm.getContainingClass().getQualifiedName(), pm.getName(), createFullyQualifiedName(pm), getRefactoringType(refactoringId));
        }
        return null;
    }

    private String createFullyQualifiedName(PsiMethod method) {
        return "%s.%s(%s)".formatted(method.getContainingClass().getQualifiedName(), method.getName(), String.join(", ", ClassUtil.getVMParametersMethodSignature(method).split(",")));
    }

    private PsiElement getRefactoredElement(RefactoringEventData refactoringEventData) {
        Key elementKey = getElementKey(refactoringEventData.get());

        if (elementKey != null) {
            return (PsiElement) refactoringEventData.getUserData(elementKey);
        }

        return null;
    }

    private Key getElementKey(KeyFMap keyFMap) {
        Key[] keys = keyFMap.getKeys();

        for (Key key : keys) {
            if ("element".equals(key.toString())) { //assumes the toString only produces the key value
                return key;
            }
        }

        return null;
    }

    private RefactoringType getRefactoringType(String refactoringId) {
        return RefactoringType.METHOD_NAME;
    }
}
