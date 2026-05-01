package models;

import java.util.*;

import enums.LetterGrade;
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
    EnumMap<LetterGrade, Double> gradeCutoffs;

    public Course(String dept, int code, String name, Description description) {
        this(dept, code, name, description, "", "", "", null);
    }

    public Course(String dept, int code, String name, Description description, String meetingTimes, String building, String prereqs) {
        this(dept, code, name, description, meetingTimes, building, prereqs, null);
    }

    public Course(String dept, int code, String name, Description description, String meetingTimes, String building,
            String prereqs, Map<LetterGrade, Double> gradeCutoffs) {
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
        this.gradeCutoffs = new EnumMap<>(LetterGrade.class);
        setGradeCutoffs(gradeCutoffs == null ? getDefaultGradeCutoffs() : gradeCutoffs);
    }

    public static EnumMap<LetterGrade, Double> getDefaultGradeCutoffs() {
        EnumMap<LetterGrade, Double> defaults = new EnumMap<>(LetterGrade.class);
        defaults.put(LetterGrade.A, 93.0);
        defaults.put(LetterGrade.A_MINUS, 90.0);
        defaults.put(LetterGrade.B_PLUS, 87.0);
        defaults.put(LetterGrade.B, 83.0);
        defaults.put(LetterGrade.B_MINUS, 80.0);
        defaults.put(LetterGrade.C_PLUS, 77.0);
        defaults.put(LetterGrade.C, 73.0);
        defaults.put(LetterGrade.C_MINUS, 70.0);
        defaults.put(LetterGrade.D_PLUS, 67.0);
        defaults.put(LetterGrade.D, 63.0);
        defaults.put(LetterGrade.D_MINUS, 60.0);
        defaults.put(LetterGrade.F, 0.0);
        return defaults;
    }

    public static void validateGradeCutoffs(Map<LetterGrade, Double> cutoffs) {
        if (cutoffs == null) {
            throw new IllegalArgumentException("Grade cutoffs are required.");
        }

        for (LetterGrade grade : LetterGrade.values()) {
            if (!cutoffs.containsKey(grade)) {
                throw new IllegalArgumentException("Missing cutoff for " + grade + ".");
            }
            Double cutoff = cutoffs.get(grade);
            if (cutoff == null) {
                throw new IllegalArgumentException("Cutoff for " + grade + " cannot be empty.");
            }
            if (cutoff < 0.0 || cutoff > 100.0) {
                throw new IllegalArgumentException("Cutoff for " + grade + " must be between 0 and 100.");
            }
        }

        double previous = 101.0;
        for (LetterGrade grade : LetterGrade.values()) {
            double current = cutoffs.get(grade);
            if (current > previous) {
                throw new IllegalArgumentException("Grade cutoffs must be in descending order from A to F.");
            }
            previous = current;
        }
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

    public Map<LetterGrade, Double> getGradeCutoffs() {
        return Collections.unmodifiableMap(gradeCutoffs);
    }

    public void setGradeCutoffs(Map<LetterGrade, Double> cutoffs) {
        validateGradeCutoffs(cutoffs);
        this.gradeCutoffs.clear();
        this.gradeCutoffs.putAll(cutoffs);
    }

    public LetterGrade toLetterGrade(double percentage) {
        for (LetterGrade grade : LetterGrade.values()) {
            if (percentage >= gradeCutoffs.get(grade)) {
                return grade;
            }
        }
        return LetterGrade.F;
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

    @Override
    public String toString() {
        return getDisplayLabel();
    }
}
