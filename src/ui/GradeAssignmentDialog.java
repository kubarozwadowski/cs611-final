package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import models.Assignment;
import models.Course;
import models.Date;
import models.Student;
import models.Submission;

public class GradeAssignmentDialog extends JDialog {
    private final Course course;
    private final Assignment assignment;
    private final List<StudentGradeRow> gradeRows;

    public GradeAssignmentDialog(Dialog owner, Course course, Assignment assignment) {
        super(owner, "Grade: " + assignment.getName(), true);
        this.course = course;
        this.assignment = assignment;
        this.gradeRows = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setSize(600, 400);
        setLocationRelativeTo(owner);

        JLabel titleLabel = new JLabel("Assignment: " + assignment.getName() + " (" + assignment.getTotalPoints() + " points)");
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(formPanel);
        add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Add header row
        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel studentHeaderLabel = new JLabel("Student");
        studentHeaderLabel.setFont(studentHeaderLabel.getFont().deriveFont(java.awt.Font.BOLD));
        formPanel.add(studentHeaderLabel, gbc);

        gbc.gridx = 1;
        JLabel gradeHeaderLabel = new JLabel("Points Earned");
        gradeHeaderLabel.setFont(gradeHeaderLabel.getFont().deriveFont(java.awt.Font.BOLD));
        formPanel.add(gradeHeaderLabel, gbc);

        // Add student grade rows
        int rowIndex = 1;
        List<Student> sortedStudents = new ArrayList<>(course.getStudents());
        sortedStudents.sort(Comparator.comparingInt(Student::getId));

        for (Student student : sortedStudents) {
            Submission submission = getOrCreateSubmission(student);
            StudentGradeRow row = new StudentGradeRow(student, submission, assignment);
            gradeRows.add(row);

            gbc.gridy = rowIndex;
            gbc.gridx = 0;
            formPanel.add(new JLabel(student.getId() + " - " + student.getName()), gbc);

            gbc.gridx = 1;
            formPanel.add(row.pointsField, gbc);

            rowIndex++;
        }

        // Add filler to push content to top
        gbc.gridy = rowIndex;
        gbc.weighty = 1.0;
        formPanel.add(new JPanel(), gbc);

        // Add button panel
        JButton saveButton = new JButton("Save Grades");
        saveButton.addActionListener(event -> saveGrades());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private Submission getOrCreateSubmission(Student student) {
        // Check if submission already exists
        for (Submission submission : student.getSubmissions()) {
            if (submission.getAssignment().equals(assignment)) {
                return submission;
            }
        }

        // Create new submission if it doesn't exist
        Submission newSubmission = new Submission(assignment, student, new Date());
        student.getSubmissions().add(newSubmission);
        return newSubmission;
    }

    private void saveGrades() {
        try {
            for (StudentGradeRow row : gradeRows) {
                String pointsText = row.pointsField.getText().trim();

                // If empty, skip (leave as 0)
                if (pointsText.isEmpty()) {
                    row.submission.setPointsEarned(0);
                    row.submission.grade();
                    continue;
                }

                double points;
                try {
                    points = Double.parseDouble(pointsText);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "Invalid points for " + row.student.getName() + ": '" + pointsText + "' is not a valid number."
                    );
                }

                // Validate points are within bounds
                if (points < 0) {
                    throw new IllegalArgumentException(
                        row.student.getName() + ": Points cannot be negative. Got: " + points
                    );
                }

                if (points > assignment.getTotalPoints()) {
                    throw new IllegalArgumentException(
                        row.student.getName() + ": Points cannot exceed " + assignment.getTotalPoints() + ". Got: " + points
                    );
                }

                row.submission.setPointsEarned(points);
                row.submission.grade();
            }

            JOptionPane.showMessageDialog(this, "Grades saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error Saving Grades", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class StudentGradeRow {
        Student student;
        Submission submission;
        JTextField pointsField;
        Assignment assignment;

        StudentGradeRow(Student student, Submission submission, Assignment assignment) {
            this.student = student;
            this.submission = submission;
            this.assignment = assignment;
            this.pointsField = new JTextField(10);

            // Pre-fill with existing points if any
            if (submission.getPointsEarned() > 0) {
                this.pointsField.setText(String.valueOf(submission.getPointsEarned()));
            }

            // Add real-time validation listener
            pointsField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validateInput();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    validateInput();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    validateInput();
                }

                private void validateInput() {
                    String text = pointsField.getText().trim();

                    // If empty, reset to white (valid state)
                    if (text.isEmpty()) {
                        pointsField.setBackground(Color.WHITE);
                        pointsField.setForeground(Color.BLACK);
                        return;
                    }

                    try {
                        double points = Double.parseDouble(text);

                        if (points < 0 || points > assignment.getTotalPoints()) {
                            // Invalid - show red
                            pointsField.setBackground(new Color(255, 200, 200)); // Light red
                            pointsField.setForeground(Color.RED);
                        } else {
                            // Valid - show white
                            pointsField.setBackground(Color.WHITE);
                            pointsField.setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException ex) {
                        // Invalid number - show red
                        pointsField.setBackground(new Color(255, 200, 200)); // Light red
                        pointsField.setForeground(Color.RED);
                    }
                }
            });
        }
    }
}
