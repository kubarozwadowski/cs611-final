package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Course;
import models.Semester;

public class SemesterManager {
    private final List<Semester> semesters;

    public SemesterManager() {
        this.semesters = new ArrayList<>();
    }

    public List<Semester> getSemesters() {
        return Collections.unmodifiableList(semesters);
    }

    public Semester addSemester(String label) {
        String normalizedLabel = normalizeRequired(label, "Semester label");

        if (findSemesterByLabel(normalizedLabel) != null) {
            throw new IllegalArgumentException("A semester with that label already exists.");
        }

        Semester semester = new Semester(normalizedLabel);
        semesters.add(semester);
        return semester;
    }

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

    public List<Course> getCoursesForSemester(Semester semester) {
        if (semester == null) {
            return Collections.emptyList();
        }

        return semester.getCourses();
    }

    private Semester findSemesterByLabel(String label) {
        for (Semester semester : semesters) {
            if (semester.getLabel().equalsIgnoreCase(label)) {
                return semester;
            }
        }
        return null;
    }

    private boolean hasCourse(Semester semester, String dept, int code) {
        for (Course existingCourse : semester.getCourses()) {
            if (existingCourse.getDept().equalsIgnoreCase(dept) && existingCourse.getCode() == code) {
                return true;
            }
        }
        return false;
    }

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
