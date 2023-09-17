package nl.jiankai.refactoringplugin.refactoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import nl.jiankai.refactoringplugin.dependencymanagement.Project;
import nl.jiankai.refactoringplugin.storage.api.Identifiable;

import java.util.List;

@JsonIgnoreProperties(value = {"id"})
public record ProjectImpactInfo(Project project, RefactoringData refactoringData, List<RefactoringImpact> refactoringImpacts) implements Identifiable {

    @Override
    public String getId() {
        return project.toString() + "-" + refactoringData.fullyQualifiedSignature() + "-" + refactoringData.refactoringType();
    }

    @Override
    public String toString() {
        return getId();
    }
}
