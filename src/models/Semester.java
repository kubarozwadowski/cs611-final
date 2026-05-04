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

    public Semester(String label) {
        this.label = label;
        this.courses = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }

    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    @Override
    public String toString() {
        return label;
    }

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
