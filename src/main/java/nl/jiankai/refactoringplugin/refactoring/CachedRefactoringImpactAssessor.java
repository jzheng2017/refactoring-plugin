package nl.jiankai.refactoringplugin.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.dependencymanagement.Project;
import nl.jiankai.refactoringplugin.dependencymanagement.ProjectDependencyResolver;
import nl.jiankai.refactoringplugin.git.GitRepository;
import nl.jiankai.refactoringplugin.git.GitRepositoryListener;
import nl.jiankai.refactoringplugin.git.GitRepositoryManager;
import nl.jiankai.refactoringplugin.serialisation.JacksonSerializationService;
import nl.jiankai.refactoringplugin.storage.filestorage.refactoringcache.RefactoringImpactStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CachedRefactoringImpactAssessor implements RefactoringImpactAssessor, GitRepositoryListener<GitRepository> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedRefactoringImpactAssessor.class);
    private Map<RefactoringKey, List<RefactoringImpact>> refactoringImpactCache = new HashMap<>();
    private RefactoringImpactAssessor refactoringImpactAssessor;
    private GitRepositoryManager gitRepositoryManager;
    private RefactoringImpactStorageService refactoringImpactStorageService;
    private ProjectDependencyResolver dependencyResolver;
    private PluginConfiguration pluginConfiguration;

    public CachedRefactoringImpactAssessor(RefactoringImpactAssessor refactoringImpactAssessor, ProjectDependencyResolver dependencyResolver) {
        if (CachedRefactoringImpactAssessor.class.equals(refactoringImpactAssessor.getClass())) {
            throw new IllegalArgumentException("Can not inject '%s' into itself!".formatted(refactoringImpactAssessor.getClass()));
        }

        this.gitRepositoryManager = ApplicationManager.getApplication().getService(GitRepositoryManager.class);
        this.gitRepositoryManager.addListener(this);
        this.refactoringImpactStorageService = new RefactoringImpactStorageService(new JacksonSerializationService());
        this.dependencyResolver = dependencyResolver;
        this.refactoringImpactAssessor = refactoringImpactAssessor;
        this.pluginConfiguration = new PluginConfiguration();
    }

    @Override
    public ImpactAssessment assesImpact(RefactoringData refactoringData) {
        Map<Project, List<RefactoringImpact>> impacts = gitRepositoryManager
                .gitRepositories()
                .values()
                .stream()
                .map(repository -> dependencyResolver.getProjectVersion(new File(repository.getLocalPath())))
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
        return true;
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
    public void onAdded(GitRepositoryEvent<GitRepository> event) {
        clearCache();
    }

    @Override
    public void onRemoved(GitRepositoryEvent<GitRepository> event) {
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
