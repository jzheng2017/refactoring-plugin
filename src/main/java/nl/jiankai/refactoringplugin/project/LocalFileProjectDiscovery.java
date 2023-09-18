package nl.jiankai.refactoringplugin.project;

import com.intellij.openapi.components.Service;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.project.git.JGitRepositoryFactory;
import nl.jiankai.refactoringplugin.tasks.ScheduledTask;
import nl.jiankai.refactoringplugin.tasks.ScheduledTaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Service
public final class LocalFileProjectDiscovery implements ProjectDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileProjectDiscovery.class);
    private ScheduledTaskExecutorService<Stream<Project>> executorService = new ScheduledTaskExecutorService<>();
    private PluginConfiguration pluginConfiguration;
    private ProjectFactory projectFactory;

    public LocalFileProjectDiscovery() {
        pluginConfiguration = new PluginConfiguration();
        projectFactory = new JGitRepositoryFactory();
    }

    @Override
    public Stream<Project> discover() {
        try {
            return executorService.executeTask(
                            ScheduledTask
                                    .builder((Class<Stream<Project>>)null)
                                    .task(() -> this.scan(pluginConfiguration.pluginProjectsLocation()))
                                    .build())
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn("Something went wrong while discovering projects", e);
            return Stream.empty();
        }
    }

    private Stream<Project> scan(String directory) {
        return getAllSubDirectories(directory)
                .stream()
                .filter(file -> file.isDirectory() && !file.getName().startsWith("."))
                .map(dir -> projectFactory.createProject(dir));
    }

    private List<File> getAllSubDirectories(String directory) {
        return Arrays
                .stream(
                        Objects.requireNonNull(new File(directory).listFiles(File::isDirectory))
                ).toList();
    }
}
