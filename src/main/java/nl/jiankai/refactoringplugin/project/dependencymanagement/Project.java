package nl.jiankai.refactoringplugin.project.dependencymanagement;

import java.io.File;

public record Project(String groupId, String artifactId, String version, File pathToProject) {


    @Override
    public String toString() {
        return groupId + "-" + artifactId + "-" + version;
    }
}
