package nl.jiankai.refactoringplugin.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.dependencymanagement.MavenProjectDependencyResolver;
import nl.jiankai.refactoringplugin.dependencymanagement.Project;
import nl.jiankai.refactoringplugin.dependencymanagement.ProjectDependencyResolver;
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

public class BaseRefactoringImpactAssessor implements RefactoringImpactAssessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRefactoringImpactAssessor.class);
    private Map<Project, List<RefactoringImpact>> refactoringImpactCache = new HashMap<>();
    private RefactoringImpactAssessor refactoringImpactAssessor;
    private GitRepositoryManager gitRepositoryManager;
    private RefactoringImpactStorageService refactoringImpactStorageService;
    private ProjectDependencyResolver dependencyResolver;
    private PluginConfiguration pluginConfiguration;

    public BaseRefactoringImpactAssessor(RefactoringImpactAssessor refactoringImpactAssessor) {
        if (BaseRefactoringImpactAssessor.class.equals(refactoringImpactAssessor.getClass())) {
            throw new IllegalArgumentException("Can not inject '%s' into itself!".formatted(refactoringImpactAssessor.getClass()));
        }

        this.gitRepositoryManager = ApplicationManager.getApplication().getService(GitRepositoryManager.class);
        this.refactoringImpactStorageService = new RefactoringImpactStorageService(new JacksonSerializationService());
        this.dependencyResolver = new MavenProjectDependencyResolver();
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
                .map(project -> new ProjectImpactInfo(project, assesImpact(project, refactoringData)))
                .collect(Collectors.toMap(ProjectImpactInfo::project, ProjectImpactInfo::refactoringImpacts));

        return new ImpactAssessment(impacts, RefactoringStatisticsGenerator.compute(impacts));
    }

    @Override
    public List<RefactoringImpact> assesImpact(Project project, RefactoringData refactoringData) {
        if (isCached(project)) {
            LOGGER.info("Project {} is cached. Trying to fetch from cache...", project);
            return getFromCache(project).orElseGet(() -> {
                LOGGER.warn("Could not find project {} in the cache. The refactoring impact will be recomputed again.", project);
                return computeRefactoringImpacts(project, refactoringData);
            });
        } else {
            return computeRefactoringImpacts(project, refactoringData);
        }
    }

    private boolean isCached(Project project) {
        return refactoringImpactCache.containsKey(project) || refactoringImpactStorageService.exists(project.toString());
    }

    private boolean shouldCache(Project project) {
        return true;
    }

    private void cacheIfNeeded(Project project, List<RefactoringImpact> refactoringImpacts) {
        if (shouldCache(project)) {
            refactoringImpactCache.put(project, refactoringImpacts);
            refactoringImpactStorageService.write(new ProjectImpactInfo(project, refactoringImpacts));
        }
    }

    private Optional<List<RefactoringImpact>> getFromCache(Project project) {
        Optional<List<RefactoringImpact>> refactoringImpacts =
                Optional.ofNullable(
                        Optional
                        .ofNullable(refactoringImpactCache.get(project)) // try in memory cache first
                        .orElseGet(() -> { //if not in memory then try on disk
                            Optional<ProjectImpactInfo> projectImpactInfo = refactoringImpactStorageService.read(project.toString());

                            return projectImpactInfo.orElse(new ProjectImpactInfo(project, null)).refactoringImpacts();
                        }));

        refactoringImpacts.ifPresent(p -> LOGGER.info("Found project {} in cache", p));
        return refactoringImpacts;
    }

    private List<RefactoringImpact> computeRefactoringImpacts(Project project, RefactoringData refactoringData) {
        List<RefactoringImpact> refactoringImpacts = refactoringImpactAssessor.assesImpact(project, refactoringData);
        cacheIfNeeded(project, refactoringImpacts);
        return refactoringImpacts;
    }
}
