package ui;

import java.awt.BorderLayout;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import logic.GradeCalculator;
import models.Assignment;
import models.Course;
import models.Student;
import models.Submission;
import enums.LetterGrade;

public class StudentGradesDialog extends JDialog {
    private final Course course;
    private final Student student;

    public StudentGradesDialog(Dialog owner, Course course, Student student) {
        super(owner, "Grades for: " + student.getName(), true);
        this.course = course;
        this.student = student;

        setLayout(new BorderLayout(10, 10));
        setSize(600, 400);
        setLocationRelativeTo(owner);

        JLabel titleLabel = new JLabel(student.getId() + " - " + student.getName(), SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel gradePanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(gradePanel);
        add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Add header row
        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel assignmentHeaderLabel = new JLabel("Assignment");
        assignmentHeaderLabel.setFont(assignmentHeaderLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(assignmentHeaderLabel, gbc);

        gbc.gridx = 1;
        JLabel totalHeaderLabel = new JLabel("Total Points");
        totalHeaderLabel.setFont(totalHeaderLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(totalHeaderLabel, gbc);

        gbc.gridx = 2;
        JLabel earnedHeaderLabel = new JLabel("Points Earned");
        earnedHeaderLabel.setFont(earnedHeaderLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(earnedHeaderLabel, gbc);

        gbc.gridx = 3;
        JLabel percentHeaderLabel = new JLabel("Percentage");
        percentHeaderLabel.setFont(percentHeaderLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(percentHeaderLabel, gbc);

        // Add grade rows for each assignment
        int rowIndex = 1;
        List<Assignment> sortedAssignments = new ArrayList<>(course.getAssignments());
        sortedAssignments.sort(Comparator.comparing(Assignment::getDueDate).thenComparing(Assignment::getName));

        double totalPointsPossible = 0;
        double totalPointsEarned = 0;

        for (Assignment assignment : sortedAssignments) {
            Submission submission = findSubmission(assignment);
            totalPointsPossible += assignment.getTotalPoints();

            gbc.gridy = rowIndex;
            gbc.gridx = 0;
            gradePanel.add(new JLabel(assignment.getName()), gbc);

            gbc.gridx = 1;
            gradePanel.add(new JLabel(String.valueOf(assignment.getTotalPoints())), gbc);

            if (submission != null && submission.isGraded()) {
                double earned = submission.getPointsEarned();
                totalPointsEarned += earned;

                gbc.gridx = 2;
                gradePanel.add(new JLabel(String.valueOf(earned)), gbc);

                gbc.gridx = 3;
                double percentage = (earned / assignment.getTotalPoints()) * 100;
                gradePanel.add(new JLabel(String.format("%.1f%%", percentage)), gbc);
            } else {
                gbc.gridx = 2;
                gradePanel.add(new JLabel("Not Graded"), gbc);

                gbc.gridx = 3;
                gradePanel.add(new JLabel("-"), gbc);
            }

            rowIndex++;
        }

        // Add total row
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        JLabel totalLabel = new JLabel("TOTAL");
        totalLabel.setFont(totalLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(totalLabel, gbc);

        gbc.gridx = 1;
        JLabel totalPossibleLabel = new JLabel(String.valueOf((int) totalPointsPossible));
        totalPossibleLabel.setFont(totalPossibleLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(totalPossibleLabel, gbc);

        gbc.gridx = 2;
        JLabel totalEarnedLabel = new JLabel(String.valueOf((int) totalPointsEarned));
        totalEarnedLabel.setFont(totalEarnedLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(totalEarnedLabel, gbc);

        gbc.gridx = 3;
        double weightedPercentage = new GradeCalculator(course).calculateStudentGrade(student);
        JLabel totalPercentageLabel = new JLabel(String.format("%.1f%% (weighted)", weightedPercentage));
        totalPercentageLabel.setFont(totalPercentageLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(totalPercentageLabel, gbc);

        gbc.gridy = rowIndex + 1;
        gbc.gridx = 0;
        JLabel letterLabel = new JLabel("Course Letter Grade");
        letterLabel.setFont(letterLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(letterLabel, gbc);

        gbc.gridx = 3;
        LetterGrade letterGrade = student.getCurrentGrade() != null ? student.getCurrentGrade() : course.toLetterGrade(weightedPercentage);
        JLabel letterValueLabel = new JLabel(formatLetterGradeLabel(letterGrade));
        letterValueLabel.setFont(letterValueLabel.getFont().deriveFont(java.awt.Font.BOLD));
        gradePanel.add(letterValueLabel, gbc);

        // Add filler to push content to top
        gbc.gridy = rowIndex + 2;
        gbc.weighty = 1.0;
        gradePanel.add(new JPanel(), gbc);

        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private Submission findSubmission(Assignment assignment) {
        for (Submission submission : student.getSubmissions()) {
            if (submission.getAssignment().equals(assignment)) {
                return submission;
            }
        }
        return null;
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
}
