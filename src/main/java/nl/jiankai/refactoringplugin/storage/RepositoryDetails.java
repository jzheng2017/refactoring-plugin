package nl.jiankai.refactoringplugin.storage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RepositoryDetails implements Identifiable {
    private final String url;
    private String urlPath;

    public RepositoryDetails(String url) {
        this.url = url;
        try {
            this.urlPath = Arrays
                    .stream(
                            new URL(url)
                                    .getPath()
                                    .split("/")
                    )
                    .filter(Predicate.not(String::isBlank))
                    .collect(Collectors.joining("-"));
        } catch (MalformedURLException e) {
            this.urlPath = "";
        }
    }

    @Override
    public String toString() {
        return url;
    }

    public String url() {
        return url;
    }

    public String urlPath() {
        return urlPath;
    }

    @Override
    public String getId() {
        return urlPath;
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
