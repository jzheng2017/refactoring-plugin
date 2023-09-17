package nl.jiankai.refactoringplugin.git;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.dependencymanagement.MavenProjectDependencyResolver;
import nl.jiankai.refactoringplugin.dependencymanagement.ProjectDependencyResolver;
import nl.jiankai.refactoringplugin.storage.filestorage.repository.RepositoryDetails;
import nl.jiankai.refactoringplugin.storage.api.StorageListener;
import nl.jiankai.refactoringplugin.tasks.ScheduledTask;
import nl.jiankai.refactoringplugin.tasks.ScheduledTaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public final class GitRepositoryManager implements StorageListener<RepositoryDetails>, GitRepositoryObservable<GitRepository> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryManager.class);
    private final Map<String, GitRepository> gitRepositories = new HashMap<>();
    private PluginConfiguration pluginConfiguration;
    private ScheduledTaskExecutorService<Void> executorService = new ScheduledTaskExecutorService<>();
    private GitRepositoryDiscovery gitRepositoryDiscovery = new LocalFileGitRepositoryDiscovery();
    private ProjectDependencyResolver projectDependencyResolver;
    private List<GitRepositoryListener<GitRepository>> listeners = new ArrayList<>();
    public GitRepositoryManager() {
        pluginConfiguration = new PluginConfiguration();
        projectDependencyResolver = new MavenProjectDependencyResolver();

        discoverGitRepositories();
        submitGitRepositoryDiscoveryTask();
    }

    private void submitGitRepositoryDiscoveryTask() {
        executorService.executeRecurringTask(
                ScheduledTask.builder(Void.class)
                        .task(() -> {
                            discoverGitRepositories();
                            return null;
                        })
                        .period(10)
                        .periodTimeUnit(TimeUnit.SECONDS)
                        .recurring()
                        .build()
        );
    }

    private void discoverGitRepositories() {
        LOGGER.trace("Discovering git repositories...");
        try {
            synchronized (gitRepositories) {
                Map<String, GitRepository> existingGitRepositories = new HashMap<>(gitRepositories);
                gitRepositoryDiscovery
                        .discover()
                        .parallel()
                        .forEach(gitRepository -> {
                            String gitRepositoryId = gitRepository.getId();
                            existingGitRepositories.remove(gitRepositoryId);

                            if (!gitRepositories.containsKey(gitRepositoryId)) {
                                LOGGER.info("Discovered new git repository: '{}'", gitRepositoryId);
                                gitRepositories.put(gitRepositoryId, gitRepository);
                                notifyAdded(gitRepository);
                                downloadProjectDependencies(gitRepository);
                            }
                        });

                existingGitRepositories.values().forEach(gitRepository -> {
                    String gitRepositoryId = gitRepository.getId();
                    gitRepositories.remove(gitRepositoryId);
                    LOGGER.info("Git repository '{}' has been removed as it's not available anymore", gitRepositoryId);
                    notifyRemoved(gitRepository);
                });
            }
        } catch (Exception e) {
            LOGGER.warn("Something went wrong while discovering git repositories: {}", e.getMessage(), e);
        }
    }

    private void downloadProjectDependencies(GitRepository gitRepository) {
         projectDependencyResolver.install(new File(gitRepository.getLocalPath()));
    }

    @Override
    public void onAdded(StorageEvent<RepositoryDetails> event) {
        RepositoryDetails repositoryDetails = event.affected();

        if (gitRepositories.containsKey(repositoryDetails.getId())) {
            LOGGER.warn("Repository '{}' not added as it already exists", repositoryDetails.getId());
        } else {
            gitRepositories.put(repositoryDetails.getId(), GitUtil.clone(repositoryDetails.url(), new File(pluginConfiguration.pluginGitRepositoryDirectory() + File.separator + repositoryDetails.getId())));
        }
    }

    @Override
    public void onUpdated(StorageEvent<RepositoryDetails> event) {

    }

    @Override
    public void onRemoved(StorageEvent<RepositoryDetails> event) {
        RepositoryDetails repositoryDetails = event.affected();
        String repositoryId = repositoryDetails.getId();

        if (gitRepositories.containsKey(repositoryId)) {
            LOGGER.info("Repository '{}' will be removed", repositoryId);
            gitRepositories.remove(repositoryId);
        } else {
            LOGGER.warn("Tried to remove repository '{}'. But it could not be found.", repositoryId);
        }
    }

    public Map<String, GitRepository> gitRepositories() {
        return Map.copyOf(gitRepositories);
    }

    @Override
    public void addListener(GitRepositoryListener<GitRepository> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(GitRepositoryListener<GitRepository> listener) {
        listeners.remove(listener);
    }

    public void notifyAdded(GitRepository gitRepository) {
        listeners.forEach(listener -> listener.onAdded(new GitRepositoryListener.GitRepositoryEvent<>(gitRepository)));
    }
    public void notifyRemoved(GitRepository gitRepository) {
        listeners.forEach(listener -> listener.onRemoved(new GitRepositoryListener.GitRepositoryEvent<>(gitRepository)));
    }
}
