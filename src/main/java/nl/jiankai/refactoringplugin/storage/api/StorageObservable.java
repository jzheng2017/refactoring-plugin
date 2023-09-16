package nl.jiankai.refactoringplugin.storage.api;

public interface StorageObservable<T> {

    void addListener(StorageListener<T> listener);
    void removeListener(StorageListener<T> listener);
}
