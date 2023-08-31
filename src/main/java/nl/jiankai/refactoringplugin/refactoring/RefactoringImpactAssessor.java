package nl.jiankai.refactoringplugin.refactoring;

import java.util.Collection;

public interface RefactoringImpactAssessor {
    ProjectImpactInfo assesImpact(RefactoringData refactoringData);
}
