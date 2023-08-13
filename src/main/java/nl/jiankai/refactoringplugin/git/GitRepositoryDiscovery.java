package nl.jiankai.refactoringplugin.git;

import java.util.stream.Stream;


public interface GitRepositoryDiscovery extends GitObservable {

    Stream<GitRepository> discover();
}
