package nl.jiankai.refactoringplugin.storage;

public class FileRepositoryStorageService extends RepositoryStorageService<String> {
    public FileRepositoryStorageService(LocalFileStorageService storageService) {
        super(storageService);
    }

    @Override
    public String target(RepositoryDetails source) {
        return source.toString();
    }

    @Override
    public RepositoryDetails source(String target) {
        return new RepositoryDetails(target);
    }
}
