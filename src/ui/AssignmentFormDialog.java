package ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
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
import models.Assignment;
import models.Course;
import models.Date;
import models.Description;

public class AssignmentFormDialog extends JDialog {
    private final Course course;
    private final Runnable onAssignmentCreated;

    private final JTextField nameField;
    private final JTextField totalPointsField;
    private final JComboBox<AssignmentTypeOption> typeCombo;
    private final JTextArea descriptionArea;
    private final JTextField yearField;
    private final JTextField monthField;
    private final JTextField dayField;
    private final JTextField hourField;
    private final JTextField minuteField;
    private final JTextField lateYearField;
    private final JTextField lateMonthField;
    private final JTextField lateDayField;
    private final JTextField lateHourField;
    private final JTextField lateMinuteField;
    private final JTextField latePenaltyField;
    private final List<BreakdownRow> breakdownRows;
    private final JPanel breakdownRowsPanel;
    private final JLabel breakdownTotalLabel;

    // Initializes the dialog with form fields and layout
    public AssignmentFormDialog(Dialog owner, Course course, Runnable onAssignmentCreated) {
        super(owner, "Add Assignment", true);
        this.course = course;
        this.onAssignmentCreated = onAssignmentCreated;

        nameField = new JTextField(18);
        totalPointsField = new JTextField(10);
        typeCombo = new JComboBox<>(new DefaultComboBoxModel<>(getAvailableAssignmentTypes()));
        descriptionArea = new JTextArea(4, 18);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        yearField = new JTextField(6);
        monthField = new JTextField(4);
        dayField = new JTextField(4);
        hourField = new JTextField(4);
        minuteField = new JTextField(4);
        lateYearField = new JTextField(6);
        lateMonthField = new JTextField(4);
        lateDayField = new JTextField(4);
        lateHourField = new JTextField(4);
        lateMinuteField = new JTextField(4);
        latePenaltyField = new JTextField(6);
        breakdownRows = new ArrayList<>();
        breakdownRowsPanel = new JPanel(new GridBagLayout());
        breakdownTotalLabel = new JLabel();

        attachDocumentListener(totalPointsField, this::updateBreakdownTotalLabel);

        setLayout(new BorderLayout(12, 12));
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        addBreakdownRow();
        updateBreakdownTotalLabel();

        pack();
        setLocationRelativeTo(owner);
    }

    // Builds the main form panel with all input fields
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addField(panel, gbc, 0, "Name *", nameField);
        addField(panel, gbc, 1, "Total Points *", totalPointsField);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Type *"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Description"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Due Date *"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(buildDueDatePanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Late Due Date"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(buildLateDueDatePanel(), gbc);

        addField(panel, gbc, 6, "Late Penalty (pts)", latePenaltyField);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Breakdown *"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(buildBreakdownPanel(), gbc);

