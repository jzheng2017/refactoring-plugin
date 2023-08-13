package nl.jiankai.refactoringplugin.storage;

public interface StorageListener<T> {

    void onAdded(StorageEvent<T> event);
    void onUpdated(StorageEvent<T> event);
    void onRemoved(StorageEvent<T> event);

    record StorageEvent<T>(T affected){}
}
