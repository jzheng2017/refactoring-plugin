package nl.jiankai.refactoringplugin.storage;

public interface Mappable<S, T> {
    T target(S source);
    S source(T target);
}
