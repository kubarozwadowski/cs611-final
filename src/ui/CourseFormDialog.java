package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import enums.AssignmentType;
import enums.LetterGrade;
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
    private final Map<String, Double> customAssignmentWeights;
    private final List<CustomCategoryRow> customCategoryRows;
    private final JPanel customCategoryRowsPanel;
    private final JLabel weightsWarningLabel;
    private final JCheckBox useCustomCutoffsCheckbox;
    private final Map<LetterGrade, JTextField> gradeCutoffFields;

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
        customAssignmentWeights = new java.util.LinkedHashMap<>();
        customCategoryRows = new ArrayList<>();
        customCategoryRowsPanel = new JPanel(new GridBagLayout());
        weightsWarningLabel = new JLabel(formatWarningText("Selected category weights must add up to 100%. Current total: 0%"));
        useCustomCutoffsCheckbox = new JCheckBox("Use custom letter grade cutoffs");
        gradeCutoffFields = new EnumMap<>(LetterGrade.class);

        for (LetterGrade letterGrade : LetterGrade.values()) {
            JTextField field = new JTextField(6);
            Double defaultCutoff = Course.getDefaultGradeCutoffs().get(letterGrade);
            field.setText(defaultCutoff == null ? "" : String.valueOf(defaultCutoff));
            field.setEnabled(false);
            gradeCutoffFields.put(letterGrade, field);
        }

        useCustomCutoffsCheckbox.addActionListener(event -> {
            boolean enabled = useCustomCutoffsCheckbox.isSelected();
            for (JTextField field : gradeCutoffFields.values()) {
                field.setEnabled(enabled);
            }
        });

        setLayout(new BorderLayout(12, 12));
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        pack();
        ensureMinimumDialogWidth();
        setLocationRelativeTo(owner);
    }

    private void ensureMinimumDialogWidth() {
        final int minWidth = 760;
        Dimension currentSize = getSize();
        if (currentSize.width < minWidth) {
            setSize(minWidth, currentSize.height);
        }
    }

    private String formatWarningText(String message) {
        return "<html><div style='width: 430px;'>" + message + "</div></html>";
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

        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(weightsWarningLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Grade Cutoffs"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buildGradeCutoffPanel(), gbc);

        updateWeightWarning();

        return panel;
    }

    private JPanel buildGradeCutoffPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(useCustomCutoffsCheckbox, gbc);

        int row = 1;
        for (LetterGrade grade : LetterGrade.values()) {
            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.0;
            panel.add(new JLabel(formatLetterGradeLabel(grade) + " min"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.0;
            panel.add(gradeCutoffFields.get(grade), gbc);

            gbc.gridx = 2;
            panel.add(new JLabel("%"), gbc);

            row++;
        }

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
            JTextField weightField = new JTextField(6);
            weightField.setEnabled(false);

            checkBox.addActionListener(event -> {
                weightField.setEnabled(checkBox.isSelected());
                if (!checkBox.isSelected()) {
                    weightField.setText("");
                }
                updateWeightWarning();
            });

            attachDocumentListener(weightField, this::updateWeightWarning);

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
            gbc.weightx = 0.0;
            panel.add(new JLabel("%"), gbc);

            row++;
        }

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        panel.add(buildCustomCategoryPanel(), gbc);

        return panel;
    }

    private JPanel buildCustomCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        JButton addCustomTypeButton = new JButton("Add Custom Type");
        addCustomTypeButton.addActionListener(event -> {
            addCustomCategoryRow();
            updateWeightWarning();
        });
        panel.add(addCustomTypeButton, BorderLayout.NORTH);

        panel.add(customCategoryRowsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void addCustomCategoryRow() {
        CustomCategoryRow row = new CustomCategoryRow();
        customCategoryRows.add(row);
        refreshCustomCategoryRowsPanel();
    }

    private void refreshCustomCategoryRowsPanel() {
        customCategoryRowsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < customCategoryRows.size(); i++) {
            CustomCategoryRow row = customCategoryRows.get(i);

            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 1.0;
            customCategoryRowsPanel.add(row.nameField, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.0;
            customCategoryRowsPanel.add(row.weightField, gbc);

            gbc.gridx = 2;
            customCategoryRowsPanel.add(new JLabel("%"), gbc);

            gbc.gridx = 3;
            customCategoryRowsPanel.add(row.removeButton, gbc);
        }

        customCategoryRowsPanel.revalidate();
        customCategoryRowsPanel.repaint();
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
            Map<String, Double> customWeights = parseCustomAssignmentWeights();
            Map<LetterGrade, Double> gradeCutoffs = parseGradeCutoffs();
            validateTotalWeight(assignmentWeights, customWeights);

            Description description = new Description(name, descriptionText, syllabusText, assignmentWeights, customWeights);
            Course course = new Course(dept, code, name, description, meetingTimes, building, prereqs, gradeCutoffs);

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

        for (AssignmentType assignmentType : AssignmentType.values()) {
            JCheckBox checkBox = assignmentTypeCheckboxes.get(assignmentType);
            JTextField weightField = assignmentWeightFields.get(assignmentType);

            if (!checkBox.isSelected()) {
                continue;
            }

            double weight = parseWeight(weightField.getText(), formatAssignmentTypeLabel(assignmentType) + " weight");
            if (weight <= 0.0) {
                throw new IllegalArgumentException(formatAssignmentTypeLabel(assignmentType) + " weight must be greater than 0.");
            }

            weights.put(assignmentType, weight);
        }

        return weights;
    }

    private Map<String, Double> parseCustomAssignmentWeights() {
        customAssignmentWeights.clear();

        for (CustomCategoryRow row : customCategoryRows) {
            String categoryName = optionalText(row.nameField.getText());
            String weightText = optionalText(row.weightField.getText());

            if (categoryName.isEmpty() && weightText.isEmpty()) {
                continue;
            }

            if (categoryName.isEmpty()) {
                throw new IllegalArgumentException("Custom assignment type name is required.");
            }

            if (customAssignmentWeights.containsKey(categoryName)) {
                throw new IllegalArgumentException("Duplicate custom assignment type: " + categoryName);
            }

            double weight = parseWeight(weightText, categoryName + " weight");
            if (weight <= 0.0) {
                throw new IllegalArgumentException(categoryName + " weight must be greater than 0.");
            }

            customAssignmentWeights.put(categoryName, weight);
        }

        return new java.util.LinkedHashMap<>(customAssignmentWeights);
    }

    private Map<LetterGrade, Double> parseGradeCutoffs() {
        if (!useCustomCutoffsCheckbox.isSelected()) {
            return Course.getDefaultGradeCutoffs();
        }

        Map<LetterGrade, Double> cutoffs = new EnumMap<>(LetterGrade.class);
        for (LetterGrade grade : LetterGrade.values()) {
            JTextField field = gradeCutoffFields.get(grade);
            double cutoff = parseWeight(field.getText(), formatLetterGradeLabel(grade) + " cutoff");
            cutoffs.put(grade, cutoff);
        }

        Course.validateGradeCutoffs(cutoffs);
        return cutoffs;
    }

    private double parseWeight(String rawWeight, String fieldLabel) {
        String weightText = requireText(rawWeight, fieldLabel);
        try {
            return Double.parseDouble(weightText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldLabel + " must be a number.");
        }
    }

    private void validateTotalWeight(Map<AssignmentType, Double> standardWeights, Map<String, Double> customWeights) {
        int selectedCategoryCount = standardWeights.size() + customWeights.size();
        if (selectedCategoryCount == 0) {
            throw new IllegalArgumentException("Select at least one assignment category.");
        }

        double total = 0.0;
        for (double weight : standardWeights.values()) {
            total += weight;
        }
        for (double weight : customWeights.values()) {
            total += weight;
        }

        if (Math.abs(total - 100.0) > 0.0001) {
            throw new IllegalArgumentException("Selected category weights must add up to 100%. Current total: " + total + "%");
        }
    }

    private void updateWeightWarning() {
        double total = 0.0;
        int selectedCount = 0;

        for (AssignmentType assignmentType : AssignmentType.values()) {
            JCheckBox checkBox = assignmentTypeCheckboxes.get(assignmentType);
            JTextField weightField = assignmentWeightFields.get(assignmentType);

            if (checkBox != null && checkBox.isSelected()) {
                selectedCount++;
                String weightText = optionalText(weightField.getText());
                if (!weightText.isEmpty()) {
                    try {
                        total += Double.parseDouble(weightText);
                    } catch (NumberFormatException exception) {
                        weightsWarningLabel.setForeground(Color.RED);
                        weightsWarningLabel.setText(formatWarningText("One or more weights is not a valid number."));
                        return;
                    }
                }
            }
        }

        for (CustomCategoryRow row : customCategoryRows) {
            String name = optionalText(row.nameField.getText());
            String weightText = optionalText(row.weightField.getText());

            if (name.isEmpty() && weightText.isEmpty()) {
                continue;
            }

            selectedCount++;
            if (weightText.isEmpty()) {
                continue;
            }

            try {
                total += Double.parseDouble(weightText);
            } catch (NumberFormatException exception) {
                weightsWarningLabel.setForeground(Color.RED);
                weightsWarningLabel.setText(formatWarningText("One or more custom weights is not a valid number."));
                return;
            }
        }

        if (selectedCount == 0) {
            weightsWarningLabel.setForeground(Color.RED);
            weightsWarningLabel.setText(formatWarningText("Select at least one assignment category."));
            return;
        }

        if (Math.abs(total - 100.0) <= 0.0001) {
            weightsWarningLabel.setForeground(new Color(0, 128, 0));
            weightsWarningLabel.setText(formatWarningText("Weights total 100%."));
            return;
        }

        weightsWarningLabel.setForeground(Color.RED);
        weightsWarningLabel.setText(formatWarningText("Selected category weights must add up to 100%. Current total: " + total + "%"));
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

    private class CustomCategoryRow {
        private final JTextField nameField;
        private final JTextField weightField;
        private final JButton removeButton;

        private CustomCategoryRow() {
            this.nameField = new JTextField(16);
            this.weightField = new JTextField(6);
            this.removeButton = new JButton("Remove");

            attachDocumentListener(nameField, CourseFormDialog.this::updateWeightWarning);
            attachDocumentListener(weightField, CourseFormDialog.this::updateWeightWarning);

            removeButton.addActionListener(event -> {
                customCategoryRows.remove(this);
                refreshCustomCategoryRowsPanel();
                updateWeightWarning();
            });
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
