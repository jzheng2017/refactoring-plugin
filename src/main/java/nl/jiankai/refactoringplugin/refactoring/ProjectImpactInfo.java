package nl.jiankai.refactoringplugin.refactoring;

import java.util.List;
import java.util.Map;

public record ProjectImpactInfo(Map<Project, List<RefactoringImpact>> refactoringImpacts) {
}
