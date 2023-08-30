package nl.jiankai.refactoringplugin.refactoring;

import java.util.Collection;

public interface RefactoringImpactAssessor {
    Collection<ProjectImpactInfo> assesImpact(RefactoringData refactoringData);
}
