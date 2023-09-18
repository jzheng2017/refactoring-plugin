package nl.jiankai.refactoringplugin.project;

import nl.jiankai.refactoringplugin.project.Project;

import java.io.File;

public interface ProjectFactory {
    Project createProject(File directory);
}
