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
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import enums.LetterGrade;
import enums.StudentStatus;
import logic.SemesterManager;
import models.Assignment;
import models.Course;
import models.Date;
import models.GradStudent;
import models.Student;
import models.UndergradStudent;
import storage.StorageManager;

public class CourseDetailFrame extends JFrame {
    private final SemesterManager semesterManager;
    private final Course course;
    private final JLabel studentsSummaryLabel;
    private final JLabel assignmentsSummaryLabel;
    private final JLabel gradeCutoffsSummaryLabel;
    private final JLabel settingsSummaryLabel;
    private final DefaultListModel<Assignment> assignmentListModel;

    public CourseDetailFrame(SemesterManager semesterManager, Course course) {
        super(course.getDisplayLabel());
        this.semesterManager = semesterManager;
        this.course = course;
        this.studentsSummaryLabel = new JLabel();
        this.assignmentsSummaryLabel = new JLabel();
        this.gradeCutoffsSummaryLabel = new JLabel();
        this.settingsSummaryLabel = new JLabel();
        this.assignmentListModel = new DefaultListModel<>();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 340);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        Border framePadding = new EmptyBorder(12, 12, 12, 12);
        ((JPanel) getContentPane()).setBorder(framePadding);

        JLabel headerLabel = new JLabel(course.getDisplayLabel(), SwingConstants.CENTER);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(headerLabel, BorderLayout.NORTH);

