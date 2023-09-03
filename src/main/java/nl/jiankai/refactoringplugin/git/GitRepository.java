package nl.jiankai.refactoringplugin.git;

import nl.jiankai.refactoringplugin.storage.Identifiable;

public interface GitRepository extends Identifiable {
    /**
     * @return the local file path of the git repository
     */
    String getLocalPath();
}
