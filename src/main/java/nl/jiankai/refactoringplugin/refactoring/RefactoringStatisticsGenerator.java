package nl.jiankai.refactoringplugin.refactoring;

import nl.jiankai.refactoringplugin.project.dependencymanagement.Project;

import java.util.List;
import java.util.Map;

public class RefactoringStatisticsGenerator {
    public static RefactoringStatistics compute(Map<Project, List<RefactoringImpact>> projectImpactInfo) {
        return new RefactoringStatistics();
    }
}
