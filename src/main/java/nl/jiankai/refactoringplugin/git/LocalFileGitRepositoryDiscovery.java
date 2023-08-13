package nl.jiankai.refactoringplugin.git;

import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.tasks.ScheduledTask;
import nl.jiankai.refactoringplugin.tasks.ScheduledTaskExecutorService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class LocalFileGitRepositoryDiscovery implements GitRepositoryDiscovery {
    private ScheduledTaskExecutorService<GitRepository> executorService;
    private PluginConfiguration pluginConfiguration;

    @Override
    public Stream<GitRepository> discover() {

//        return executorService.executeTask(new ScheduledTask<>(() -> this.scan(pluginConfiguration.pluginAssetsBaseDirectory() + "/repositories"), 1, TimeUnit.SECONDS, 0, false));
        return null;
    }

    private List<GitRepository> scan(String directory) {
        return null;
    }
}
