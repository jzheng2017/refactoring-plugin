package nl.jiankai.refactoringplugin.refactoring;

import nl.jiankai.refactoringplugin.project.dependencymanagement.Project;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class RefactoringStatisticsGenerator {
    public static RefactoringStatistics compute(Map<Project, List<RefactoringImpact>> projectImpactInfo) {
        return new RefactoringStatistics(computeProjectsWithMostImpact(projectImpactInfo));
    }

    private static Map<Project, Integer> computeProjectsWithMostImpact(Map<Project, List<RefactoringImpact>> projectImpactInfo) {
        List<Map.Entry<Project, List<RefactoringImpact>>> projects = new ArrayList<>(projectImpactInfo.entrySet());

        projects.sort(Comparator.comparingInt(a -> a.getValue().size()));

        return projects
                .stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }
}
