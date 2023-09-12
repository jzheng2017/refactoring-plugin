package nl.jiankai.refactoringplugin.dependencymanagement;

import com.intellij.openapi.components.Service;
import nl.jiankai.refactoringplugin.refactoring.javaparser.Dependency;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.api.model.Model;
import org.apache.maven.model.v4.MavenXpp3Reader;
import org.apache.maven.shared.invoker.*;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public final class MavenProjectDependencyResolver implements ProjectDependencyResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProjectDependencyResolver.class);
    private static final String POM_FILE = "pom.xml";

    @Override
    public Collection<Dependency> resolve(File projectRootPath) {
        try {
            File file = findPomFile(projectRootPath);
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(file));
            Map<String, String> properties = model.getProperties();
            return model
                    .getDependencies()
                    .stream()
                    .map(dependency -> new Dependency(dependency.getGroupId(), dependency.getArtifactId(), resolveProperty(properties, dependency.getVersion())))
                    .toList();
        } catch (FileNotFoundException | IOException | XmlPullParserException e) {
            LOGGER.warn("Could not resolve project dependencies for project path '{}'", projectRootPath, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Collection<File> jars(File projectRootPath) {
        return findProjectDependencyJars(resolve(projectRootPath));
    }

    @NotNull
    private List<File> findProjectDependencyJars(Collection<Dependency> projectDependencies) {
        File repositoryLocation = getMavenRepositoryLocation();
        Set<Dependency> dependencies = new HashSet<>(projectDependencies);
        Set<String> fileNames = dependencies.stream().map(this::createJarName).collect(Collectors.toSet());

        List<File> foundJars = findJarsRecursive(repositoryLocation, fileNames).toList();
        if (foundJars.size() != dependencies.size()) {
            List<String> jarsNotFound = getMissingJars(fileNames, foundJars.stream().map(File::getName).collect(Collectors.toSet()));
            LOGGER.warn("Not all jars were found. {} jars were found of the {} dependencies", foundJars.size(), dependencies.size());
            LOGGER.warn("The following jars are missing: {}", jarsNotFound);
        }
        return foundJars;
    }

    @Override
    public void install(File projectRootPath) {
        if (dependenciesAlreadySatisfied(projectRootPath)) {
            LOGGER.info("[{}]: Installing project dependencies is not necessary. All dependencies have already been satisfied.", projectRootPath);
        } else {
            File file = findPomFile(projectRootPath);
            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(file);
            request.setGoals(Collections.singletonList("compile"));
            Invoker invoker = new DefaultInvoker();

            try {
                invoker.execute(request);
            } catch (MavenInvocationException e) {
                LOGGER.warn("Could not install dependencies for project on path '{}'", projectRootPath.getPath(), e);
            }
        }
    }

    private boolean dependenciesAlreadySatisfied(File projectRootPath) {
        Collection<Dependency> projectDependencies = resolve(projectRootPath);

        return projectDependencies.size() == findProjectDependencyJars(projectDependencies).size();
    }

    private List<String> getMissingJars(Set<String> fileNames, Set<String> foundJars) {
        List<String> missingJars = new ArrayList<>();

        for (String fileName : fileNames) {
            if (!foundJars.contains(fileName)) {
                missingJars.add(fileName);
            }
        }

        return missingJars;
    }

    private Stream<File> findJarsRecursive(File directory, Set<String> fileNames) {
        return FileUtils
                .listFiles(directory, FileFilterUtils.suffixFileFilter(".jar"), TrueFileFilter.INSTANCE)
                .stream()
                .filter(file -> fileNames.contains(file.getName()));
    }

    private String createJarName(Dependency dependency) {
        return dependency.artifactId() + "-" + dependency.version() + ".jar";
    }

    private File findPomFile(File projectRootPath) {
        return findFileNonRecursive(projectRootPath, (dir, fileName) -> POM_FILE.equals(fileName));
    }

    private File findFileNonRecursive(File directory, FilenameFilter filter) {
        File[] foundFiles = directory.listFiles(filter);

        if (foundFiles != null && foundFiles.length > 0) {
            return foundFiles[0];
        }

        throw new FileNotFoundException("Could not find file '%s'".formatted(directory.getName()));
    }

    private String resolveProperty(Map<String, String> properties, String property) {
        if (property.startsWith("${") && property.endsWith("}")) {
            return properties.getOrDefault(property.substring(2, property.length() - 1), property);
        }

        return property;
    }

    private File getMavenRepositoryLocation() {
        String location = System.getenv("MAVEN_HOME");

        if (location == null) {
            location = System.getenv("MVN_HOME");
        }

        if (location == null) {
            location = System.getenv("M2_HOME");
        }

        if (location == null) {
            location = System.getenv("HOME") + "/.m2";
        }

        return new File(location);
    }

    private static class FileNotFoundException extends RuntimeException {
        public FileNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }
}
