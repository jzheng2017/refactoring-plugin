package nl.jiankai.refactoringplugin.storage;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static nl.jiankai.refactoringplugin.git.GitUtil.validGitRepository;

public abstract class RepositoryStorageService<T> implements EntityStorageService<RepositoryDetails>, Mappable<RepositoryDetails, T> {
    private StorageService<T> storageService;
    private List<StorageListener<RepositoryDetails>> listeners = new ArrayList<>();

    protected RepositoryStorageService(StorageService<T> storageService) {
        this.storageService = storageService;
    }

    public void addStorageListener(StorageListener<RepositoryDetails> listener) {
        this.listeners.add(listener);
    }

    public void removeStorageListener(StorageListener<RepositoryDetails> listener) {
        this.listeners.remove(listener);
    }

    @Override
    public Stream<RepositoryDetails> read() {
        return storageService.read().map(this::source);
    }

    @Override
    public void write(RepositoryDetails content) {
        writeWithoutNotify(content);
        notifyUpdated(content);
    }

    @Override
    public void write(List<RepositoryDetails> list) {
        writeWithoutNotify(list);
        list.forEach(this::notifyUpdated);
    }

    @Override
    public void append(RepositoryDetails content) {
        ensureValidGitRepo(content.url());
        storageService.append(target(content));
        notifyAdded(content);
    }

    @Override
    public void append(List<RepositoryDetails> list) {
        ensureValidGitRepos(list);
        storageService.append(list.stream().map(this::target).toList());
        list.forEach(this::notifyAdded);
    }

    @Override
    public void remove(Collection<RepositoryDetails> entities) {
        Set<RepositoryDetails> entitiesSet = new HashSet<>(entities);
        writeWithoutNotify(read().filter(Predicate.not(entitiesSet::contains)).toList());
        entities.forEach(this::notifyRemoved);
    }

    @Override
    public void remove(RepositoryDetails entity) {
        ensureValidGitRepo(entity.url());
        writeWithoutNotify(read().filter(repo -> !Objects.equals(repo, entity)).toList());
        notifyRemoved(entity);
    }

    private void ensureValidGitRepo(String url) {
        if (!validGitRepository(url)) {
            throw new IllegalArgumentException("Invalid url '%s'".formatted(url));
        }
    }

    private void ensureValidGitRepos(Collection<RepositoryDetails> list) {
        list.forEach(repo -> ensureValidGitRepo(repo.url()));
    }

    private void notifyAdded(RepositoryDetails repositoryDetails) {
        listeners.forEach(listener -> listener.onAdded(new StorageListener.StorageEvent<>(repositoryDetails)));
    }

    private void notifyUpdated(RepositoryDetails repositoryDetails) {
        listeners.forEach(listener -> listener.onUpdated(new StorageListener.StorageEvent<>(repositoryDetails)));
    }

    private void notifyRemoved(RepositoryDetails repositoryDetails) {
        listeners.forEach(listener -> listener.onRemoved(new StorageListener.StorageEvent<>(repositoryDetails)));
    }

    private void writeWithoutNotify(RepositoryDetails content) {
        ensureValidGitRepo(content.url());
        storageService.write(target(content));
    }

    private void writeWithoutNotify(List<RepositoryDetails> list) {
        ensureValidGitRepos(list);
        storageService.write(list.stream().map(this::target).toList());
    }
}
