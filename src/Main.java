import java.util.EnumMap;
import java.util.Map;

import enums.AssignmentType;
import enums.StudentStatus;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import logic.SemesterManager;
import models.Assignment;
import models.Course;
import models.Date;
import models.Description;
import models.GradStudent;
import models.Semester;
import models.UndergradStudent;
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

        // Add sample students
        sampleCourse.addStudent(new UndergradStudent(1001, "Alice Johnson", new Date(), "alice@university.edu", 2027, StudentStatus.UNDERGRADUATE));
        sampleCourse.addStudent(new UndergradStudent(1002, "Bob Smith", new Date(), "bob@university.edu", 2026, StudentStatus.UNDERGRADUATE));
        sampleCourse.addStudent(new UndergradStudent(1003, "Carol White", new Date(), "carol@university.edu", 2027, StudentStatus.UNDERGRADUATE));
        sampleCourse.addStudent(new GradStudent(1004, "David Lee", new Date(), "david@university.edu", 2025, StudentStatus.MASTER));
        sampleCourse.addStudent(new GradStudent(1005, "Eve Martinez", new Date(), "eve@university.edu", 2026, StudentStatus.PHD));

        // Add sample assignments
        Description hwDesc = new Description("Homework 1", "Design a distributed consensus protocol");
        sampleCourse.addAssignment(new Assignment(101, "Homework 1", AssignmentType.HOMEWORK, 100, 
            new Date(2026, 2, 15, 23, 59), hwDesc));

        Description hw2Desc = new Description("Homework 2", "Implement a simple message passing system");
        sampleCourse.addAssignment(new Assignment(102, "Homework 2", AssignmentType.HOMEWORK, 100,
            new Date(2026, 3, 1, 23, 59), hw2Desc));

        Description quiz1Desc = new Description("Quiz 1", "Concepts of distributed systems");
        sampleCourse.addAssignment(new Assignment(103, "Quiz 1", AssignmentType.QUIZ, 50,
            new Date(2026, 2, 22, 14, 0), quiz1Desc));

        Description midtermDesc = new Description("Midterm", "Comprehensive midterm exam");
        sampleCourse.addAssignment(new Assignment(104, "Midterm Exam", AssignmentType.MIDTERM, 150,
            new Date(2026, 3, 15, 14, 0), midtermDesc));

        Description finalDesc = new Description("Final", "Comprehensive final exam");
        sampleCourse.addAssignment(new Assignment(105, "Final Exam", AssignmentType.FINAL, 200,
            new Date(2026, 5, 10, 14, 0), finalDesc));

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
