package nl.jiankai.refactoringplugin.git;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.storage.RepositoryDetails;
import nl.jiankai.refactoringplugin.storage.StorageListener;
import nl.jiankai.refactoringplugin.tasks.ScheduledTask;
import nl.jiankai.refactoringplugin.tasks.ScheduledTaskExecutorService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public final class GitRepositoryManager implements StorageListener<RepositoryDetails> {
    private static final Logger LOGGER = Logger.getInstance(GitRepositoryManager.class);
    private Map<String, GitRepository> gitRepositories = new HashMap<>();
    private PluginConfiguration pluginConfiguration;
    private ScheduledTaskExecutorService<Void> executorService = new ScheduledTaskExecutorService<>();
    private GitRepositoryDiscovery gitRepositoryDiscovery = new LocalFileGitRepositoryDiscovery();

    public GitRepositoryManager() {
        pluginConfiguration = ApplicationManager.getApplication().getService(PluginConfiguration.class);
        discoverGitRepositories();
    }

    private void discoverGitRepositories() {
        executorService.executeRecurringTask(
                ScheduledTask.builder(Void.class)
                        .task(() -> {
                            LOGGER.info("Discovering git repositories...");
                            gitRepositoryDiscovery
                                    .discover()
                                    .forEach(gitRepository -> {
                                        String gitRepositoryId = gitRepository.getId();

                                        if (!gitRepositories.containsKey(gitRepositoryId)) {
                                            LOGGER.info("Discovered new git repository: '%s'".formatted(gitRepositoryId));
                                            gitRepositories.put(gitRepositoryId, gitRepository);
                                        }
                                    });
                            return null;
                        })
                        .period(10)
                        .periodTimeUnit(TimeUnit.SECONDS)
                        .recurring()
                        .build()
        );


    }

    @Override
    public void onAdded(StorageEvent<RepositoryDetails> event) {
        RepositoryDetails repositoryDetails = event.affected();

        if (gitRepositories.containsKey(repositoryDetails.getId())) {
            LOGGER.warn("Repository '%s' not added as it already exists".formatted(repositoryDetails.getId()));
        } else {
            gitRepositories.put(repositoryDetails.getId(), GitUtil.clone(repositoryDetails.url(), new File(pluginConfiguration.pluginGitRepositoryDirectory() + "/" + repositoryDetails.getId())));
        }
    }

    @Override
    public void onUpdated(StorageEvent<RepositoryDetails> event) {

    }

    @Override
    public void onRemoved(StorageEvent<RepositoryDetails> event) {

    }

    public Map<String, GitRepository> gitRepositories() {
        return gitRepositories;
    }
}
