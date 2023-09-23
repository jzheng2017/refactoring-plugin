package nl.jiankai.refactoringplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import nl.jiankai.refactoringplugin.configuration.ApplicationConfiguration;
import nl.jiankai.refactoringplugin.dialogs.RefactoringToolConfigurationDialog;
import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.project.ProjectManager;
import nl.jiankai.refactoringplugin.storage.api.EntityStorageService;
import nl.jiankai.refactoringplugin.storage.filestorage.repository.FileProjectStorageService;
import nl.jiankai.refactoringplugin.storage.filestorage.LocalFileStorageService;
import org.jetbrains.annotations.NotNull;

public class ConfigureRefactoringToolAction extends AnAction {
    private ApplicationConfiguration applicationConfiguration;

    public ConfigureRefactoringToolAction() {
        applicationConfiguration = new ApplicationConfiguration();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        EntityStorageService<Project> projectToScanStorageService = new FileProjectStorageService(new LocalFileStorageService(applicationConfiguration.applicationProjectsToScanLocation(), true));
        ProjectManager projectManager = ApplicationManager.getApplication().getService(ProjectManager.class);
        new RefactoringToolConfigurationDialog(projectToScanStorageService, projectManager).show();
    }
}
