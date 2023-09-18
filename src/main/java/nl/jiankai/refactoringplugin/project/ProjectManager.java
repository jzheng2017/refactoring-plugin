package nl.jiankai.refactoringplugin.project;

import com.intellij.openapi.components.Service;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.project.dependencymanagement.MavenProjectDependencyResolver;
import nl.jiankai.refactoringplugin.project.dependencymanagement.ProjectDependencyResolver;
import nl.jiankai.refactoringplugin.project.git.GitUtil;
import nl.jiankai.refactoringplugin.storage.filestorage.repository.ProjectDetails;
import nl.jiankai.refactoringplugin.storage.api.StorageListener;
import nl.jiankai.refactoringplugin.tasks.ScheduledTask;
import nl.jiankai.refactoringplugin.tasks.ScheduledTaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public final class ProjectManager implements StorageListener<ProjectDetails>, ProjectObservable<Project> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectManager.class);
    private final Map<String, Project> projects = new HashMap<>();
    private PluginConfiguration pluginConfiguration;
    private ScheduledTaskExecutorService<Void> executorService = new ScheduledTaskExecutorService<>();
    private ProjectDiscovery projectDiscovery = new LocalFileProjectDiscovery();
    private ProjectDependencyResolver projectDependencyResolver;
    private List<ProjectListener<Project>> listeners = new ArrayList<>();
    public ProjectManager() {
        pluginConfiguration = new PluginConfiguration();
        projectDependencyResolver = new MavenProjectDependencyResolver();

        discoverProjects();
        submitProjectDiscoverTask();
    }

    private void submitProjectDiscoverTask() {
        executorService.executeRecurringTask(
                ScheduledTask.builder(Void.class)
                        .task(() -> {
                            discoverProjects();
                            return null;
                        })
                        .period(10)
                        .periodTimeUnit(TimeUnit.SECONDS)
                        .recurring()
                        .build()
        );
    }

    private void discoverProjects() {
        LOGGER.trace("Discovering projects...");
        try {
            synchronized (projects) {
                Map<String, Project> existingGitRepositories = new HashMap<>(projects);
                projectDiscovery
                        .discover()
                        .parallel()
                        .forEach(project -> {
                            String projectId = project.getId();
                            existingGitRepositories.remove(projectId);

                            if (!projects.containsKey(projectId)) {
                                LOGGER.info("Discovered new project: '{}'", projectId);
                                projects.put(projectId, project);
                                project.install();
                                notifyAdded(project);
                            }
                        });

                existingGitRepositories.values().forEach(gitRepository -> {
                    String gitRepositoryId = gitRepository.getId();
                    projects.remove(gitRepositoryId);
                    LOGGER.info("Project '{}' has been removed as it's not available anymore", gitRepositoryId);
                    notifyRemoved(gitRepository);
                });
            }
        } catch (Exception e) {
            LOGGER.warn("Something went wrong while discovering projects: {}", e.getMessage(), e);
        }
    }

    @Override
    public void onAdded(StorageEvent<ProjectDetails> event) {
        ProjectDetails projectDetails = event.affected();

        if (projects.containsKey(projectDetails.getId())) {
            LOGGER.warn("Project '{}' not added as it already exists", projectDetails.getId());
        } else {
            projects.put(projectDetails.getId(), GitUtil.clone(projectDetails.url(), new File(pluginConfiguration.pluginProjectsLocation() + File.separator + projectDetails.getId())));
        }
    }

    @Override
    public void onUpdated(StorageEvent<ProjectDetails> event) {

    }

    @Override
    public void onRemoved(StorageEvent<ProjectDetails> event) {
        ProjectDetails projectDetails = event.affected();
        String projectId = projectDetails.getId();

        if (projects.containsKey(projectId)) {
            LOGGER.info("Project '{}' will be removed", projectId);
            projects.remove(projectId);
        } else {
            LOGGER.warn("Tried to remove project '{}'. But it could not be found.", projectId);
        }
    }

    public Map<String, Project> projects() {
        return Map.copyOf(projects);
    }

    @Override
    public void addListener(ProjectListener<Project> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ProjectListener<Project> listener) {
        listeners.remove(listener);
    }

    public void notifyAdded(Project project) {
        listeners.forEach(listener -> listener.onAdded(new ProjectListener.ProjectEvent<>(project)));
    }
    public void notifyRemoved(Project project) {
        listeners.forEach(listener -> listener.onRemoved(new ProjectListener.ProjectEvent<>(project)));
    }
}
