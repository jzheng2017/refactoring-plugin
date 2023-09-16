package nl.jiankai.refactoringplugin.refactoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import nl.jiankai.refactoringplugin.dependencymanagement.Project;
import nl.jiankai.refactoringplugin.storage.api.Identifiable;

import java.util.List;

public record ProjectImpactInfo(Project project, List<RefactoringImpact> refactoringImpacts) implements Identifiable {

    @JsonIgnoreProperties
    @Override
    public String getId() {
        return project.toString();
    }
}
