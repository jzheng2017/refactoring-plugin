package nl.jiankai.refactoringplugin.git;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.dependencymanagement.MavenProjectDependencyResolver;
import nl.jiankai.refactoringplugin.dependencymanagement.ProjectDependencyResolver;
import nl.jiankai.refactoringplugin.storage.RepositoryDetails;
import nl.jiankai.refactoringplugin.storage.StorageListener;
import nl.jiankai.refactoringplugin.tasks.ScheduledTask;
import nl.jiankai.refactoringplugin.tasks.ScheduledTaskExecutorService;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public final class GitRepositoryManager implements StorageListener<RepositoryDetails> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryManager.class);
    private Map<String, GitRepository> gitRepositories = new HashMap<>();
    private PluginConfiguration pluginConfiguration;
    private ScheduledTaskExecutorService<Void> executorService = new ScheduledTaskExecutorService<>();
    private GitRepositoryDiscovery gitRepositoryDiscovery = new LocalFileGitRepositoryDiscovery();
    private ProjectDependencyResolver projectDependencyResolver;
    public GitRepositoryManager() {
        pluginConfiguration = ApplicationManager.getApplication().getService(PluginConfiguration.class);
        projectDependencyResolver = ApplicationManager.getApplication().getService(MavenProjectDependencyResolver.class);

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
            gitRepositoryDiscovery
                    .discover()
                    .parallel()
                    .forEach(gitRepository -> {
                        String gitRepositoryId = gitRepository.getId();

                        if (!gitRepositories.containsKey(gitRepositoryId)) {
                            LOGGER.info("Discovered new git repository: '{}'", gitRepositoryId);
                            gitRepositories.put(gitRepositoryId, gitRepository);
                            downloadProjectDependencies(gitRepository);
                        }
                    });
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

    }

    public Map<String, GitRepository> gitRepositories() {
        return Map.copyOf(gitRepositories);
    }
}
