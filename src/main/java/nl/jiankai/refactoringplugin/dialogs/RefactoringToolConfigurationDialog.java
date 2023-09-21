package nl.jiankai.refactoringplugin.dialogs;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.project.ProjectListener;
import nl.jiankai.refactoringplugin.project.ProjectManager;
import nl.jiankai.refactoringplugin.storage.api.EntityStorageService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RefactoringToolConfigurationDialog extends DialogWrapper implements ProjectListener<Project> {
    private EntityStorageService<Project> storageService;
    private ProjectManager projectManager;
    private DefaultListModel<Project> allProjectsModel;

    public RefactoringToolConfigurationDialog(EntityStorageService<Project> storageService, ProjectManager projectManager) {
        super(true);
        this.storageService = storageService;
        this.projectManager = projectManager;
        projectManager.addListener(this);
        setTitle("Configure Repositories");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        JPanel controlsPanel = new JPanel();
        JButton addButton = new JButton("Add repository");
        JTextField repositoryInput = new JTextField("Enter link...");
//        addButton.setEnabled(false);
        JButton removeButton = new JButton("Remove repository");

        // all projects
        allProjectsModel = new DefaultListModel<>();
        JList<Project> allProjects = new JBList<>(allProjectsModel);
        allProjectsModel.addAll(getAllProjects());
        allProjects.setSize(new Dimension(300, 600));

        // projects to scan
        DefaultListModel<Project> projectsToScanModel = new DefaultListModel<>();
        JList<Project> projectsToScan = new JBList<>(projectsToScanModel);
        projectsToScanModel.addAll(storageService.read().toList());

        repositoryInput.setSize(new Dimension(150, 20));
        addButton.setSize(new Dimension(75, 20));
        removeButton.setSize(new Dimension(75, 20));

        populateComponent(allProjectsModel);
        registerComponentActions(addButton, repositoryInput, allProjectsModel, projectsToScanModel, removeButton, allProjects, projectsToScan);
        connectComponents(dialogPanel, allProjects, projectsToScan, controlsPanel, repositoryInput, addButton, removeButton);
        return dialogPanel;
    }

    private void populateComponent(DefaultListModel<Project> defaultListModel) {
        defaultListModel.addAll(storageService.read().toList());
    }

    private void registerComponentActions(JButton addButton, JTextField repositoryInput, DefaultListModel<Project> allProjectsModel, DefaultListModel<Project> projectsToScanModel, JButton removeButton, JList<Project> allProjects, JList<Project> projectsToScan) {
        addButton.addActionListener(actionEvent -> {
            if (!allProjects.isSelectionEmpty()) {
                storageService.append(allProjects.getSelectedValue());
                projectsToScanModel.addElement(allProjects.getSelectedValue());
            }
        });

        removeButton.addActionListener(event -> {
            if (projectsToScan.getSelectedIndex() >= 0 && !projectsToScanModel.isEmpty()) {
                storageService.remove(projectsToScan.getSelectedValue());
                projectsToScanModel.remove(projectsToScan.getSelectedIndex());
            }
        });
    }

    private static void connectComponents(JPanel dialogPanel, JList<Project> list, JList<Project> projectsToScan, JPanel controlsPanel, JTextField repositoryInput, JButton addButton, JButton removeButton) {
        dialogPanel.add(list, BorderLayout.NORTH);
        controlsPanel.add(addButton, BorderLayout.NORTH);
        dialogPanel.add(controlsPanel, BorderLayout.SOUTH);
//        controlsPanel.add(repositoryInput, BorderLayout.NORTH);
        dialogPanel.add(projectsToScan, BorderLayout.SOUTH);
        controlsPanel.add(removeButton, BorderLayout.SOUTH);
    }

    @Override
    public void onAdded(ProjectEvent<Project> event) {
        allProjectsModel.clear();
        allProjectsModel.addAll(getAllProjects());
    }

    @Override
    public void onRemoved(ProjectEvent<Project> event) {
        allProjectsModel.clear();
        allProjectsModel.addAll(getAllProjects());
    }

    private List<Project> getAllProjects() {
        return projectManager.projects().values().stream().toList();
    }
}
