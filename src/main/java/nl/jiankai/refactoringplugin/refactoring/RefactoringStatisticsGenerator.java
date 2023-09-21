package nl.jiankai.refactoringplugin.refactoring;

import nl.jiankai.refactoringplugin.project.dependencymanagement.Project;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class RefactoringStatisticsGenerator {
    public static RefactoringStatistics compute(Map<Project, List<RefactoringImpact>> projectImpactInfo) {
        return new RefactoringStatistics(computeProjectsWithMostImpact(projectImpactInfo), computeMostImpactedFiles(projectImpactInfo.values().stream().flatMap(Collection::stream).toList()), computeTotalProjectsImpacted(projectImpactInfo));
    }

    private static long computeTotalProjectsImpacted(Map<Project, List<RefactoringImpact>> projectImpactInfo) {
        return projectImpactInfo.values().stream().filter(Predicate.not(List::isEmpty)).count();
    }

    private static Map<Project, Integer> computeProjectsWithMostImpact(Map<Project, List<RefactoringImpact>> projectImpactInfo) {
        List<Map.Entry<Project, List<RefactoringImpact>>> projects = new ArrayList<>(projectImpactInfo.entrySet());

        projects.sort(Comparator.comparingInt(a -> a.getValue().size()));

        return projects
                .stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }

    private static Map<String, Integer> computeMostImpactedFiles(List<RefactoringImpact> refactoringImpacts) {
        Map<String, Integer> files = new HashMap<>();

        for (RefactoringImpact refactoringImpact : refactoringImpacts) {
            String filePath = refactoringImpact.filePath();
            if (files.containsKey(filePath)) {
                files.put(filePath, files.get(filePath) + 1);
            } else {
                files.put(filePath, 1);
            }
        }

        List<Map.Entry<String, Integer>> filesList = new ArrayList<>(files.entrySet());

        filesList.sort(Comparator.comparingInt(Map.Entry::getValue));

        return filesList
                .stream()
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
