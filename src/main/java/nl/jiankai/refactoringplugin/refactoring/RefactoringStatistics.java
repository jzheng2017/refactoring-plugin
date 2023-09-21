package nl.jiankai.refactoringplugin.refactoring;

import nl.jiankai.refactoringplugin.project.dependencymanagement.Project;

import java.util.Map;

public record RefactoringStatistics(Map<Project, Integer> projectsWithMostImpact) {
}
