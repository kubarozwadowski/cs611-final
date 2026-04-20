package models;

import java.util.*;

import enums.LetterGrade;
import util.Description;

public class Course {
    String dept;
    int code;
    String name;
    Description description;
    HashSet<Student> students;
    HashSet<Assignment> assignments;
    HashMap<Student, LetterGrade> grades; // Consider creating a grade object that stores a float instead of letter

    public Course(String dept, int code, String name, Description description) {
        this.dept = dept;
        this.code = code;
        this.name = name;
        this.description = description;
        this.students = new HashSet<>();
        this.assignments = new HashSet<>();
        this.grades = new HashMap<>();
    }

    // Getters
    public String getDept() {
        return dept;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Description getDescription() {
        return description;
    }

    public HashSet<Student> getStudents() {
        return students;
    }

    public HashSet<Assignment> getAssignments() {
        return assignments;
    }

    public HashMap<Student, LetterGrade> getGrades() {
        return grades;
    }

    // Util Methods
    public void addStudent(Student student) {
        students.add(student);
        initGrade(student);
    }

    private void initGrade(Student student) {
        grades.put(student, LetterGrade.A);
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }
}
