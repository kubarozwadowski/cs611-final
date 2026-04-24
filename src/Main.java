import java.util.EnumMap;
import java.util.Map;

import enums.AssignmentType;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import logic.SemesterManager;
import models.Course;
import models.Description;
import models.Semester;
import ui.SemesterListFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();

            SemesterManager semesterManager = new SemesterManager();
            seedSampleData(semesterManager);
            SemesterListFrame frame = new SemesterListFrame(semesterManager);
            frame.setVisible(true);
        });
    }

    private static void seedSampleData(SemesterManager semesterManager) {
        Semester sampleSemester = semesterManager.addSemester("Spring 2026");

        Map<AssignmentType, Double> weights = new EnumMap<>(AssignmentType.class);
        weights.put(AssignmentType.HOMEWORK, 40.0);
        weights.put(AssignmentType.QUIZ, 20.0);
        weights.put(AssignmentType.MIDTERM, 20.0);
        weights.put(AssignmentType.FINAL, 20.0);

        Description description = new Description(
            "CS611",
            "Sample distributed systems course for local testing.",
            "Weekly lectures and labs.",
            weights
        );

        Course sampleCourse = new Course(
            "CS",
            611,
            "Distributed Systems",
            description,
            "Tue/Thu 2:00-3:15 PM",
            "Engineering 204",
            "CS 510"
        );

        semesterManager.addCourseToSemester(sampleSemester, sampleCourse);
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            // If the platform look and feel fails, Swing will use its default.
        }
    }
}
