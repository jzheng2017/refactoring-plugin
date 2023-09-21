package nl.jiankai.refactoringplugin.storage.filestorage.repository;

import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.project.CompositeProjectFactory;
import nl.jiankai.refactoringplugin.project.Project;
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
    public String target(Project source) {
        return source.toString();
    }

    @Override
    public Project source(String target) {
        return new CompositeProjectFactory().createProject(new File(target));
    }
}
