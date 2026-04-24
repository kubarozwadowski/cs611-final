package ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import logic.SemesterManager;
import models.Course;
import models.Semester;

public class SemesterDetailFrame extends JFrame {
    private final SemesterManager semesterManager;
    private final Semester semester;
    private final DefaultListModel<Course> courseListModel;
    private final JList<Course> courseList;

    public SemesterDetailFrame(SemesterManager semesterManager, Semester semester) {
        super(semester.getLabel());
        this.semesterManager = semesterManager;
        this.semester = semester;
        this.courseListModel = new DefaultListModel<>();
        this.courseList = new JList<>(courseListModel);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        JLabel headerLabel = new JLabel(semester.getLabel(), SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    openSelectedCourse();
                }
            }
        });
        add(new JScrollPane(courseList), BorderLayout.CENTER);

        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(event -> openAddCourseDialog());
        JButton openCourseButton = new JButton("Open Course");
        openCourseButton.addActionListener(event -> openSelectedCourse());
        JButton backButton = new JButton("Back to Semesters");
        backButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(openCourseButton);
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
            courseListModel.addElement(course);
        }
    }

    private void openSelectedCourse() {
        Course selectedCourse = courseList.getSelectedValue();
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "No Course Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CourseDetailFrame courseDetailFrame = new CourseDetailFrame(selectedCourse);
        courseDetailFrame.setVisible(true);
    }
}
