package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Course;
import models.Semester;

public class SemesterManager {
    private final List<Semester> semesters;

    // Initializes semester manager
    public SemesterManager() {
        this.semesters = new ArrayList<>();
    }

    // Returns an unmodifiable list of all semesters
    public List<Semester> getSemesters() {
        return Collections.unmodifiableList(semesters);
    }

    // Creates a new semester with the given label
    public Semester addSemester(String label) {
        String normalizedLabel = normalizeRequired(label, "Semester label");

        if (findSemesterByLabel(normalizedLabel) != null) {
            throw new IllegalArgumentException("A semester with that label already exists.");
        }

        Semester semester = new Semester(normalizedLabel);
        semesters.add(semester);
        return semester;
    }

    // Adds a course to a semester
    public void addCourseToSemester(Semester semester, Course course) {
        if (semester == null) {
            throw new IllegalArgumentException("Semester is required.");
        }

        if (course == null) {
            throw new IllegalArgumentException("Course is required.");
        }

        if (hasCourse(semester, course.getDept(), course.getCode())) {
            throw new IllegalArgumentException("That course already exists in this semester.");
        }

        semester.addCourse(course);
    }

    // Returns all courses for the given semester
    public List<Course> getCoursesForSemester(Semester semester) {
        if (semester == null) {
            return Collections.emptyList();
        }

        return semester.getCourses();
    }

    // Finds and returns a semester by label (case-insensitive)
    private Semester findSemesterByLabel(String label) {
        for (Semester semester : semesters) {
            if (semester.getLabel().equalsIgnoreCase(label)) {
                return semester;
            }
        }
        return null;
    }

    // Checks if a course exists in a semester
    private boolean hasCourse(Semester semester, String dept, int code) {
        for (Course existingCourse : semester.getCourses()) {
            if (existingCourse.getDept().equalsIgnoreCase(dept) && existingCourse.getCode() == code) {
                return true;
            }
        }
        return false;
    }

    // Validates and normalizes required text fields
    private String normalizeRequired(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }
}
