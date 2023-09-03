package nl.jiankai.refactoringplugin.git;

import java.util.stream.Stream;


public interface GitRepositoryDiscovery {

    /**
     * Discover all git repositories
     * @return the list of all discovered git repositories
     */
    Stream<GitRepository> discover();
}
