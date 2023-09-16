package nl.jiankai.refactoringplugin.dependencymanagement;

public record Project(String groupId, String artifactId, String version, String pathToProject) {


    @Override
    public String toString() {
        return groupId + "-" + artifactId + "-" + version;
    }
}
