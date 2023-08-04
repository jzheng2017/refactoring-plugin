package nl.jiankai.refactoringplugin.dialogs;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import nl.jiankai.refactoringplugin.storage.RepositoryDetails;
import nl.jiankai.refactoringplugin.storage.StorageService;
import nl.jiankai.refactoringplugin.util.HttpUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
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
        JTextField repositoryInput = new JTextField("Enter link...");
        addButton.setEnabled(false);
        JButton removeButton = new JButton("Remove repository");
        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        JList<String> list = new JBList<>(defaultListModel);



        list.setSize(new Dimension(300, 600));
        repositoryInput.setSize(new Dimension(150, 20));
        addButton.setSize(new Dimension(75, 20));
        removeButton.setSize(new Dimension(75, 20));

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
                storageService.append(new RepositoryDetails(repositoryInput.getText()));
                defaultListModel.addElement(repositoryInput.getText());
                repositoryInput.setText("");
            }
        });

        repositoryInput.getDocument().addDocumentListener(new DocumentListener() {
            private Instant lastChange = Instant.now();
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateIfNeeded();
            }

            private void validateIfNeeded() {
                final boolean isHalfSecondAfterLastInputChange = Instant.now().isAfter(lastChange.plusMillis(500));
                lastChange = Instant.now();
                if (isHalfSecondAfterLastInputChange) {
                    disableAddButtonIfInvalidUrl();
                }

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateIfNeeded();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateIfNeeded();
            }

            private void disableAddButtonIfInvalidUrl() {
                addButton.setEnabled(HttpUtil.validUrl(repositoryInput.getText()));
            }
        });

        removeButton.addActionListener(event -> {
            if (list.getSelectedIndex() >= 0 && !defaultListModel.isEmpty()) {
                List<String> repos = new ArrayList<>(Arrays.stream((String[]) defaultListModel.toArray()).toList());
                repos.remove(list.getSelectedValue());
                storageService.write(getRepositoryDetails(repos));
                defaultListModel.remove(list.getSelectedIndex());
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

    private List<RepositoryDetails> getRepositoryDetails(List<String> repo) {
        return repo
                .stream()
                .map(repository -> new RepositoryDetails((String) repository))
                .toList();
    }
}
