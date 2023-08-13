package nl.jiankai.refactoringplugin.git;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.tasks.ScheduledTask;
import nl.jiankai.refactoringplugin.tasks.ScheduledTaskExecutorService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public final class LocalFileGitRepositoryDiscovery implements GitRepositoryDiscovery {
    private static final Logger LOGGER = Logger.getInstance(LocalFileGitRepositoryDiscovery.class);
    private ScheduledTaskExecutorService<Stream<GitRepository>> executorService = new ScheduledTaskExecutorService<>();
    private PluginConfiguration pluginConfiguration;
    private GitRepositoryFactory gitRepositoryFactory;

    public LocalFileGitRepositoryDiscovery() {
        pluginConfiguration = ApplicationManager.getApplication().getService(PluginConfiguration.class);
        gitRepositoryFactory = new JGitRepositoryFactory();
    }

    @Override
    public Stream<GitRepository> discover() {
        try {
            Class<Stream<GitRepository>> clazz = null;
            return executorService.executeTask(
                            ScheduledTask
                                    .builder(clazz)
                                    .task(() -> this.scan(pluginConfiguration.pluginGitRepositoryDirectory()))
                                    .build())
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn("Something went wrong while discovering git repositories", e);
            return Stream.empty();
        }
    }

    private Stream<GitRepository> scan(String directory) {
        return getAllSubDirectories(directory)
                .stream()
                .filter(file -> file.isDirectory() && !file.getName().startsWith("."))
                .map(dir -> gitRepositoryFactory.createRepository(dir.getAbsolutePath()));
    }

    private List<File> getAllSubDirectories(String directory) {
        return Arrays
                .stream(
                        Objects.requireNonNull(new File(directory).listFiles(File::isDirectory))
                ).toList();
    }
}
