package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import enums.StudentStatus;
import models.Course;
import models.Date;
import models.GradStudent;
import models.Student;
import models.UndergradStudent;

public class CourseDetailFrame extends JFrame {
    private final Course course;
    private final JLabel studentsSummaryLabel;
    private final JLabel assignmentsSummaryLabel;
    private final JLabel settingsSummaryLabel;

    public CourseDetailFrame(Course course) {
        super(course.getDisplayLabel());
        this.course = course;
        this.studentsSummaryLabel = new JLabel();
        this.assignmentsSummaryLabel = new JLabel();
        this.settingsSummaryLabel = new JLabel();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        Border framePadding = new EmptyBorder(12, 12, 12, 12);
        ((JPanel) getContentPane()).setBorder(framePadding);

        JLabel headerLabel = new JLabel(course.getDisplayLabel(), SwingConstants.CENTER);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(headerLabel, BorderLayout.NORTH);

        JPanel sectionsPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        sectionsPanel.add(buildSectionRow("Students", studentsSummaryLabel, "Open", this::openStudentsDialog));
        sectionsPanel.add(buildSectionRow("Assignments", assignmentsSummaryLabel, "Open", this::openAssignmentsPlaceholder));
        sectionsPanel.add(buildSectionRow("Settings", settingsSummaryLabel, "Open", this::openSettingsPlaceholder));
        add(sectionsPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshSectionSummaries();
    }

    private JPanel buildSectionRow(String sectionName, JLabel summaryLabel, String actionLabel, Runnable action) {
        JPanel rowPanel = new JPanel(new BorderLayout(8, 8));
        Border cardBorder = new CompoundBorder(new LineBorder(new Color(210, 210, 210)), new EmptyBorder(8, 10, 8, 10));
        rowPanel.setBorder(cardBorder);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        JLabel sectionTitleLabel = new JLabel(sectionName);
        sectionTitleLabel.setFont(sectionTitleLabel.getFont().deriveFont(Font.BOLD, 14f));
        summaryLabel.setForeground(new Color(90, 90, 90));
        summaryLabel.setFont(summaryLabel.getFont().deriveFont(Font.PLAIN, 12f));

        textPanel.add(sectionTitleLabel);
        textPanel.add(summaryLabel);
        rowPanel.add(textPanel, BorderLayout.CENTER);

        JButton actionButton = new JButton(actionLabel);
        actionButton.addActionListener(event -> action.run());
        rowPanel.add(actionButton, BorderLayout.EAST);

        return rowPanel;
    }

    private void refreshSectionSummaries() {
        studentsSummaryLabel.setText(course.getStudents().size() + " students in this course");
        assignmentsSummaryLabel.setText(course.getAssignments().size() + " assignments configured");
        settingsSummaryLabel.setText("Course settings panel (coming soon)");
    }

    private void openStudentsDialog() {
        JDialog dialog = new JDialog(this, "Students", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(520, 340);
        dialog.setLocationRelativeTo(this);

        DefaultListModel<String> studentListModel = new DefaultListModel<>();
        JList<String> studentList = new JList<>(studentListModel);
        dialog.add(new JScrollPane(studentList), BorderLayout.CENTER);

        refreshStudentList(studentListModel);

        JButton addStudentButton = new JButton("Add Student");
        addStudentButton.addActionListener(event -> addStudent(studentListModel));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(event -> dialog.dispose());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addStudentButton);
        buttonsPanel.add(closeButton);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addStudent(DefaultListModel<String> studentListModel) {
        JTextField idField = new JTextField(12);
        JTextField nameField = new JTextField(16);
        JTextField emailField = new JTextField(16);
        JTextField gradYearField = new JTextField(12);
        JComboBox<StudentStatus> statusCombo = new JComboBox<>(StudentStatus.values());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, "ID *", idField);
        addFormRow(formPanel, gbc, 1, "Name *", nameField);
        addFormRow(formPanel, gbc, 2, "Email *", emailField);
        addFormRow(formPanel, gbc, 3, "Grad Year *", gradYearField);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Status *"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(statusCombo, gbc);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Add Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int id = parseInteger(requireText(idField.getText(), "Student ID"), "Student ID");
            String name = requireText(nameField.getText(), "Student name");
            String email = requireText(emailField.getText(), "Student email");
            int gradYear = parseInteger(requireText(gradYearField.getText(), "Graduation year"), "Graduation year");
            StudentStatus status = (StudentStatus) statusCombo.getSelectedItem();

            if (status == null) {
                throw new IllegalArgumentException("Student status is required.");
            }

            if (containsStudentId(id)) {
                throw new IllegalArgumentException("A student with that ID already exists in this course.");
            }

            Student student;
            if (status == StudentStatus.UNDERGRADUATE) {
                student = new UndergradStudent(id, name, new Date(), email, gradYear, status);
            } else {
                student = new GradStudent(id, name, new Date(), email, gradYear, status);
            }

            course.addStudent(student);
            refreshStudentList(studentListModel);
            refreshSectionSummaries();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Unable to Add Student", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAssignmentsPlaceholder() {
        JOptionPane.showMessageDialog(this, "Assignments section is ready, detailed assignment management will be added next.", "Assignments", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openSettingsPlaceholder() {
        JOptionPane.showMessageDialog(this, "Settings section is ready, course settings controls will be added next.", "Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private boolean containsStudentId(int id) {
        for (Student student : course.getStudents()) {
            if (student.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void refreshStudentList(DefaultListModel<String> studentListModel) {
        studentListModel.clear();
        List<Student> sortedStudents = new ArrayList<>(course.getStudents());
        sortedStudents.sort(Comparator.comparingInt(Student::getId));

        for (Student student : sortedStudents) {
            String line = student.getId() + " - " + student.getName() + " (" + student.getStatus() + ")";
            studentListModel.addElement(line);
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private int parseInteger(String rawValue, String fieldName) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }
}
