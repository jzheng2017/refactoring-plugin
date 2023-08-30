package nl.jiankai.refactoringplugin.refactoring;

import java.util.Collection;

public record ProjectImpactInfo(Collection<RefactoringImpact> refactoringImpacts) {
}
