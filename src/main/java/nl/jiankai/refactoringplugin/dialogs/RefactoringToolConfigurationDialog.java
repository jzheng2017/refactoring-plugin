package nl.jiankai.refactoringplugin.dialogs;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.thaiopensource.xml.dtd.om.Def;
import net.miginfocom.swing.MigLayout;
import nl.jiankai.refactoringplugin.project.Project;
import nl.jiankai.refactoringplugin.project.ProjectListener;
import nl.jiankai.refactoringplugin.project.ProjectManager;
import nl.jiankai.refactoringplugin.storage.api.EntityStorageService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RefactoringToolConfigurationDialog extends DialogWrapper implements ProjectListener<Project> {
    private EntityStorageService<Project> projectsToScanStorageService;
    private ProjectManager projectManager;
    private DefaultListModel<Project> allProjectsModel;
    private DefaultListModel<Project> projectsToScanModel;

    public RefactoringToolConfigurationDialog(EntityStorageService<Project> projectsToScanStorageService, ProjectManager projectManager) {
        super(true);
        this.projectsToScanStorageService = projectsToScanStorageService;
        this.projectManager = projectManager;
        projectManager.addListener(this);
        setTitle("Configure Repositories");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        JLabel allProjectsLabel = new JLabel();
        JLabel projectsToScanLabel = new JLabel();
        setupDialogPanel(dialogPanel);
        JButton addButton = new JButton("Add repository >");
        JTextField repositoryInput = new JTextField("Enter link...");

        JButton removeButton = new JButton("< Remove repository");

        projectsToScanModel = new DefaultListModel<>();
        JList<Project> projectsToScan = new JBList<>(projectsToScanModel);
        projectsToScanModel.addAll(getAllProjectsToScan());
        JScrollPane projectsToScanPane = new JBScrollPane();
        projectsToScanPane.setViewportView(projectsToScan);

        // all projects
        allProjectsModel = new DefaultListModel<>();
        JList<Project> allProjects = new JBList<>(allProjectsModel);
        allProjectsModel.addAll(getAllProjects());
        allProjects.setSize(new Dimension(300, 600));
        JScrollPane allProjectPane = new JBScrollPane();
        allProjectPane.setViewportView(allProjects);

        // projects to scan

        repositoryInput.setSize(new Dimension(150, 20));
        addButton.setSize(new Dimension(75, 20));
        removeButton.setSize(new Dimension(75, 20));

        registerComponentActions(addButton, removeButton, allProjects, projectsToScan);
        connectComponents(dialogPanel, allProjectPane, projectsToScanPane, addButton, removeButton, allProjectsLabel, projectsToScanLabel);
        return dialogPanel;
    }

    private void setupDialogPanel(JPanel dialogPanel) {
        dialogPanel.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]",
                // rows
                "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]"));
    }

    private List<Project> getAllProjectsToScan() {
        return projectsToScanStorageService.read().toList();
    }

    private void registerComponentActions(JButton addButton, JButton removeButton, JList<Project> allProjects, JList<Project> projectsToScan) {
        addButton.addActionListener(actionEvent -> {
            if (!allProjects.isSelectionEmpty() && !this.projectsToScanStorageService.exists(allProjects.getSelectedValue().getId())) {
                this.projectsToScanStorageService.append(allProjects.getSelectedValue());
                refreshViews();
            }
        });

        removeButton.addActionListener(event -> {
            if (projectsToScan.getSelectedIndex() >= 0 && !projectsToScanModel.isEmpty()) {
                this.projectsToScanStorageService.remove(projectsToScan.getSelectedValue());
                refreshViews();
            }
        });
    }

    private static void connectComponents(JPanel dialogPanel, JScrollPane allProjectsPane, JScrollPane projectsToScanPane, JButton addButton, JButton removeButton, JLabel allProjectsLabel, JLabel projectsToScanLabel) {
        allProjectsLabel.setText("All available projects");
        dialogPanel.add(allProjectsLabel, "cell 3 2 10 1");

        //---- label2 ----
        projectsToScanLabel.setText("Projects to scan for refactoring impact");
        dialogPanel.add(projectsToScanLabel, "cell 30 2 10 1");
        dialogPanel.add(allProjectsPane, "cell 3 3 10 12,grow");
        dialogPanel.add(projectsToScanPane, "cell 30 3 10 12,grow");
        dialogPanel.add(addButton, "cell 16 7 10 1");
        dialogPanel.add(removeButton, "cell 16 12 10 1");
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
        return filterOutAlreadyChosenProjects(projectManager.projects().values().stream());
    }

    private List<Project> filterOutAlreadyChosenProjects(Stream<Project> allProjects) {
        Set<Project> projectsToScan = Arrays.stream(projectsToScanModel.toArray()).map(s -> (Project) s).collect(Collectors.toSet());
        ;
        return allProjects
                .filter(Predicate.not(projectsToScan::contains))
                .collect(Collectors.toList());
    }

    private void refreshViews() {
        projectsToScanModel.clear();
        allProjectsModel.clear();
        projectsToScanModel.addAll(getAllProjectsToScan());
        allProjectsModel.addAll(getAllProjects());
    }
}
