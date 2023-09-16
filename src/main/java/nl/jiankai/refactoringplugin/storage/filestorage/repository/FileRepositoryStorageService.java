package nl.jiankai.refactoringplugin.storage.filestorage.repository;

import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.storage.filestorage.LocalFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileRepositoryStorageService extends RepositoryStorageService<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileRepositoryStorageService.class);
    public PluginConfiguration pluginConfiguration;
    public FileRepositoryStorageService(LocalFileStorageService storageService) {
        super(storageService);
        this.pluginConfiguration = new PluginConfiguration();
    }

    @Override
    public String target(RepositoryDetails source) {
        return source.toString();
    }

    @Override
    public RepositoryDetails source(String target) {
        return new RepositoryDetails(target);
    }

    @Override
    public boolean exists(String identifier) {
        LOGGER.warn("Provided identifier '{}' is not needed and will be ignored", identifier);
        return new File(pluginConfiguration.pluginRepositoryUrlsLocation()).exists();
    }
}
