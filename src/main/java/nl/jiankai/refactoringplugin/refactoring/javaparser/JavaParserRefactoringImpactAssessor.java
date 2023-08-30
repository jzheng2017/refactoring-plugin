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
import nl.jiankai.refactoringplugin.git.GitRepositoryManager;
import nl.jiankai.refactoringplugin.refactoring.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class JavaParserRefactoringImpactAssessor implements RefactoringImpactAssessor {
    private Logger LOGGER = Logger.getInstance(JavaParserRefactoringImpactAssessor.class);
    private GitRepositoryManager gitRepositoryManager;
    private PluginConfiguration pluginConfiguration;

    public JavaParserRefactoringImpactAssessor() {
        pluginConfiguration = ApplicationManager.getApplication().getService(PluginConfiguration.class);
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
        try {
            CombinedTypeSolver typeSolver =
                    new CombinedTypeSolver(
                            new ReflectionTypeSolver(),
                            new JavaParserTypeSolver("/home/jiankai/Documents/ref-plugin/repositories/jzheng2017-resultset-mapper/src/main/java"),
                            new JarTypeSolver("/home/jiankai/.m2/repository/org/slf4j/slf4j-api/2.0.3/slf4j-api-2.0.3.jar"),
                            new JarTypeSolver("/home/jiankai/.m2/repository/org/slf4j/slf4j-reload4j/2.0.3/slf4j-reload4j-2.0.3.jar")
                    );
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
}
