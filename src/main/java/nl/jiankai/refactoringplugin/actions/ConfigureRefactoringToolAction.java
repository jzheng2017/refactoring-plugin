package nl.jiankai.refactoringplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.dialogs.RefactoringToolConfigurationDialog;
import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.project.ProjectManager;
import nl.jiankai.refactoringplugin.storage.api.EntityStorageService;
import nl.jiankai.refactoringplugin.storage.filestorage.repository.FileProjectStorageService;
import nl.jiankai.refactoringplugin.storage.filestorage.LocalFileStorageService;
import org.jetbrains.annotations.NotNull;

public class ConfigureRefactoringToolAction extends AnAction {
    private PluginConfiguration pluginConfiguration;

    public ConfigureRefactoringToolAction() {
        pluginConfiguration = new PluginConfiguration();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        EntityStorageService<Project> projectToScanStorageService = new FileProjectStorageService(new LocalFileStorageService(pluginConfiguration.pluginProjectsToScanLocation(), true));
        ProjectManager projectManager = ApplicationManager.getApplication().getService(ProjectManager.class);
        new RefactoringToolConfigurationDialog(projectToScanStorageService, projectManager).show();
    }
}
