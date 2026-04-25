package models;

import enums.AssignmentType;

import java.util.*;

public class Assignment {
    private int id;
    private String name;
    private AssignmentType type;
    private String customType;
    private int totalPoints;
    private HashMap<String, Double> gradeBreakdown;
    private Date dueDate;
    private Date lateDueDate;
    private Description description;

    public Assignment(int id, String name, AssignmentType type, int totalPoints, Date dueDate, Description description) {
        this(id, name, type, totalPoints, new HashMap<>(), dueDate, description);
    }

    public Assignment(int id, String name, AssignmentType type, int totalPoints, Map<String, Double> gradeBreakdown, Date dueDate, Description description) {
        this(id, name, type, null, totalPoints, gradeBreakdown, dueDate, description);
    }

    public Assignment(int id, String name, String customType, int totalPoints, Date dueDate, Description description) {
        this(id, name, null, customType, totalPoints, new HashMap<>(), dueDate, description);
    }

    public Assignment(int id, String name, String customType, int totalPoints, Map<String, Double> gradeBreakdown, Date dueDate, Description description) {
        this(id, name, null, customType, totalPoints, gradeBreakdown, dueDate, description);
    }

    private Assignment(int id, String name, AssignmentType type, String customType, int totalPoints, Map<String, Double> gradeBreakdown, Date dueDate, Description description) {
        if ((type == null && (customType == null || customType.trim().isEmpty()))
            || (type != null && customType != null && !customType.trim().isEmpty())) {
            throw new IllegalArgumentException("Assignment must have either an enum type or a custom type.");
        }

        this.id = id;
        this.name = name;
        this.type = type;
        this.customType = customType == null ? null : customType.trim();
        this.totalPoints = totalPoints;
        this.gradeBreakdown = new HashMap<>();
        if (gradeBreakdown != null) {
            this.gradeBreakdown.putAll(gradeBreakdown);
        }
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

    public String getCustomType() {
        return customType;
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

    public String getTypeLabel() {
        if (type != null) {
            return type.name();
        }
        return customType;
    }

    public String getDisplayLabel() {
        return name + " (" + getTypeLabel() + ", " + totalPoints + " pts)";
    }

    @Override
    public String toString() {
        return getDisplayLabel();
    }
}
