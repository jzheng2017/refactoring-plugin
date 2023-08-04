package nl.jiankai.refactoringplugin.storage;

import nl.jiankai.refactoringplugin.git.GitUtil;

import java.util.List;
import java.util.stream.Stream;

import static nl.jiankai.refactoringplugin.git.GitUtil.validGitRepository;

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
        ensureValidGitRepo(content.url());
        storageService.write(target(content));
    }

    @Override
    public void write(List<RepositoryDetails> list) {
        ensureValidGitRepos(list);
        storageService.write(list.stream().map(this::target).toList());
    }

    @Override
    public void append(RepositoryDetails content) {
        ensureValidGitRepo(content.url());
        storageService.append(target(content));
    }

    @Override
    public void append(List<RepositoryDetails> list) {
        ensureValidGitRepos(list);
        storageService.append(list.stream().map(this::target).toList());
    }

    private void ensureValidGitRepo(String url) {
        if (!validGitRepository(url)) {
            throw new IllegalArgumentException("Invalid url '%s'".formatted(url));
        }
    }

    private void ensureValidGitRepos(List<RepositoryDetails> list) {
        list.forEach(repo -> ensureValidGitRepo(repo.url()));
        System.out.println("wow");
    }
}
