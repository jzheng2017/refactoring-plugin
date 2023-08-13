package nl.jiankai.refactoringplugin.git;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.storage.RepositoryDetails;
import nl.jiankai.refactoringplugin.storage.StorageListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public final class GitRepositoryManager implements StorageListener<RepositoryDetails>, GitRepositoryListener {
    private static final Logger LOGGER = com.intellij.openapi.diagnostic.Logger.getInstance(GitRepositoryManager.class);
    private Map<String, GitRepository> gitRepositories = new HashMap<>();
    private PluginConfiguration pluginConfiguration;

    public GitRepositoryManager() {
        pluginConfiguration = ApplicationManager.getApplication().getService(PluginConfiguration.class);
        ApplicationManager.getApplication().getService(LocalFileGitRepositoryDiscovery.class).addListener(this);
    }

    @Override
    public void onAdded(StorageEvent<RepositoryDetails> event) {
        RepositoryDetails repositoryDetails = event.affected();

        if (gitRepositories.containsKey(repositoryDetails.getId())) {
            LOGGER.warn("Repository '%s' not added as it already exists".formatted(repositoryDetails.getId()));
        } else {
            gitRepositories.put(repositoryDetails.getId(), GitUtil.clone(repositoryDetails.url(), new File(pluginConfiguration.pluginGitRepositoryDirectory())));
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

    @Override
    public void onAdded(GitRepositoryEvent event) {
        GitRepository repository = event.affected();
        if (!gitRepositories.containsKey(repository.getId())) {
            gitRepositories.put(repository.getId(), repository);
        }
    }
}
