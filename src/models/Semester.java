package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import interfaces.Summarizable;

public class Semester implements Summarizable{
    private final String label;
    private final List<Course> courses;

    // Initializes a semester with a label
    public Semester(String label) {
        this.label = label;
        this.courses = new ArrayList<>();
    }

    // Returns the semester label
    public String getLabel() {
        return label;
    }

    // Returns an unmodifiable list of courses in the semester
    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    // Adds a course to the semester
    public void addCourse(Course course) {
        courses.add(course);
    }

    // Returns string representation of the semester
    @Override
    public String toString() {
        return label;
    }

    // Returns a summary of the semester including course and student counts
    public String getSummary() {
        int totalAssignments = 0;
        Set<Student> uniqueStudents = new HashSet<>();
        for (Course course : courses) {
            totalAssignments += course.getAssignments().size();
            uniqueStudents.addAll(course.getStudents());
        }
        return label
                + " | " + courses.size() + " course(s)"
                + " | " + uniqueStudents.size() + " student(s)"
                + " | " + totalAssignments + " assignment(s)";
    }
}
