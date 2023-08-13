package nl.jiankai.refactoringplugin.storage;

import java.util.Collection;

public interface EntityStorageService<E extends Identifiable> extends StorageService<E> {

    void remove(E entity);

    void remove(Collection<E> entities);
}
