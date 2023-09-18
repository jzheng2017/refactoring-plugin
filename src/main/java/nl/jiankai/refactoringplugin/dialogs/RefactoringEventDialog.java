package nl.jiankai.refactoringplugin.dialogs;

import com.intellij.ide.DataManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBList;
import nl.jiankai.refactoringplugin.project.dependencymanagement.Project;
import nl.jiankai.refactoringplugin.refactoring.ImpactAssessment;
import nl.jiankai.refactoringplugin.refactoring.RefactoringImpact;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RefactoringEventDialog extends DialogWrapper {
    private final ImpactAssessment impactInfo;

    public RefactoringEventDialog(ImpactAssessment impactInfo) {
        super(true);
        this.impactInfo = impactInfo;
        setTitle("Code Affected by Refactoring Action");
        setSize(1000, 1000);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();

        DefaultListModel<RefactoringImpact> defaultListModel = new DefaultListModel<>();
        JList<RefactoringImpact> list = new JBList<>(defaultListModel);
        for (Map.Entry<Project, List<RefactoringImpact>> projectImpact : impactInfo.refactoringImpacts().entrySet()) {
            for (RefactoringImpact refactoringImpact : projectImpact.getValue()) {
                defaultListModel.addElement(refactoringImpact);
            }
        }
        list.setSize(800, 300);

        EditorTextField editorTextField;
        try {
            com.intellij.openapi.project.Project project = DataManager.getInstance().getDataContextFromFocusAsync().blockingGet(1).getData(CommonDataKeys.PROJECT);
            editorTextField = new EditorTextField(project, JavaFileType.INSTANCE);
        } catch (TimeoutException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        editorTextField.setSize(1000, 700);
        editorTextField.setVisible(false);
        editorTextField.setOneLineMode(false);
        editorTextField.setViewer(true);
        editorTextField.setFileType(JavaFileType.INSTANCE);
        dialogPanel.add(editorTextField);

        dialogPanel.add(list, BorderLayout.PAGE_START);
        EditorTextField finalEditorTextField = editorTextField;
        list.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        RefactoringImpact refactoringImpact = list.getSelectedValue();
                        VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByNioPath(Path.of(refactoringImpact.filePath()));
                        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                        finalEditorTextField.setDocument(document);
                        Position elementPosition = computeElementPosition(refactoringImpact.position(), document);
                        finalEditorTextField.getEditor().getSelectionModel().setSelection(elementPosition.startOffset, elementPosition.endOffset);
                        finalEditorTextField.setVisible(true);
                    }
                }
        );
        return dialogPanel;
    }

    private Position computeElementPosition(RefactoringImpact.Position position, Document document) {
        int start = document.getLineStartOffset(Math.max(0, position.rowStart() - 1));
        return new Position(start + position.columnStart() - 1, start + position.columnEnd());
    }

    private record Position(int startOffset, int endOffset) {
    }
}
