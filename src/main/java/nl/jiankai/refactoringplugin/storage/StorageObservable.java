package nl.jiankai.refactoringplugin.storage;

public interface StorageObservable<T> {

    void addListener(StorageListener<T> listener);
    void removeListener(StorageListener<T> listener);
}
