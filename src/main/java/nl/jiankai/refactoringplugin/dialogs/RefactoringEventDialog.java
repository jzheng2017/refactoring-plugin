package nl.jiankai.refactoringplugin.dialogs;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RefactoringEventDialog extends DialogWrapper {

    public RefactoringEventDialog() {
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
