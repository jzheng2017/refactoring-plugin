package nl.jiankai.refactoringplugin.storage.filestorage.repository;

import nl.jiankai.refactoringplugin.storage.api.EntityStorageService;
import nl.jiankai.refactoringplugin.storage.api.Mappable;
import nl.jiankai.refactoringplugin.storage.api.StorageListener;
import nl.jiankai.refactoringplugin.storage.api.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static nl.jiankai.refactoringplugin.project.git.GitUtil.validGitRepository;

//TODO: prevent entities with duplicate IDs
public abstract class ProjectStorageService<T> implements EntityStorageService<ProjectDetails>, Mappable<ProjectDetails, T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStorageService.class);
    private StorageService<T> storageService;
    private List<StorageListener<ProjectDetails>> listeners = new ArrayList<>();

    protected ProjectStorageService(StorageService<T> storageService) {
        this.storageService = storageService;
    }

    public void addListener(StorageListener<ProjectDetails> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(StorageListener<ProjectDetails> listener) {
        this.listeners.remove(listener);
    }

    @Override
    public Stream<ProjectDetails> read() {
        return storageService.read().map(this::source);
    }

    @Override
    public Optional<ProjectDetails> read(String identifier) {
        return read()
                .filter(project -> Objects.equals(identifier, project.getId()))
                .findFirst();
    }

    @Override
    public void write(ProjectDetails content) {
        writeWithoutNotify(content);
        notifyUpdated(content);
    }

    @Override
    public void write(List<ProjectDetails> list) {
        writeWithoutNotify(list);
        list.forEach(this::notifyUpdated);
    }

    @Override
    public void append(ProjectDetails content) {
        ensureValidGitRepo(content.url());
        storageService.append(target(content));
        notifyAdded(content);
    }

    @Override
    public void append(List<ProjectDetails> list) {
        ensureValidGitRepos(list);
        storageService.append(list.stream().map(this::target).toList());
        list.forEach(this::notifyAdded);
    }

    @Override
    public void remove(Collection<ProjectDetails> entities) {
        Set<ProjectDetails> entitiesSet = new HashSet<>(entities);
        writeWithoutNotify(read().filter(Predicate.not(entitiesSet::contains)).toList());
        entities.forEach(this::notifyRemoved);
    }

    @Override
    public void remove(ProjectDetails entity) {
        ensureValidGitRepo(entity.url());
        read(entity.getId()).ifPresentOrElse(
                project -> {
                    writeWithoutNotify(project);
                    notifyRemoved(entity);
                },
                () -> LOGGER.warn("Could not remove project {} as it could not be found", entity)
        );
    }

    @Override
    public void remove(String identifier) {
        remove(new ProjectDetails(identifier));
    }

    @Override
    public void remove(List<String> identifiers) {
        identifiers.forEach(this::remove);
    }

    @Override
    public void clear() {
        storageService.clear();
    }

    private void ensureValidGitRepo(String url) {
        if (!validGitRepository(url)) {
            throw new IllegalArgumentException("Invalid url '%s'".formatted(url));
        }
    }

    private void ensureValidGitRepos(Collection<ProjectDetails> list) {
        list.forEach(repo -> ensureValidGitRepo(repo.url()));
    }

    private void notifyAdded(ProjectDetails projectDetails) {
        listeners.forEach(listener -> listener.onAdded(new StorageListener.StorageEvent<>(projectDetails)));
    }

    private void notifyUpdated(ProjectDetails projectDetails) {
        listeners.forEach(listener -> listener.onUpdated(new StorageListener.StorageEvent<>(projectDetails)));
    }

    private void notifyRemoved(ProjectDetails projectDetails) {
        listeners.forEach(listener -> listener.onRemoved(new StorageListener.StorageEvent<>(projectDetails)));
    }

    private void writeWithoutNotify(ProjectDetails content) {
        ensureValidGitRepo(content.url());
        storageService.write(target(content));
    }

    private void writeWithoutNotify(List<ProjectDetails> list) {
        ensureValidGitRepos(list);
        storageService.write(list.stream().map(this::target).toList());
    }
}
