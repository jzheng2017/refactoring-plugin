package nl.jiankai.refactoringplugin.project.maven;

import nl.jiankai.refactoringplugin.project.dependencymanagement.MavenProjectDependencyResolver;
import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.refactoring.javaparser.Dependency;

import java.io.File;
import java.util.Collection;
import java.util.Objects;

public class MavenProject implements Project {
    private final File projectRootPath;
    private final MavenProjectDependencyResolver dependencyResolver = new MavenProjectDependencyResolver();

    public MavenProject(File projectRootPath) {
        this.projectRootPath = projectRootPath;
    }

    @Override
    public File getLocalPath() {
        return projectRootPath;
    }

    @Override
    public Collection<Dependency> resolve() {
        return dependencyResolver.resolve(getLocalPath());
    }

    @Override
    public Collection<File> jars() {
        return dependencyResolver.jars(getLocalPath());
    }

    @Override
    public void install() {
        dependencyResolver.install(getLocalPath());
    }

    @Override
    public nl.jiankai.refactoringplugin.project.dependencymanagement.Project getProjectVersion() {
        return dependencyResolver.getProjectVersion(getLocalPath());
    }

    @Override
    public String getId() {
        return getLocalPath().getAbsolutePath();
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MavenProject that = (MavenProject) o;
        return Objects.equals(projectRootPath, that.projectRootPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectRootPath);
    }
}