        JPanel sectionsPanel = new JPanel(new GridLayout(4, 1, 0, 8));
        sectionsPanel.add(buildSectionRow("Students", studentsSummaryLabel, "Open", this::openStudentsDialog));
        sectionsPanel.add(buildSectionRow("Assignments", assignmentsSummaryLabel, "Open", this::openAssignmentsPlaceholder));
        sectionsPanel.add(buildSectionRow("Grade Cutoffs", gradeCutoffsSummaryLabel, "Open", this::openGradeCutoffsDialog));
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
        gradeCutoffsSummaryLabel.setText("A starts at " + formatPercentage(course.getGradeCutoffs().get(LetterGrade.A)) + "% (customizable)");
        settingsSummaryLabel.setText("Course settings panel (coming soon)");
    }

    private String formatPercentage(Double value) {
        if (value == null) {
            return "-";
        }
        return String.format("%.1f", value);
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
        
        JButton viewGradesButton = new JButton("View Grades");
        viewGradesButton.setEnabled(false);
        viewGradesButton.addActionListener(event -> {
            int selectedIndex = studentList.getSelectedIndex();
            if (selectedIndex != -1) {
                Student selectedStudent = getStudentByIndex(selectedIndex);
                if (selectedStudent != null) {
                    openStudentGradesDialog(dialog, selectedStudent);
                }
            }
        });
        
        studentList.addListSelectionListener(event -> viewGradesButton.setEnabled(studentList.getSelectedIndex() != -1));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(event -> dialog.dispose());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addStudentButton);
        buttonsPanel.add(viewGradesButton);
        buttonsPanel.add(closeButton);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private Student getStudentByIndex(int index) {
        List<Student> sortedStudents = new ArrayList<>(course.getStudents());
        sortedStudents.sort(Comparator.comparingInt(Student::getId));
        if (index >= 0 && index < sortedStudents.size()) {
            return sortedStudents.get(index);
        }
        return null;
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
            StorageManager.getInstance().save(semesterManager);
            refreshStudentList(studentListModel);
            refreshSectionSummaries();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Unable to Add Student", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAssignmentsPlaceholder() {
        boolean hasEnumCategories = !course.getDescription().getAssignmentWeights().isEmpty();
        boolean hasCustomCategories = !course.getDescription().getCustomAssignmentWeights().isEmpty();

        if (!hasEnumCategories && !hasCustomCategories) {
            JOptionPane.showMessageDialog(
                this,
                "This course does not have any assignment categories configured yet.",
                "Assignments",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JDialog dialog = new JDialog(this, "Assignments", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(560, 360);
        dialog.setLocationRelativeTo(this);

        JList<Assignment> assignmentList = new JList<>(assignmentListModel);
        dialog.add(new JScrollPane(assignmentList), BorderLayout.CENTER);

        refreshAssignmentList();

        JButton addAssignmentButton = new JButton("Add Assignment");
        addAssignmentButton.addActionListener(event -> openAssignmentDialog(dialog));
        
        JButton gradeButton = new JButton("Grade");
        gradeButton.setEnabled(false);
        gradeButton.addActionListener(event -> {
            Assignment selectedAssignment = assignmentList.getSelectedValue();
            if (selectedAssignment != null) {
                openGradeAssignmentDialog(dialog, selectedAssignment);
            }
        });
        
        assignmentList.addListSelectionListener(event -> gradeButton.setEnabled(assignmentList.getSelectedValue() != null));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(event -> dialog.dispose());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addAssignmentButton);
        buttonsPanel.add(gradeButton);
        buttonsPanel.add(closeButton);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void openSettingsPlaceholder() {
        JOptionPane.showMessageDialog(this, "Settings section is ready, course settings controls will be added next.", "Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openGradeCutoffsDialog() {
        JDialog dialog = new JDialog(this, "Grade Cutoffs", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(520, 560);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Map<LetterGrade, Integer> distribution = calculateGradeDistribution();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel gradeHeader = new JLabel("Grade");
        gradeHeader.setFont(gradeHeader.getFont().deriveFont(Font.BOLD));
        formPanel.add(gradeHeader, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel cutoffHeader = new JLabel("Cutoff");
        cutoffHeader.setFont(cutoffHeader.getFont().deriveFont(Font.BOLD));
        formPanel.add(cutoffHeader, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        JLabel countHeader = new JLabel("Students");
        countHeader.setFont(countHeader.getFont().deriveFont(Font.BOLD));
        formPanel.add(countHeader, gbc);

        Map<LetterGrade, JTextField> cutoffFields = new EnumMap<>(LetterGrade.class);
        Map<LetterGrade, JLabel> countLabels = new EnumMap<>(LetterGrade.class);
        int row = 1;
        for (LetterGrade grade : LetterGrade.values()) {
            JTextField field = new JTextField(8);
            field.setText(formatPercentage(course.getGradeCutoffs().get(grade)));
            cutoffFields.put(grade, field);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.0;
            formPanel.add(new JLabel(formatLetterGradeLabel(grade)), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            formPanel.add(field, gbc);

            gbc.gridx = 2;
            gbc.weightx = 0.0;
            JLabel countValue = new JLabel(String.valueOf(distribution.getOrDefault(grade, 0)));
            countLabels.put(grade, countValue);
            formPanel.add(countValue, gbc);
            row++;
        }

        JLabel liveStatusLabel = new JLabel("Live preview updates as you type.");

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 4, 4, 4);
        formPanel.add(liveStatusLabel, gbc);

        Runnable refreshLiveDistribution = () -> {
            try {
                Map<LetterGrade, Double> previewCutoffs = parseCutoffFields(cutoffFields);
                Map<LetterGrade, Integer> previewDistribution = calculateGradeDistribution(previewCutoffs);
                for (LetterGrade grade : LetterGrade.values()) {
                    countLabels.get(grade).setText(String.valueOf(previewDistribution.getOrDefault(grade, 0)));
                }
                liveStatusLabel.setForeground(new Color(0, 128, 0));
                liveStatusLabel.setText("Live preview updates as you type.");
            } catch (IllegalArgumentException exception) {
                liveStatusLabel.setForeground(Color.RED);
                liveStatusLabel.setText("Fix cutoff values to preview counts.");
            }
        };

        for (JTextField field : cutoffFields.values()) {
            attachDocumentListener(field, refreshLiveDistribution);
        }

        dialog.add(formPanel, BorderLayout.CENTER);

        JButton resetButton = new JButton("Reset to Standard");
        resetButton.addActionListener(event -> {
            Map<LetterGrade, Double> defaults = Course.getDefaultGradeCutoffs();
            for (LetterGrade grade : LetterGrade.values()) {
                cutoffFields.get(grade).setText(formatPercentage(defaults.get(grade)));
            }
            refreshLiveDistribution.run();
        });

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(event -> {
            try {
                Map<LetterGrade, Double> updated = parseCutoffFields(cutoffFields);

                course.setGradeCutoffs(updated);
                StorageManager.getInstance().save(semesterManager);
                refreshSectionSummaries();
                dialog.dispose();
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(dialog, "All cutoffs must be numeric values.", "Invalid Cutoffs", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(dialog, exception.getMessage(), "Invalid Cutoffs", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        refreshLiveDistribution.run();
        dialog.setVisible(true);
    }

    private Map<LetterGrade, Double> parseCutoffFields(Map<LetterGrade, JTextField> cutoffFields) {
        Map<LetterGrade, Double> cutoffs = new EnumMap<>(LetterGrade.class);
        for (LetterGrade grade : LetterGrade.values()) {
            JTextField field = cutoffFields.get(grade);
            String value = requireText(field.getText(), formatLetterGradeLabel(grade) + " cutoff");
            try {
                cutoffs.put(grade, Double.parseDouble(value));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("All cutoffs must be numeric values.");
            }
        }
        Course.validateGradeCutoffs(cutoffs);
        return cutoffs;
    }

    private Map<LetterGrade, Integer> calculateGradeDistribution() {
        return calculateGradeDistribution(course.getGradeCutoffs());
    }

    private Map<LetterGrade, Integer> calculateGradeDistribution(Map<LetterGrade, Double> cutoffs) {
        Map<LetterGrade, Integer> distribution = new EnumMap<>(LetterGrade.class);
        for (LetterGrade grade : LetterGrade.values()) {
            distribution.put(grade, 0);
        }

        for (Student student : course.getStudents()) {
            double percentage = calculateOverallPercentage(student);
            LetterGrade grade = toLetterGrade(cutoffs, percentage);
            distribution.put(grade, distribution.get(grade) + 1);
        }

        return distribution;
    }

    private LetterGrade toLetterGrade(Map<LetterGrade, Double> cutoffs, double percentage) {
        for (LetterGrade grade : LetterGrade.values()) {
            if (percentage >= cutoffs.get(grade)) {
                return grade;
            }
        }
        return LetterGrade.F;
    }

    private double calculateOverallPercentage(Student student) {
        double totalPointsPossible = 0.0;
        double totalPointsEarned = 0.0;

        for (Assignment assignment : course.getAssignments()) {
            totalPointsPossible += assignment.getTotalPoints();
        }

        for (models.Submission submission : student.getSubmissions()) {
            if (course.getAssignments().contains(submission.getAssignment()) && submission.isGraded()) {
                totalPointsEarned += submission.getPointsEarned();
            }
        }

        if (totalPointsPossible <= 0.0) {
            return 0.0;
        }
        return (totalPointsEarned / totalPointsPossible) * 100.0;
    }

    private String formatLetterGradeLabel(LetterGrade grade) {
        switch (grade) {
            case A_MINUS:
                return "A-";
            case B_PLUS:
                return "B+";
            case B_MINUS:
                return "B-";
            case C_PLUS:
                return "C+";
            case C_MINUS:
                return "C-";
            case D_PLUS:
                return "D+";
            case D_MINUS:
                return "D-";
            default:
                return grade.name();
        }
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

    private void openAssignmentDialog(Dialog owner) {
        AssignmentFormDialog dialog = new AssignmentFormDialog(owner, course, () -> {
            StorageManager.getInstance().save(semesterManager);
            refreshAssignmentList();
            refreshSectionSummaries();
        });
        dialog.setVisible(true);
    }

    private void openGradeAssignmentDialog(Dialog owner, Assignment assignment) {
        GradeAssignmentDialog dialog = new GradeAssignmentDialog(owner, semesterManager, course, assignment);
        dialog.setVisible(true);
    }

    private void openStudentGradesDialog(Dialog owner, Student student) {
        StudentGradesDialog dialog = new StudentGradesDialog(owner, course, student);
        dialog.setVisible(true);
    }

    private void refreshAssignmentList() {
        assignmentListModel.clear();
        List<Assignment> sortedAssignments = new ArrayList<>(course.getAssignments());
        Collections.sort(sortedAssignments, Comparator.comparing(Assignment::getDueDate).thenComparing(Assignment::getName));

        for (Assignment assignment : sortedAssignments) {
            assignmentListModel.addElement(assignment);
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

    private void attachDocumentListener(JTextField field, Runnable onChange) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                onChange.run();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                onChange.run();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                onChange.run();
            }
        });
    }
}
