package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import enums.LetterGrade;
import logic.GradeCalculator;
import models.Course;
import models.Student;

public class CourseSummaryDialog extends JDialog {

    public CourseSummaryDialog(Dialog owner, Course course) {
        super(owner, "Course Summary — " + course.getDisplayLabel(), true);

        setSize(460, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- Compute stats ---
        GradeCalculator calculator = new GradeCalculator(course);
        List<Student> students = new ArrayList<>(course.getStudents());

        double totalPercentage = 0;
        int gradedCount = 0;
        Student bestStudent = null;
        Student worstStudent = null;
        double bestPct = -1;
        double worstPct = 101;

        Map<LetterGrade, Integer> distribution = new EnumMap<>(LetterGrade.class);
        for (LetterGrade g : LetterGrade.values()) distribution.put(g, 0);

        for (Student student : students) {
            double pct = calculator.calculateStudentGrade(student);

            // Only count students with at least one graded submission
            boolean hasGraded = student.getSubmissions().stream()
                    .anyMatch(s -> course.getAssignments().contains(s.getAssignment()) && s.isGraded());
            if (!hasGraded) continue;

            gradedCount++;
            totalPercentage += pct;

            if (pct > bestPct) { bestPct = pct; bestStudent = student; }
            if (pct < worstPct) { worstPct = pct; worstStudent = student; }

            LetterGrade letter = course.toLetterGrade(pct);
            distribution.put(letter, distribution.get(letter) + 1);
        }

        double averagePct = gradedCount > 0 ? totalPercentage / gradedCount : 0;

        // --- Build UI ---
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.anchor = GridBagConstraints.WEST;
        labelGbc.insets = new Insets(4, 0, 4, 12);
        labelGbc.gridx = 0;

        GridBagConstraints valueGbc = new GridBagConstraints();
        valueGbc.anchor = GridBagConstraints.WEST;
        valueGbc.fill = GridBagConstraints.HORIZONTAL;
        valueGbc.weightx = 1.0;
        valueGbc.insets = new Insets(4, 0, 4, 0);
        valueGbc.gridx = 1;

        int row = 0;

        // Title
        JLabel title = new JLabel(course.getDisplayLabel());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 15f));
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0; titleGbc.gridy = row++;
        titleGbc.gridwidth = 2;
        titleGbc.anchor = GridBagConstraints.WEST;
        titleGbc.insets = new Insets(0, 0, 10, 0);
        content.add(title, titleGbc);

        // Overview
        addSectionHeader(content, row++, "Overview");
        addRow(content, labelGbc, valueGbc, row++, "Total Students", String.valueOf(students.size()));
        addRow(content, labelGbc, valueGbc, row++, "Graded Students", gradedCount + " / " + students.size());
        addRow(content, labelGbc, valueGbc, row++, "Total Assignments", String.valueOf(course.getAssignments().size()));

        // Separator
        row = addSeparator(content, row);

        // Grade stats
        addSectionHeader(content, row++, "Grade Statistics (Weighted)");

        if (gradedCount == 0) {
            GridBagConstraints noDataGbc = new GridBagConstraints();
            noDataGbc.gridx = 0; noDataGbc.gridy = row++;
            noDataGbc.gridwidth = 2;
            noDataGbc.anchor = GridBagConstraints.WEST;
            noDataGbc.insets = new Insets(4, 0, 4, 0);
            JLabel noData = new JLabel("No graded submissions yet.");
            noData.setForeground(new Color(120, 120, 120));
            content.add(noData, noDataGbc);
        } else {
            addRow(content, labelGbc, valueGbc, row++, "Class Average",
                    String.format("%.1f%%  (%s)", averagePct, formatGrade(course.toLetterGrade(averagePct))));
            addRow(content, labelGbc, valueGbc, row++, "Best Student",
                    bestStudent == null ? "—" : bestStudent.getName() + String.format("  (%.1f%%)", bestPct));
            addRow(content, labelGbc, valueGbc, row++, "Lowest Student",
                    worstStudent == null ? "—" : worstStudent.getName() + String.format("  (%.1f%%)", worstPct));

            // Separator
            row = addSeparator(content, row);

            // Grade distribution
            addSectionHeader(content, row++, "Grade Distribution");
            for (LetterGrade g : LetterGrade.values()) {
                int count = distribution.get(g);
                if (count > 0) {
                    addRow(content, labelGbc, valueGbc, row++, formatGrade(g),
                            count + " student" + (count == 1 ? "" : "s"));
                }
            }
        }

        // Filler
        GridBagConstraints fillerGbc = new GridBagConstraints();
        fillerGbc.gridx = 0; fillerGbc.gridy = row;
        fillerGbc.gridwidth = 2;
        fillerGbc.weighty = 1.0;
        fillerGbc.fill = GridBagConstraints.BOTH;
        content.add(new JPanel(), fillerGbc);

        add(new JScrollPane(content), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // --- Helpers ---

    private void addRow(JPanel panel, GridBagConstraints labelGbc, GridBagConstraints valueGbc,
                        int row, String labelText, String valueText) {
        labelGbc.gridy = row;
        valueGbc.gridy = row;
        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        panel.add(lbl, labelGbc);
        panel.add(new JLabel(valueText), valueGbc);
    }

    private void addSectionHeader(JPanel panel, int row, String text) {
        JLabel header = new JLabel(text);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));
        header.setForeground(new Color(60, 100, 180));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 2, 0);
        panel.add(header, gbc);
    }

    private int addSeparator(JPanel panel, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        panel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        return row + 1;
    }

    private String formatGrade(LetterGrade grade) {
        switch (grade) {
            case A_MINUS: return "A-";
            case B_PLUS:  return "B+";
            case B_MINUS: return "B-";
            case C_PLUS:  return "C+";
            case C_MINUS: return "C-";
            case D_PLUS:  return "D+";
            case D_MINUS: return "D-";
            default:      return grade.name();
        }
    }
}
