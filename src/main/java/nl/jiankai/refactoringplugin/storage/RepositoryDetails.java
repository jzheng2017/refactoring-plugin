package nl.jiankai.refactoringplugin.storage;

import java.util.Objects;

public record RepositoryDetails(String url) implements Identifiable {

    @Override
    public String toString() {
        return url;
    }

    @Override
    public String getId() {
        return String.valueOf(hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepositoryDetails that = (RepositoryDetails) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
