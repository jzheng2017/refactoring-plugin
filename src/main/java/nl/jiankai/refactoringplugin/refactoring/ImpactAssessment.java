package nl.jiankai.refactoringplugin.refactoring;

import nl.jiankai.refactoringplugin.project.dependencymanagement.Project;

import java.util.List;
import java.util.Map;

public record ImpactAssessment(Map<Project, List<RefactoringImpact>> refactoringImpacts, RefactoringStatistics refactoringStatistics) {
}
