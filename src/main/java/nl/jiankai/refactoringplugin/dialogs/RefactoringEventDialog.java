package nl.jiankai.refactoringplugin.dialogs;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import nl.jiankai.refactoringplugin.refactoring.Project;
import nl.jiankai.refactoringplugin.refactoring.ProjectImpactInfo;
import nl.jiankai.refactoringplugin.refactoring.RefactoringImpact;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RefactoringEventDialog extends DialogWrapper {
    private final ProjectImpactInfo impactInfo;
    public RefactoringEventDialog(ProjectImpactInfo impactInfo) {
        super(true);
        this.impactInfo = impactInfo;
        setTitle("Code Affected by Refactoring Action");
        setSize(500, 300);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();

        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        JList<String> list = new JBList<>(defaultListModel);
        for (Map.Entry<Project, List<RefactoringImpact>> projectImpact: impactInfo.refactoringImpacts().entrySet()) {
            defaultListModel.addElement("--- %s ---".formatted(projectImpact.getKey().toString()));
            for (RefactoringImpact refactoringImpact: projectImpact.getValue()) {
                defaultListModel.addElement("package: %s | class: %s | line %s position %s".formatted(refactoringImpact.packageLocation(), refactoringImpact.className(), refactoringImpact.position().rowStart(), refactoringImpact.position().columnStart()));
            }
        }
        dialogPanel.add(list);
        return dialogPanel;
    }
}
