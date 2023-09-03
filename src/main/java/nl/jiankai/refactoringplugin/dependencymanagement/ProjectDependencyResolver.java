package nl.jiankai.refactoringplugin.dependencymanagement;

import nl.jiankai.refactoringplugin.refactoring.javaparser.Dependency;

import java.io.File;
import java.util.Collection;

/**
 * Interface that handles a Project's dependencies
 */
public interface ProjectDependencyResolver {
    /**
     * Resolves all the dependencies of a project
     * @param projectRootPath the location of the project
     * @return all dependencies
     */
    Collection<Dependency> resolve(File projectRootPath);

    /**
     * Resolves the file locations of all the jars of the dependencies that the project depends on
     * @param projectRootPath the location of the project
     * @return
     */
    Collection<File> jars(File projectRootPath);

    /**
     * Download all dependencies of the project to a local repository
     * @param projectRootPath the location of the project
     */
    void install(File projectRootPath);
}
