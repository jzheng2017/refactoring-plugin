package nl.jiankai.refactoringplugin.project;

import nl.jiankai.refactoringplugin.project.Project;

import java.util.stream.Stream;


public interface ProjectDiscovery {

    /**
     * Discover all git repositories
     * @return the list of all discovered git repositories
     */
    Stream<Project> discover();
}
