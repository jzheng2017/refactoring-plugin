package nl.jiankai.refactoringplugin.refactoring;

import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.storage.api.StorageListener;
import nl.jiankai.refactoringplugin.storage.filestorage.LocalFileStorageService;
import nl.jiankai.refactoringplugin.storage.filestorage.repository.FileProjectStorageService;
import nl.jiankai.refactoringplugin.storage.filestorage.repository.ProjectStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectsToScan implements StorageListener<Project> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectsToScan.class);
    private Set<Project> projects = new HashSet<>();
    private final ProjectStorageService<String> projectStorageService;

    public ProjectsToScan() {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        projectStorageService = new FileProjectStorageService(new LocalFileStorageService(pluginConfiguration.pluginProjectsToScanLocation(), false));
        projectStorageService.addListener(this);
    }

    public List<Project> projects() {
        if (projects.isEmpty()) {
            projects = projectStorageService.read().collect(Collectors.toSet());
        }

        return List.copyOf(projects);
    }

    @Override
    public void onAdded(StorageEvent<Project> event) {
        Project project = event.affected();
        if (projects.contains(project)) {
            LOGGER.warn("Project '{}' has already been added", project);
        } else {
            projects.add(project);
        }
    }

    @Override
    public void onUpdated(StorageEvent<Project> event) {

    }

    @Override
    public void onRemoved(StorageEvent<Project> event) {
        Project project = event.affected();
        if (projects.contains(project)) {
            projects.remove(project);
        } else {
            LOGGER.warn("Project '{}' does not exist", project);
        }
    }
}
