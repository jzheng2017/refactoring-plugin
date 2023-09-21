package nl.jiankai.refactoringplugin.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.project.dependencymanagement.Project;
import nl.jiankai.refactoringplugin.project.ProjectListener;
import nl.jiankai.refactoringplugin.project.ProjectManager;
import nl.jiankai.refactoringplugin.serialisation.JacksonSerializationService;
import nl.jiankai.refactoringplugin.storage.filestorage.refactoringcache.RefactoringImpactStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CachedRefactoringImpactAssessor implements RefactoringImpactAssessor, ProjectListener<nl.jiankai.refactoringplugin.project.Project> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedRefactoringImpactAssessor.class);
    private Map<RefactoringKey, List<RefactoringImpact>> refactoringImpactCache = new HashMap<>();
    private RefactoringImpactAssessor refactoringImpactAssessor;
    private RefactoringImpactStorageService refactoringImpactStorageService;
    private PluginConfiguration pluginConfiguration;
    private ProjectsToScan projectsToScan;

    public CachedRefactoringImpactAssessor(RefactoringImpactAssessor refactoringImpactAssessor) {
        if (CachedRefactoringImpactAssessor.class.equals(refactoringImpactAssessor.getClass())) {
            throw new IllegalArgumentException("Can not inject '%s' into itself!".formatted(refactoringImpactAssessor.getClass()));
        }
        this.projectsToScan = new ProjectsToScan();
        this.refactoringImpactStorageService = new RefactoringImpactStorageService(new JacksonSerializationService());
        this.refactoringImpactAssessor = refactoringImpactAssessor;
        this.pluginConfiguration = new PluginConfiguration();
    }

    @Override
    public ImpactAssessment assesImpact(RefactoringData refactoringData) {
        Map<Project, List<RefactoringImpact>> impacts = projectsToScan
                .projects()
                .stream()
                .map(nl.jiankai.refactoringplugin.project.Project::getProjectVersion)
                .map(project -> new ProjectImpactInfo(project, refactoringData, assesImpact(project, refactoringData)))
                .collect(Collectors.toMap(ProjectImpactInfo::project, ProjectImpactInfo::refactoringImpacts));

        return new ImpactAssessment(impacts, RefactoringStatisticsGenerator.compute(impacts));
    }

    @Override
    public List<RefactoringImpact> assesImpact(Project project, RefactoringData refactoringData) {
        final RefactoringKey refactoringKey = createRefactoringKey(project, refactoringData);
        if (isCached(refactoringKey)) {
            LOGGER.info("Project {} is cached. Trying to fetch from cache...", project);
            return getFromCache(project, refactoringData).orElseGet(() -> {
                LOGGER.warn("Could not find project {} in the cache. The refactoring impact will be recomputed again.", project);
                return computeRefactoringImpacts(project, refactoringData);
            });
        } else {
            return computeRefactoringImpacts(project, refactoringData);
        }
    }

    private boolean isCached(RefactoringKey key) {
        return refactoringImpactCache.containsKey(key) || refactoringImpactStorageService.exists(key.toString());
    }

    private boolean shouldCache(Project project) {
        return !project.version().endsWith("-SNAPSHOT");
    }

    private void cacheIfNeeded(Project project, RefactoringData refactoringData, List<RefactoringImpact> refactoringImpacts) {
        if (shouldCache(project)) {
            refactoringImpactCache.put(createRefactoringKey(project, refactoringData), refactoringImpacts);
            refactoringImpactStorageService.write(new ProjectImpactInfo(project, refactoringData, refactoringImpacts));
        }
    }

    private Optional<List<RefactoringImpact>> getFromCache(Project project, RefactoringData refactoringData) {
        final RefactoringKey refactoringKey = createRefactoringKey(project, refactoringData);
        Optional<List<RefactoringImpact>> refactoringImpacts =
                Optional.ofNullable(
                        Optional
                                .ofNullable(refactoringImpactCache.get(refactoringKey)) // try in memory cache first
                                .orElseGet(() -> { //if not in memory then try on disk
                                    LOGGER.info("Refactoring results could not be found in the memory cache. Trying disk cache...");
                                    Optional<ProjectImpactInfo> projectImpactInfo = refactoringImpactStorageService.read(refactoringKey.toString());
                                    projectImpactInfo.ifPresent(p -> LOGGER.info("Refactoring results found on the disk cache!"));
                                    return projectImpactInfo.orElse(new ProjectImpactInfo(project, refactoringData, null)).refactoringImpacts();
                                }));

        refactoringImpacts.ifPresent(p -> LOGGER.info("Found project {} in cache", project));
        return refactoringImpacts;
    }

    private List<RefactoringImpact> computeRefactoringImpacts(Project project, RefactoringData refactoringData) {
        List<RefactoringImpact> refactoringImpacts = refactoringImpactAssessor.assesImpact(project, refactoringData);
        cacheIfNeeded(project, refactoringData, refactoringImpacts);
        return refactoringImpacts;
    }

    @Override
    public void onAdded(ProjectEvent<nl.jiankai.refactoringplugin.project.Project> event) {
        clearCache();
    }

    @Override
    public void onRemoved(ProjectEvent<nl.jiankai.refactoringplugin.project.Project> event) {
        clearCache();
    }

    private void clearCache() {
        refactoringImpactCache.clear();
        refactoringImpactStorageService.clear();
    }

    private RefactoringKey createRefactoringKey(Project project, RefactoringData refactoringData) {
        return new RefactoringKey(project, refactoringData.fullyQualifiedSignature(), refactoringData.refactoringType());
    }

    private record RefactoringKey(Project project, String fullyQualifiedSignature, RefactoringType refactoringType) {

        @Override
        public String toString() {
            return project.toString() + "-" + fullyQualifiedSignature + "-" + refactoringType;
        }
    }
}
