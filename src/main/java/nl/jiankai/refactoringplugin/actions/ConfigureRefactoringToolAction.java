package nl.jiankai.refactoringplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import nl.jiankai.refactoringplugin.configuration.PluginConfiguration;
import nl.jiankai.refactoringplugin.dialogs.RefactoringToolConfigurationDialog;
import nl.jiankai.refactoringplugin.git.GitRepositoryManager;
import nl.jiankai.refactoringplugin.storage.EntityStorageService;
import nl.jiankai.refactoringplugin.storage.FileRepositoryStorageService;
import nl.jiankai.refactoringplugin.storage.LocalFileStorageService;
import nl.jiankai.refactoringplugin.storage.RepositoryDetails;
import org.jetbrains.annotations.NotNull;

public class ConfigureRefactoringToolAction extends AnAction {
    private PluginConfiguration pluginConfiguration;

    public ConfigureRefactoringToolAction() {
        pluginConfiguration = ApplicationManager.getApplication().getService(PluginConfiguration.class);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        EntityStorageService<RepositoryDetails> entityStorageService = new FileRepositoryStorageService(new LocalFileStorageService(pluginConfiguration.pluginRepositoryUrlsLocation(), true));
        entityStorageService.addListener(ApplicationManager.getApplication().getService(GitRepositoryManager.class));
        new RefactoringToolConfigurationDialog(entityStorageService).show();
    }
}
