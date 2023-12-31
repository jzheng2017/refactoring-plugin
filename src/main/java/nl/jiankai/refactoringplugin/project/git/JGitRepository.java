package nl.jiankai.refactoringplugin.project.git;

import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.refactoring.javaparser.Dependency;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.Collection;
import java.util.Objects;

public class JGitRepository implements GitRepository {
    private final Git git;
    private final Project project;

    public JGitRepository(Git git, Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Plain projects are not supported currently");
        }
        this.git = git;
        this.project = project;
    }

    @Override
    public String getId() {
        return project.getId();
    }

    @Override
    public File getLocalPath() {
        String path = git.getRepository().getDirectory().getAbsolutePath();
        if (path.endsWith("/.git")) {
            return new File(path.substring(0, path.length() - "/.git".length()));
        } else {
            return new File(path);
        }
    }

    @Override
    public Collection<Dependency> resolve() {
        return project.resolve();
    }

    @Override
    public Collection<File> jars() {
        return project.jars();
    }

    @Override
    public void install() {
        project.install();
    }

    @Override
    public nl.jiankai.refactoringplugin.project.dependencymanagement.Project getProjectVersion() {
        return project.getProjectVersion();
    }

    @Override
    public String toString() {
        return project.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JGitRepository that = (JGitRepository) o;
        return Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project);
    }
}