        return panel;
    }

    // Builds the panel for entering the due date
    private JPanel buildDueDatePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Year"));
        panel.add(yearField);
        panel.add(new JLabel("Month"));
        panel.add(monthField);
        panel.add(new JLabel("Day"));
        panel.add(dayField);
        panel.add(new JLabel("Hour"));
        panel.add(hourField);
        panel.add(new JLabel("Minute"));
        panel.add(minuteField);
        return panel;
    }

    // Builds the panel for entering the late due date
    private JPanel buildLateDueDatePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Year"));
        panel.add(lateYearField);
        panel.add(new JLabel("Month"));
        panel.add(lateMonthField);
        panel.add(new JLabel("Day"));
        panel.add(lateDayField);
        panel.add(new JLabel("Hour"));
        panel.add(lateHourField);
        panel.add(new JLabel("Minute"));
        panel.add(lateMinuteField);
        return panel;
    }

    // Builds the panel for defining grade breakdown items
    private JPanel buildBreakdownPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        JButton addBreakdownButton = new JButton("Add Breakdown Item");
        addBreakdownButton.addActionListener(event -> addBreakdownRow());
        panel.add(addBreakdownButton, BorderLayout.NORTH);
        panel.add(breakdownRowsPanel, BorderLayout.CENTER);
        panel.add(breakdownTotalLabel, BorderLayout.SOUTH);

        return panel;
    }

    // Adds a new breakdown row and refreshes the display
    private void addBreakdownRow() {
        BreakdownRow row = new BreakdownRow();
        breakdownRows.add(row);
        refreshBreakdownRowsPanel();
        updateBreakdownTotalLabel();
        pack();
    }

    // Refreshes the display of all breakdown rows
    private void refreshBreakdownRowsPanel() {
        breakdownRowsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < breakdownRows.size(); i++) {
            BreakdownRow row = breakdownRows.get(i);

            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 1.0;
            breakdownRowsPanel.add(row.labelField, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.0;
            breakdownRowsPanel.add(row.pointsField, gbc);

            gbc.gridx = 2;
            breakdownRowsPanel.add(new JLabel("pts"), gbc);

            gbc.gridx = 3;
            breakdownRowsPanel.add(row.removeButton, gbc);
        }

        breakdownRowsPanel.revalidate();
        breakdownRowsPanel.repaint();
    }

    // Updates the label showing the total breakdown points and validation status
    private void updateBreakdownTotalLabel() {
        double breakdownTotal = 0.0;
        boolean hasInvalidNumber = false;

        for (BreakdownRow row : breakdownRows) {
            String pointsText = optionalText(row.pointsField.getText());
            if (pointsText.isEmpty()) {
                continue;
            }

            try {
                breakdownTotal += Double.parseDouble(pointsText);
            } catch (NumberFormatException exception) {
                hasInvalidNumber = true;
                break;
            }
        }

        String totalPointsText = optionalText(totalPointsField.getText());
        if (hasInvalidNumber) {
            breakdownTotalLabel.setText("Breakdown total: invalid number entered");
            return;
        }

        if (totalPointsText.isEmpty()) {
            breakdownTotalLabel.setText("Breakdown total: " + formatPoints(breakdownTotal) + " pts");
            return;
        }

        try {
            double totalPoints = Double.parseDouble(totalPointsText);
            double difference = breakdownTotal - totalPoints;
            breakdownTotalLabel.setText(
                "Breakdown total: " + formatPoints(breakdownTotal)
                    + " / " + formatPoints(totalPoints)
                    + " pts (" + formatSignedPoints(difference) + ")"
            );
        } catch (NumberFormatException exception) {
            breakdownTotalLabel.setText("Breakdown total: " + formatPoints(breakdownTotal) + " pts");
        }
    }

    // Formats points for display, showing integers without decimals
    private String formatPoints(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001) {
            return Integer.toString((int) Math.rint(value));
        }
        return String.format("%.2f", value);
    }

    private String formatSignedPoints(double value) {
        String formatted = formatPoints(Math.abs(value));
        if (Math.abs(value) < 0.0001) {
            return "matches";
        }
        return value > 0 ? "+" + formatted : "-" + formatted;
    }

    // Builds the panel with Cancel and Save buttons
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save Assignment");

        cancelButton.addActionListener(event -> dispose());
        saveButton.addActionListener(event -> saveAssignment());

        panel.add(cancelButton);
        panel.add(saveButton);
        return panel;
    }

    // Validates form input and saves the new assignment to the course
    private void saveAssignment() {
        try {
            String name = requireText(nameField.getText(), "Assignment name");
            int totalPoints = parseInteger(requireText(totalPointsField.getText(), "Total points"), "Total points");
            if (totalPoints <= 0) {
                throw new IllegalArgumentException("Total points must be greater than 0.");
            }

            AssignmentTypeOption typeOption = (AssignmentTypeOption) typeCombo.getSelectedItem();
            if (typeOption == null) {
                throw new IllegalArgumentException("Assignment type is required.");
            }

            Map<String, Double> gradeBreakdown = parseGradeBreakdown(totalPoints);
            String descriptionText = optionalText(descriptionArea.getText());
            Date dueDate = parseDueDate();
            Date lateDueDate = parseLateDueDate();
            double latePenalty = parseLatePenalty(totalPoints);

            int assignmentId = nextAssignmentId();
            Description description = new Description(name, descriptionText);
            Assignment assignment;
            if (lateDueDate != null) {
                if (typeOption.isEnumType()) {
                    assignment = new Assignment(assignmentId, name, typeOption.getEnumType(), totalPoints, gradeBreakdown, dueDate, lateDueDate, latePenalty, description);
                } else {
                    assignment = new Assignment(assignmentId, name, typeOption.getCustomType(), totalPoints, gradeBreakdown, dueDate, lateDueDate, latePenalty, description);
                }
            } else {
                if (typeOption.isEnumType()) {
                    assignment = new Assignment(assignmentId, name, typeOption.getEnumType(), totalPoints, gradeBreakdown, dueDate, description);
                } else {
                    assignment = new Assignment(assignmentId, name, typeOption.getCustomType(), totalPoints, gradeBreakdown, dueDate, description);
                }
            }

            course.addAssignment(assignment);
            onAssignmentCreated.run();
            dispose();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Unable to Add Assignment", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Returns array of available assignment types from the course
    private AssignmentTypeOption[] getAvailableAssignmentTypes() {
        List<AssignmentTypeOption> availableTypes = new ArrayList<>();

        List<AssignmentType> enumTypes = new ArrayList<>(course.getDescription().getAssignmentWeights().keySet());
        enumTypes.sort(Comparator.comparing(Enum::name));
        for (AssignmentType assignmentType : enumTypes) {
            availableTypes.add(AssignmentTypeOption.fromEnum(assignmentType));
        }

        for (String customType : course.getDescription().getCustomAssignmentWeights().keySet()) {
            availableTypes.add(AssignmentTypeOption.fromCustom(customType));
        }

        return availableTypes.toArray(new AssignmentTypeOption[0]);
    }

    // Parses and validates the grade breakdown items
    private Map<String, Double> parseGradeBreakdown(int totalPoints) {
        if (breakdownRows.isEmpty()) {
            throw new IllegalArgumentException("Add at least one breakdown item.");
        }

        Map<String, Double> breakdown = new LinkedHashMap<>();
        double breakdownTotal = 0.0;

        for (BreakdownRow row : breakdownRows) {
            String label = optionalText(row.labelField.getText());
            String pointsText = optionalText(row.pointsField.getText());

            if (label.isEmpty() && pointsText.isEmpty()) {
                continue;
            }

            if (label.isEmpty()) {
                throw new IllegalArgumentException("Each breakdown item needs a label.");
            }

            if (breakdown.containsKey(label)) {
                throw new IllegalArgumentException("Duplicate breakdown label: " + label);
            }

            double points = parseDouble(requireText(pointsText, label + " points"), label + " points");
            if (points <= 0.0) {
                throw new IllegalArgumentException(label + " points must be greater than 0.");
            }

            breakdown.put(label, points);
            breakdownTotal += points;
        }

        if (breakdown.isEmpty()) {
            throw new IllegalArgumentException("Add at least one breakdown item.");
        }

        if (Math.abs(breakdownTotal - totalPoints) > 0.0001) {
            throw new IllegalArgumentException("Breakdown points must add up to the total points. Current total: " + breakdownTotal);
        }

        return breakdown;
    }

    // Parses the due date from input fields
    private Date parseDueDate() {
        int year = parseInteger(requireText(yearField.getText(), "Due year"), "Due year");
        int month = parseInteger(requireText(monthField.getText(), "Due month"), "Due month");
        int day = parseInteger(requireText(dayField.getText(), "Due day"), "Due day");

        String hourText = optionalText(hourField.getText());
        String minuteText = optionalText(minuteField.getText());

        Integer hour = null;
        Integer minute = null;

        if (!hourText.isEmpty() || !minuteText.isEmpty()) {
            hour = parseInteger(requireText(hourText, "Due hour"), "Due hour");
            minute = parseInteger(requireText(minuteText, "Due minute"), "Due minute");
        }

        return new Date(year, month, day, hour, minute);
    }

    // Parses the late due date from input fields, returning null if not provided
    private Date parseLateDueDate() {
        String year  = optionalText(lateYearField.getText());
        String month = optionalText(lateMonthField.getText());
        String day   = optionalText(lateDayField.getText());

        // All blank — no late due date
        if (year.isEmpty() && month.isEmpty() && day.isEmpty()) return null;

        int y = parseInteger(requireText(year,  "Late due year"),  "Late due year");
        int m = parseInteger(requireText(month, "Late due month"), "Late due month");
        int d = parseInteger(requireText(day,   "Late due day"),   "Late due day");

        String hourText   = optionalText(lateHourField.getText());
        String minuteText = optionalText(lateMinuteField.getText());

        Integer hour = null;
        Integer minute = null;
        if (!hourText.isEmpty() || !minuteText.isEmpty()) {
            hour   = parseInteger(requireText(hourText,   "Late due hour"),   "Late due hour");
            minute = parseInteger(requireText(minuteText, "Late due minute"), "Late due minute");
        }

        return new Date(y, m, d, hour, minute);
    }

    // Parses and validates the late penalty value
    private double parseLatePenalty(int totalPoints) {
        String text = optionalText(latePenaltyField.getText());
        if (text.isEmpty()) return 0;

        double penalty = parseDouble(text, "Late penalty");
        if (penalty < 0 || penalty >= totalPoints) {
            throw new IllegalArgumentException("Late penalty must be >= 0 and less than total points (" + totalPoints + ").");
        }
        return penalty;
    }

    // Generates the next available assignment ID for the course
    private int nextAssignmentId() {
        int nextId = 1;
        for (Assignment assignment : course.getAssignments()) {
            nextId = Math.max(nextId, assignment.getId() + 1);
        }
        return nextId;
    }

    // Helper method to add a labeled text field to the form panel
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

    // Validates that a field contains non-empty text
    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    // Returns trimmed text, treating null or empty strings as empty
    private String optionalText(String value) {
        return value == null ? "" : value.trim();
    }

    // Parses a string to an integer, throwing an exception for invalid input
    private int parseInteger(String rawValue, String fieldName) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }

    // Parses a string to a double, throwing an exception for invalid input
    private double parseDouble(String rawValue, String fieldName) {
        try {
            return Double.parseDouble(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }

    // Attaches a listener to track changes in a text field
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

    private static class AssignmentTypeOption {
        private final AssignmentType enumType;
        private final String customType;

        private AssignmentTypeOption(AssignmentType enumType, String customType) {
            this.enumType = enumType;
            this.customType = customType;
        }

        private static AssignmentTypeOption fromEnum(AssignmentType enumType) {
            return new AssignmentTypeOption(enumType, null);
        }

        private static AssignmentTypeOption fromCustom(String customType) {
            return new AssignmentTypeOption(null, customType);
        }

        private boolean isEnumType() {
            return enumType != null;
        }

        private AssignmentType getEnumType() {
            return enumType;
        }

        private String getCustomType() {
            return customType;
        }

        @Override
        public String toString() {
            if (enumType != null) {
                return enumType.name();
            }
            return customType;
        }
    }

    private class BreakdownRow {
        private final JTextField labelField;
        private final JTextField pointsField;
        private final JButton removeButton;

        private BreakdownRow() {
            this.labelField = new JTextField(16);
            this.pointsField = new JTextField(6);
            this.removeButton = new JButton("Remove");
            attachDocumentListener(pointsField, AssignmentFormDialog.this::updateBreakdownTotalLabel);
            this.removeButton.addActionListener(event -> {
                breakdownRows.remove(this);
                refreshBreakdownRowsPanel();
                updateBreakdownTotalLabel();
                pack();
            });
        }
    }
}
