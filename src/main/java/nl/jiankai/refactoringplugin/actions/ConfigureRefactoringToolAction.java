package nl.jiankai.refactoringplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.jiankai.refactoringplugin.dialogs.RefactoringToolConfigurationDialog;
import nl.jiankai.refactoringplugin.storage.FileRepositoryStorageService;
import nl.jiankai.refactoringplugin.storage.LocalFileStorageService;
import org.jetbrains.annotations.NotNull;

public class ConfigureRefactoringToolAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new RefactoringToolConfigurationDialog(new FileRepositoryStorageService(new LocalFileStorageService("/home/jiankai/Documents/repositories.txt", true))).show();
    }
}
