package nl.jiankai.refactoringplugin.git;

import com.intellij.openapi.components.Service;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.tasks.ScheduledTask;
import nl.jiankai.refactoringplugin.tasks.ScheduledTaskExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public final class LocalFileGitRepositoryDiscovery implements GitRepositoryDiscovery {
    private ScheduledTaskExecutorService<Stream<GitRepository>> executorService;
    private PluginConfiguration pluginConfiguration;
    private List<GitRepositoryListener> listeners = new ArrayList<>();

    @Override
    public Stream<GitRepository> discover() {

        try {
            return executorService.executeTask(new ScheduledTask<>(() -> this.scan(pluginConfiguration.pluginGitRepositoryDirectory()), 1, TimeUnit.SECONDS, 0, false)).get();
        } catch (InterruptedException | ExecutionException e) {
            return Stream.empty();
        }
    }

    @Override
    public void addListener(GitRepositoryListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(GitRepositoryListener listener) {
        listeners.remove(listener);
    }

    private Stream<GitRepository> scan(String directory) {
        return Stream.empty();
    }
}
