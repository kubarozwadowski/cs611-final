package models;

import enums.AssignmentType;

import java.util.*;

public class Assignment {
    private int id;
    private String name;
    private AssignmentType type;
    private int totalPoints;
    private HashMap<String, Double> gradeBreakdown;
    private Date dueDate;
    private Description description;

    public Assignment(int id, String name, AssignmentType type, int totalPoints, Date dueDate, Description description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.totalPoints = totalPoints;
        this.gradeBreakdown = new HashMap<>();
        this.dueDate = dueDate;
        this.description = description;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AssignmentType getType() {
        return type;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public HashMap<String, Double> getGradeBreakdown() {
        return gradeBreakdown;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Description getDescription() {
        return description;
    }
}
