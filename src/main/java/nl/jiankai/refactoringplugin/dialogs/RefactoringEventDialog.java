package nl.jiankai.refactoringplugin.dialogs;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import nl.jiankai.refactoringplugin.refactoring.ProjectImpactInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class RefactoringEventDialog extends DialogWrapper {

    public RefactoringEventDialog(Collection<ProjectImpactInfo> impacts) {
        super(true);
        setTitle("Code Affected by Refactoring Action");
        setSize(500, 300);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();

        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        JList<String> list = new JBList<>(defaultListModel);
        defaultListModel.addElement("test 1");
        defaultListModel.addElement("test 2");
        defaultListModel.addElement("test 3");
        defaultListModel.addElement("test 4");
        defaultListModel.addElement("test 5");
        dialogPanel.add(list);
        return dialogPanel;
    }
}
