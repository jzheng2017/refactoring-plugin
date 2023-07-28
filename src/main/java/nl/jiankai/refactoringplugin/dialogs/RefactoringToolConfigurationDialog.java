package nl.jiankai.refactoringplugin.dialogs;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import nl.jiankai.refactoringplugin.storage.RepositoryDetails;
import nl.jiankai.refactoringplugin.storage.StorageService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class RefactoringToolConfigurationDialog extends DialogWrapper {
    private StorageService<RepositoryDetails> storageService;

    public RefactoringToolConfigurationDialog(StorageService<RepositoryDetails> storageService) {
        super(true);
        this.storageService = storageService;
        setTitle("Configure Repositories");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        JPanel controlsPanel = new JPanel();
        JButton addButton = new JButton("Add repository");
        JTextField repositoryInput = new JTextField("Repository link");
        JButton removeButton = new JButton("Remove repository");

        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        JList<String> list = new JBList<>(defaultListModel);

        addButton.setSize(new Dimension(100, 20));
        removeButton.setSize(new Dimension(100, 20));

        populateComponent(defaultListModel);
        registerComponentActions(addButton, repositoryInput, defaultListModel, removeButton, list);
        connectComponents(dialogPanel, list, controlsPanel, repositoryInput, addButton, removeButton);
        return dialogPanel;
    }

    private void populateComponent(DefaultListModel<String> defaultListModel) {
        defaultListModel.addAll(storageService.read().map(RepositoryDetails::toString).toList());
    }

    private void registerComponentActions(JButton addButton, JTextField repositoryInput, DefaultListModel<String> defaultListModel, JButton removeButton, JList<String> list) {
        addButton.addActionListener(actionEvent -> {
            if (!repositoryInput.getText().isBlank()) {
                defaultListModel.addElement(repositoryInput.getText());
                storageService.append(new RepositoryDetails(repositoryInput.getText()));
                repositoryInput.setText("");
            }
        });

        removeButton.addActionListener(event -> {
            if (list.getSelectedIndex() >= 0 && !defaultListModel.isEmpty()) {
                defaultListModel.remove(list.getSelectedIndex());
                storageService.write(getRepositoryDetails(defaultListModel));
            }
        });
    }

    private static void connectComponents(JPanel dialogPanel, JList<String> list, JPanel controlsPanel, JTextField repositoryInput, JButton addButton, JButton removeButton) {
        dialogPanel.add(list, BorderLayout.NORTH);
        dialogPanel.add(controlsPanel, BorderLayout.SOUTH);
        controlsPanel.add(repositoryInput, BorderLayout.NORTH);
        controlsPanel.add(addButton, BorderLayout.WEST);
        controlsPanel.add(removeButton, BorderLayout.EAST);
    }

    private List<RepositoryDetails> getRepositoryDetails(DefaultListModel<String> defaultListModel) {
        return Arrays
                .stream(defaultListModel.toArray())
                .map(repository -> new RepositoryDetails((String) repository))
                .toList();
    }
}
