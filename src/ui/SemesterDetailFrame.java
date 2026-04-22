package ui;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import logic.SemesterManager;
import models.Course;
import models.Semester;

public class SemesterDetailFrame extends JFrame {
    private final SemesterManager semesterManager;
    private final Semester semester;
    private final DefaultListModel<String> courseListModel;

    public SemesterDetailFrame(SemesterManager semesterManager, Semester semester) {
        super(semester.getLabel());
        this.semesterManager = semesterManager;
        this.semester = semester;
        this.courseListModel = new DefaultListModel<>();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        JLabel headerLabel = new JLabel(semester.getLabel(), SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        JList<String> courseList = new JList<>(courseListModel);
        add(new JScrollPane(courseList), BorderLayout.CENTER);

        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(event -> openAddCourseDialog());
        JButton backButton = new JButton("Back to Semesters");
        backButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(addCourseButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshCourseList();
    }

    private void openAddCourseDialog() {
        CourseFormDialog dialog = new CourseFormDialog(this, semesterManager, semester, this::refreshCourseList);
        dialog.setVisible(true);
    }

    private void refreshCourseList() {
        courseListModel.clear();
        for (Course course : semesterManager.getCoursesForSemester(semester)) {
            courseListModel.addElement(course.getDisplayLabel());
        }
    }
}
