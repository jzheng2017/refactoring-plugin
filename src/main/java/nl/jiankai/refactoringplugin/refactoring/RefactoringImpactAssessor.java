package nl.jiankai.refactoringplugin.refactoring;

import nl.jiankai.refactoringplugin.dependencymanagement.Project;

import java.util.List;

public interface RefactoringImpactAssessor {
    /**
     * Asses the impact of the performed refactoring action
     * @param refactoringData all data related to the refactoring action
     * @return the impact of the change to all its dependents
     */
    ImpactAssessment assesImpact(RefactoringData refactoringData);

    List<RefactoringImpact> assesImpact(Project project, RefactoringData refactoringData);
}
