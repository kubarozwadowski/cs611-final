package models;

import java.util.*;

import enums.LetterGrade;
import util.Description;
import interfaces.Summarizable;

public class Course implements Summarizable{
    String dept;
    int code;
    String name;
    String meetingTimes;
    String building;
    String prereqs;
    Description description;
    HashSet<Student> students;
    HashSet<Assignment> assignments;
    HashMap<Student, LetterGrade> grades; // Consider creating a grade object that stores a float instead of letter

    public Course(String dept, int code, String name, Description description) {
        this(dept, code, name, description, "", "", "");
    }

    public Course(String dept, int code, String name, Description description, String meetingTimes, String building, String prereqs) {
        this.dept = dept;
        this.code = code;
        this.name = name;
        this.description = description;
        this.meetingTimes = meetingTimes;
        this.building = building;
        this.prereqs = prereqs;
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

    public String getMeetingTimes() {
        return meetingTimes;
    }

    public String getBuilding() {
        return building;
    }

    public String getPrereqs() {
        return prereqs;
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

    public String getDisplayLabel() {
        return dept + " " + code + " - " + name;
    }

    public String getSummary(){
        return "Placeholder";
    }
}
