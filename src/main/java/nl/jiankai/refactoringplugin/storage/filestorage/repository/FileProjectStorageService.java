package nl.jiankai.refactoringplugin.storage.filestorage.repository;

import nl.jiankai.refactoringplugin.configuration.ApplicationConfiguration;
import nl.jiankai.refactoringplugin.project.CompositeProjectFactory;
import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.storage.filestorage.LocalFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileProjectStorageService extends ProjectStorageService<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProjectStorageService.class);
    public ApplicationConfiguration applicationConfiguration;
    public FileProjectStorageService(LocalFileStorageService storageService) {
        super(storageService);
        this.applicationConfiguration = new ApplicationConfiguration();
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
