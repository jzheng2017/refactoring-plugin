package nl.jiankai.refactoringplugin.storage;

public record RepositoryDetails(String url) {

    @Override
    public String toString() {
        return url;
    }
}
