package nl.jiankai.refactoringplugin.refactoring;

import java.util.Collection;

public interface RefactoringImpactAssessor {
    /**
     * Asses the impact of the performed refactoring action
     * @param refactoringData all data related to the refactoring action
     * @return the impact of the change to all its dependents
     */
    ProjectImpactInfo assesImpact(RefactoringData refactoringData);
}
