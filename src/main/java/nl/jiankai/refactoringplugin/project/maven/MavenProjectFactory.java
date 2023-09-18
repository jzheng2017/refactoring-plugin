package nl.jiankai.refactoringplugin.project.maven;

import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.project.ProjectFactory;

import java.io.File;

public class MavenProjectFactory implements ProjectFactory {
    @Override
    public Project createProject(File directory) {
        return new MavenProject(directory);
    }
}
