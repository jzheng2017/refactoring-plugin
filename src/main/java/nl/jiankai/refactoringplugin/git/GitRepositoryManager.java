package nl.jiankai.refactoringplugin.git;

import com.intellij.openapi.components.Service;
import nl.jiankai.refactoringplugin.storage.RepositoryDetails;
import nl.jiankai.refactoringplugin.storage.StorageListener;

import java.util.HashMap;
import java.util.Map;

@Service
public final class GitRepositoryManager implements StorageListener<RepositoryDetails> {
    private Map<String, GitRepository> gitRepositories = new HashMap<>();


    public GitRepositoryManager() {

    }

    @Override
    public void onAdded(StorageEvent<RepositoryDetails> event) {

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
