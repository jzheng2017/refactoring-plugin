package nl.jiankai.refactoringplugin.project;

import nl.jiankai.refactoringplugin.project.Project;

import java.util.stream.Stream;


public interface ProjectDiscovery {

    /**
     * Discover all projects within a space
     * @return the list of all discovered projects
     */
    Stream<Project> discover();
}
