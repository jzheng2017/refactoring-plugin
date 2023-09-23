package nl.jiankai.refactoringplugin.refactoring;

import nl.jiankai.refactoringplugin.project.dependencymanagement.Project;

import java.util.Map;
import java.util.stream.Collectors;

public record RefactoringStatistics(Map<Project, Integer> projectsWithMostImpact,
                                    Map<String, Integer> filesWithMostImpacts,
                                    long projectsImpacted

                                    ) {

    @Override
    public String toString() {
        int mostImpactLimit = 3;
        StringBuilder view = new StringBuilder();
        view
                .append("Top %s projects with most impacts".formatted(mostImpactLimit))
                .append("\n=================================\n");
        for (Map.Entry<Project, Integer> projectEntry : projectsWithMostImpact.entrySet().stream().limit(3).collect(Collectors.toSet())) {
            Project project = projectEntry.getKey();
            view
                    .append(project)
                    .append(": ")
                    .append(projectEntry.getValue())
                    .append("\n");
        }

        view
                .append("Top %s files with most impacts".formatted(mostImpactLimit))
                .append("\n=================================\n");

        for (Map.Entry<String, Integer> projectEntry : filesWithMostImpacts.entrySet().stream().limit(3).collect(Collectors.toSet())) {
            view
                    .append(projectEntry.getKey())
                    .append(": ")
                    .append(projectEntry.getValue())
                    .append("\n");
        }

        view.append("\n=================================\n");

        view
                .append("%s projects out of %s impacted".formatted(projectsImpacted, projectsWithMostImpact.size()))
                .append("\n=================================\n");

        return view.toString();
    }
}
