package ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import logic.SemesterManager;
import models.Semester;
import storage.StorageManager;

public class SemesterListFrame extends JFrame {
    private final SemesterManager semesterManager;
    private final DefaultListModel<Semester> semesterListModel;
    private final JList<Semester> semesterList;

    public SemesterListFrame(SemesterManager semesterManager) {
        super("Semesters");
        this.semesterManager = semesterManager;
        this.semesterListModel = new DefaultListModel<>();
        this.semesterList = new JList<>(semesterListModel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        semesterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        semesterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    openSelectedSemester();
                }
            }
        });

        add(new JScrollPane(semesterList), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
        refreshSemesterList();
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();

        JButton addSemesterButton = new JButton("Add Semester");
        JButton openSemesterButton = new JButton("Open Semester");
        JButton summaryButton = new JButton("Summary");

        addSemesterButton.addActionListener(event -> addSemester());
        openSemesterButton.addActionListener(event -> openSelectedSemester());
        summaryButton.addActionListener(event -> showSemesterSummary());

        panel.add(addSemesterButton);
        panel.add(openSemesterButton);
        panel.add(summaryButton);
        return panel;
    }

    private void addSemester() {
        String label = JOptionPane.showInputDialog(this, "Semester label", "Add Semester", JOptionPane.PLAIN_MESSAGE);
        if (label == null) {
            return;
        }

        try {
            Semester semester = semesterManager.addSemester(label);
            StorageManager.getInstance().save(semesterManager);
            refreshSemesterList();
            semesterList.setSelectedValue(semester, true);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Unable to Add Semester", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSemesterSummary() {
        Semester selected = semesterList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a semester first.", "No Semester Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, selected.getSummary(), "Summary — " + selected.getLabel(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void openSelectedSemester() {
        Semester selectedSemester = semesterList.getSelectedValue();
        if (selectedSemester == null) {
            JOptionPane.showMessageDialog(this, "Select a semester first.", "No Semester Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SemesterDetailFrame detailFrame = new SemesterDetailFrame(semesterManager, selectedSemester);
        detailFrame.setVisible(true);
    }

    private void refreshSemesterList() {
        semesterListModel.clear();
        for (Semester semester : semesterManager.getSemesters()) {
            semesterListModel.addElement(semester);
        }
    }
}
