package ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import enums.AssignmentType;
import logic.SemesterManager;
import models.Course;
import models.Description;
import models.Semester;

public class CourseFormDialog extends JDialog {
    private final SemesterManager semesterManager;
    private final Semester semester;
    private final Runnable onCourseCreated;

    private final JTextField deptField;
    private final JTextField codeField;
    private final JTextField nameField;
    private final JTextField meetingTimesField;
    private final JTextField buildingField;
    private final JTextField prereqsField;
    private final JTextArea descriptionArea;
    private final JTextArea syllabusArea;
    private final Map<AssignmentType, JCheckBox> assignmentTypeCheckboxes;
    private final Map<AssignmentType, JTextField> assignmentWeightFields;

    public CourseFormDialog(Frame owner, SemesterManager semesterManager, Semester semester, Runnable onCourseCreated) {
        super(owner, "Add Course", true);
        this.semesterManager = semesterManager;
        this.semester = semester;
        this.onCourseCreated = onCourseCreated;

        deptField = new JTextField(18);
        codeField = new JTextField(18);
        nameField = new JTextField(18);
        meetingTimesField = new JTextField(18);
        buildingField = new JTextField(18);
        prereqsField = new JTextField(18);
        descriptionArea = new JTextArea(5, 18);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        syllabusArea = new JTextArea(5, 18);
        syllabusArea.setLineWrap(true);
        syllabusArea.setWrapStyleWord(true);
        assignmentTypeCheckboxes = new EnumMap<>(AssignmentType.class);
        assignmentWeightFields = new EnumMap<>(AssignmentType.class);

        setLayout(new BorderLayout(12, 12));
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addField(panel, gbc, 0, "Dept *", deptField);
        addField(panel, gbc, 1, "Code *", codeField);
        addField(panel, gbc, 2, "Name *", nameField);
        addField(panel, gbc, 3, "Meeting Times", meetingTimesField);
        addField(panel, gbc, 4, "Building", buildingField);
        addField(panel, gbc, 5, "Prereqs", prereqsField);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Description"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Syllabus"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(syllabusArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Categories *"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(buildAssignmentTypePanel(), gbc);

        return panel;
    }

    private JPanel buildAssignmentTypePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        for (AssignmentType assignmentType : AssignmentType.values()) {
            JCheckBox checkBox = new JCheckBox(formatAssignmentTypeLabel(assignmentType));
            JTextField weightField = new JTextField(8);

            assignmentTypeCheckboxes.put(assignmentType, checkBox);
            assignmentWeightFields.put(assignmentType, weightField);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 1.0;
            panel.add(checkBox, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.0;
            panel.add(weightField, gbc);

            gbc.gridx = 2;
            panel.add(new JLabel("%"), gbc);

            row++;
        }

        return panel;
    }

    private String formatAssignmentTypeLabel(AssignmentType assignmentType) {
        String raw = assignmentType.name().toLowerCase().replace('_', ' ');
        String[] words = raw.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            String word = words[i];
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save Course");

        cancelButton.addActionListener(event -> dispose());
        saveButton.addActionListener(event -> saveCourse());

        panel.add(cancelButton);
        panel.add(saveButton);
        return panel;
    }

    private void saveCourse() {
        try {
            String dept = requireText(deptField.getText(), "Department");
            int code = parseCode(codeField.getText());
            String name = requireText(nameField.getText(), "Course name");
            String meetingTimes = optionalText(meetingTimesField.getText());
            String building = optionalText(buildingField.getText());
            String prereqs = optionalText(prereqsField.getText());
            String descriptionText = optionalText(descriptionArea.getText());
            String syllabusText = optionalText(syllabusArea.getText());
            Map<AssignmentType, Double> assignmentWeights = parseAssignmentWeights();

            Description description = new Description(name, descriptionText, syllabusText, assignmentWeights);
            Course course = new Course(dept, code, name, description, meetingTimes, building, prereqs);

            semesterManager.addCourseToSemester(semester, course);
            onCourseCreated.run();
            dispose();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Unable to Add Course", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String optionalText(String value) {
        return value == null ? "" : value.trim();
    }

    private Map<AssignmentType, Double> parseAssignmentWeights() {
        Map<AssignmentType, Double> weights = new EnumMap<>(AssignmentType.class);
        double total = 0.0;

        for (AssignmentType assignmentType : AssignmentType.values()) {
            JCheckBox checkBox = assignmentTypeCheckboxes.get(assignmentType);
            JTextField weightField = assignmentWeightFields.get(assignmentType);

            if (!checkBox.isSelected()) {
                continue;
            }

            double weight = parseWeight(weightField.getText(), assignmentType);
            if (weight <= 0.0) {
                throw new IllegalArgumentException(formatAssignmentTypeLabel(assignmentType) + " weight must be greater than 0.");
            }

            weights.put(assignmentType, weight);
            total += weight;
        }

        if (weights.isEmpty()) {
            throw new IllegalArgumentException("Select at least one assignment category.");
        }

        if (Math.abs(total - 100.0) > 0.0001) {
            throw new IllegalArgumentException("Selected category weights must add up to 100%. Current total: " + total + "%");
        }

        return weights;
    }

    private double parseWeight(String rawWeight, AssignmentType assignmentType) {
        String weightText = requireText(rawWeight, formatAssignmentTypeLabel(assignmentType) + " weight");
        try {
            return Double.parseDouble(weightText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(formatAssignmentTypeLabel(assignmentType) + " weight must be a number.");
        }
    }

    private int parseCode(String rawCode) {
        String codeText = requireText(rawCode, "Course code");
        try {
            return Integer.parseInt(codeText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Course code must be a number.");
        }
    }
}
