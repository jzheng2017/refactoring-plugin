package nl.jiankai.refactoringplugin.storage.filestorage.repository;

import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.storage.filestorage.LocalFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileProjectStorageService extends ProjectStorageService<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProjectStorageService.class);
    public PluginConfiguration pluginConfiguration;
    public FileProjectStorageService(LocalFileStorageService storageService) {
        super(storageService);
        this.pluginConfiguration = new PluginConfiguration();
    }

    @Override
    public String target(ProjectDetails source) {
        return source.toString();
    }

    @Override
    public ProjectDetails source(String target) {
        return new ProjectDetails(target);
    }

    @Override
    public boolean exists(String identifier) {
        LOGGER.warn("Provided identifier '{}' is not needed and will be ignored", identifier);
        return new File(pluginConfiguration.pluginProjectUrlsLocation()).exists();
    }
}
