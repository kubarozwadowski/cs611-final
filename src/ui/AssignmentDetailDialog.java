package ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import models.Assignment;
import models.Date;

public class AssignmentDetailDialog extends JDialog {

    public AssignmentDetailDialog(Dialog owner, Assignment assignment) {
        super(owner, assignment.getName(), true);

        setSize(400, 380);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(12, 16, 12, 16));

        GridBagConstraints label = new GridBagConstraints();
        label.anchor = GridBagConstraints.WEST;
        label.insets = new Insets(4, 0, 4, 12);
        label.gridx = 0;

        GridBagConstraints value = new GridBagConstraints();
        value.anchor = GridBagConstraints.WEST;
        value.fill = GridBagConstraints.HORIZONTAL;
        value.weightx = 1.0;
        value.insets = new Insets(4, 0, 4, 0);
        value.gridx = 1;

        int row = 0;

        // Title
        JLabel titleLabel = new JLabel(assignment.getName());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0; titleGbc.gridy = row++;
        titleGbc.gridwidth = 2;
        titleGbc.anchor = GridBagConstraints.WEST;
        titleGbc.insets = new Insets(0, 0, 8, 0);
        content.add(titleLabel, titleGbc);

        addRow(content, label, value, row++, "Type",         assignment.getTypeLabel());
        addRow(content, label, value, row++, "Total Points", assignment.getTotalPoints() + " pts");
        addRow(content, label, value, row++, "Due Date",     formatDate(assignment.getDueDate()));

        if (assignment.getLateDueDate() != null) {
            addRow(content, label, value, row++, "Late Due Date", formatDate(assignment.getLateDueDate()));
            addRow(content, label, value, row++, "Late Penalty",  assignment.getLatePenalty() + " pts deducted");
        } else {
            addRow(content, label, value, row++, "Late Policy", "None");
        }

        // Separator
        GridBagConstraints sepGbc = new GridBagConstraints();
        sepGbc.gridx = 0; sepGbc.gridy = row++;
        sepGbc.gridwidth = 2;
        sepGbc.fill = GridBagConstraints.HORIZONTAL;
        sepGbc.insets = new Insets(6, 0, 6, 0);
        content.add(new JSeparator(), sepGbc);

        // Grade breakdown
        Map<String, Double> breakdown = assignment.getGradeBreakdown();
        if (!breakdown.isEmpty()) {
            JLabel breakdownTitle = new JLabel("Grade Breakdown");
            breakdownTitle.setFont(breakdownTitle.getFont().deriveFont(Font.BOLD));
            GridBagConstraints bdGbc = new GridBagConstraints();
            bdGbc.gridx = 0; bdGbc.gridy = row++;
            bdGbc.gridwidth = 2;
            bdGbc.anchor = GridBagConstraints.WEST;
            bdGbc.insets = new Insets(0, 0, 4, 0);
            content.add(breakdownTitle, bdGbc);

            for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
                addRow(content, label, value, row++, "  " + entry.getKey(), entry.getValue() + " pts");
            }
        }

        add(content, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints labelGbc, GridBagConstraints valueGbc,
                        int row, String labelText, String valueText) {
        labelGbc.gridy = row;
        valueGbc.gridy = row;
        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        panel.add(lbl, labelGbc);
        panel.add(new JLabel(valueText), valueGbc);
    }

    private String formatDate(Date date) {
        if (date == null) return "—";
        return date.toString();
    }
}
