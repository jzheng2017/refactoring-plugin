package nl.jiankai.refactoringplugin.refactoring.javaparser;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.ParserCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.dependencymanagement.MavenProjectDependencyResolver;
import nl.jiankai.refactoringplugin.dependencymanagement.ProjectDependencyResolver;
import nl.jiankai.refactoringplugin.git.GitRepositoryManager;
import nl.jiankai.refactoringplugin.refactoring.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class JavaParserRefactoringImpactAssessor implements RefactoringImpactAssessor {
    private Logger LOGGER = Logger.getInstance(JavaParserRefactoringImpactAssessor.class);
    private GitRepositoryManager gitRepositoryManager;
    private PluginConfiguration pluginConfiguration;
    private ProjectDependencyResolver projectDependencyResolver;

    public JavaParserRefactoringImpactAssessor() {
        pluginConfiguration = ApplicationManager.getApplication().getService(PluginConfiguration.class);
        projectDependencyResolver = ApplicationManager.getApplication().getService(MavenProjectDependencyResolver.class);
        gitRepositoryManager = ApplicationManager.getApplication().getService(GitRepositoryManager.class);
    }

    @Override
    public Collection<ProjectImpactInfo> assesImpact(RefactoringData refactoringData) {
        Map<Project, Collection<CompilationUnit>> test = getAllProjects();

        List<RefactoringImpact> r =  test.values().stream().flatMap(col -> col.stream().flatMap(cu -> collectRefactoringImpact(cu).stream())).toList();
        return null;
    }

    private Collection<RefactoringImpact> collectRefactoringImpact(CompilationUnit compilationUnit) {
        return JavaParserUtil
                .getMethodUsages(compilationUnit, "nl.jiankai.mapper.strategies.FieldNamingStrategy.transform(java.lang.String)")
                .stream()
                .map(method -> new RefactoringImpact(method.getScope().toString(), method.getNameAsString(), null))
                .toList();
    }

    private Map<Project, Collection<CompilationUnit>> getAllProjects() {
        return gitRepositoryManager
                .gitRepositories()
                .entrySet()
                .stream()
                .collect(toMap(e -> new Project(e.getKey()), e -> getProject(e.getValue().getLocalPath())));
    }

    private Collection<CompilationUnit> getProject(String path) {
        Collection<File> jarLocations = projectDependencyResolver.jars(new File(path));
        List<File> allSourceDirectories = collectAllSourceDirectories(new File(path));
        try {
            CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());

            for (File sourceDir: allSourceDirectories) {
                typeSolver.add(new JavaParserTypeSolver(sourceDir.getAbsolutePath()));
            }

            for (File jar: jarLocations) {
                typeSolver.add(new JarTypeSolver(jar.getAbsolutePath()));
            }

            ProjectRoot projectRoot = new ParserCollectionStrategy(new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(typeSolver))).collect(Path.of(path));

            return projectRoot
                    .getSourceRoots()
                    .stream()
                    .flatMap(
                            sourceRoot -> {
                                try {
                                    return sourceRoot.tryToParse().stream();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                    .filter(ParseResult::isSuccessful)
                    .map(parseResult -> parseResult.getResult().get())
                    .toList();
        } catch (Exception ex) {
            LOGGER.warn(ex);
        }

        return new ArrayList<>();
    }

    private List<File> collectAllSourceDirectories(File root) {
        List<File> sourceDirs = new ArrayList<>();

        File[] files = root.listFiles();
        if (files != null) {
            for (File child : files) {
                if (child.isDirectory() && child.getPath().endsWith("/src/main/java")) {
                    sourceDirs.add(child);
                }
                sourceDirs.addAll(collectAllSourceDirectories(child));
            }

        }
        return sourceDirs;
    }
}
