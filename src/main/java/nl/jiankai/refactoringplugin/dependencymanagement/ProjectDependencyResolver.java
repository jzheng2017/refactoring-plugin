package nl.jiankai.refactoringplugin.dependencymanagement;

import nl.jiankai.refactoringplugin.refactoring.javaparser.Dependency;

import java.io.File;
import java.util.Collection;

public interface ProjectDependencyResolver {
    Collection<Dependency> resolve(File projectRootPath);
    Collection<File> jars(File projectRootPath);
    void install(File projectRootPath);
}
