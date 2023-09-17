package nl.jiankai.refactoringplugin.storage.filestorage.refactoringcache;

import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.dependencymanagement.Project;
import nl.jiankai.refactoringplugin.refactoring.ProjectImpactInfo;
import nl.jiankai.refactoringplugin.serialisation.SerializationService;
import nl.jiankai.refactoringplugin.storage.api.EntityStorageService;
import nl.jiankai.refactoringplugin.storage.api.StorageListener;
import nl.jiankai.refactoringplugin.storage.api.StorageService;
import nl.jiankai.refactoringplugin.storage.filestorage.LocalFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RefactoringImpactStorageService implements EntityStorageService<ProjectImpactInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefactoringImpactStorageService.class);
    private PluginConfiguration pluginConfiguration;
    private SerializationService serializationService;

    public RefactoringImpactStorageService(SerializationService serializationService) {
        this.pluginConfiguration = new PluginConfiguration();
        this.serializationService = serializationService;
    }

    @Override
    public Optional<ProjectImpactInfo> read(String identifier) {
        LocalFileStorageService storageService = new LocalFileStorageService(createFileLocation(identifier), false);

        ProjectImpactInfo projectImpactInfo =
                serializationService
                        .deserialize(
                                storageService
                                        .read()
                                        .collect(Collectors.joining())
                                        .getBytes(),
                                ProjectImpactInfo.class);
        return Optional.of(projectImpactInfo);
    }

    @Override
    public boolean exists(String identifier) {
        return new File(createFileLocation(identifier)).exists();
    }

    @Override
    public void remove(ProjectImpactInfo entity) {

    }

    @Override
    public void remove(String identifier) {

    }

    @Override
    public void remove(Collection<ProjectImpactInfo> entities) {

    }

    @Override
    public void remove(List<String> identifiers) {

    }

    @Override
    public void addListener(StorageListener<ProjectImpactInfo> listener) {

    }

    @Override
    public void removeListener(StorageListener<ProjectImpactInfo> listener) {

    }

    @Override
    public Stream<ProjectImpactInfo> read() {
        return null;
    }

    @Override
    public void write(ProjectImpactInfo content) {
        LocalFileStorageService localFileStorageService = new LocalFileStorageService(createFileLocation(content), true);
        localFileStorageService.write(new String(serializationService.serialize(content)));
    }

    @Override
    public void write(List<ProjectImpactInfo> list) {

    }

    @Override
    public void append(ProjectImpactInfo content) {

    }

    @Override
    public void append(List<ProjectImpactInfo> list) {

    }

    @Override
    public void clear() {
        if (new File(pluginConfiguration.cacheDirectory()).delete()) {
            LOGGER.info("Successfully deleted refactoring result cache directory");
        } else {
            LOGGER.warn("Could not delete refactoring result cache directory at location {}", pluginConfiguration.cacheDirectory());
        }
    }

    private String createFileLocation(String filename) {
        return pluginConfiguration.cacheDirectory() + File.separator + filename.hashCode();
    }

    private String createFileLocation(ProjectImpactInfo projectImpactInfo) {
        return pluginConfiguration.cacheDirectory() + File.separator + projectImpactInfo.getId().hashCode();
    }
}
