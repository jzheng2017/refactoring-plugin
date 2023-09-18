package nl.jiankai.refactoringplugin.project;

import nl.jiankai.refactoringplugin.refactoring.javaparser.Dependency;
import nl.jiankai.refactoringplugin.storage.api.Identifiable;

import java.io.File;
import java.util.Collection;

public interface Project extends Identifiable {
    /**
     * @return the local file path of the git repository
     */
    File getLocalPath();

    /**
     * Resolves all the dependencies of a project
     * @return all dependencies
     */
    Collection<Dependency> resolve();

    /**
     * Resolves the file locations of all the jars of the dependencies that the project depends on
     * @return list of file locations of the jars
     */
    Collection<File> jars();

    /**
     * Download all dependencies of the project to a local repository
     */
    void install();

    /**
     * @return the project details containing group id, artifact id and the version
     */
    nl.jiankai.refactoringplugin.project.dependencymanagement.Project getProjectVersion();
}
