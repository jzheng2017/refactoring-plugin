package nl.jiankai.refactoringplugin.storage;

import java.util.List;
import java.util.stream.Stream;

public abstract class RepositoryStorageService<T> implements StorageService<RepositoryDetails>, Mappable<RepositoryDetails, T> {
    private StorageService<T> storageService;

    protected RepositoryStorageService(StorageService<T> storageService) {
        this.storageService = storageService;
    }

    @Override
    public Stream<RepositoryDetails> read() {
        return storageService.read().map(this::source);
    }

    @Override
    public void write(RepositoryDetails content) {
        storageService.write(target(content));
    }

    @Override
    public void write(List<RepositoryDetails> list) {
        storageService.write(list.stream().map(this::target).toList());
    }

    @Override
    public void append(RepositoryDetails content) {
        storageService.append(target(content));
    }

    @Override
    public void append(List<RepositoryDetails> list) {
        storageService.append(list.stream().map(this::target).toList());
    }
}
