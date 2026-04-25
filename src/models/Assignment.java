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
    private double latePenalty;
    private Description description;

    // Constructors (no late policy)

    public Assignment(int id, String name, AssignmentType type, int totalPoints, Date dueDate, Description description) {
        this(id, name, type, null, totalPoints, new HashMap<>(), dueDate, null, 0, description);
    }

    public Assignment(int id, String name, AssignmentType type, int totalPoints, Map<String, Double> gradeBreakdown, Date dueDate, Description description) {
        this(id, name, type, null, totalPoints, gradeBreakdown, dueDate, null, 0, description);
    }

    public Assignment(int id, String name, String customType, int totalPoints, Date dueDate, Description description) {
        this(id, name, null, customType, totalPoints, new HashMap<>(), dueDate, null, 0, description);
    }

    public Assignment(int id, String name, String customType, int totalPoints, Map<String, Double> gradeBreakdown, Date dueDate, Description description) {
        this(id, name, null, customType, totalPoints, gradeBreakdown, dueDate, null, 0, description);
    }

    // Constructors with late policy 

    public Assignment(int id, String name, AssignmentType type, int totalPoints, Date dueDate, Date lateDueDate, double latePenalty, Description description) {
        this(id, name, type, null, totalPoints, new HashMap<>(), dueDate, lateDueDate, latePenalty, description);
    }

    public Assignment(int id, String name, AssignmentType type, int totalPoints, Map<String, Double> gradeBreakdown, Date dueDate, Date lateDueDate, double latePenalty, Description description) {
        this(id, name, type, null, totalPoints, gradeBreakdown, dueDate, lateDueDate, latePenalty, description);
    }

    public Assignment(int id, String name, String customType, int totalPoints, Date dueDate, Date lateDueDate, double latePenalty, Description description) {
        this(id, name, null, customType, totalPoints, new HashMap<>(), dueDate, lateDueDate, latePenalty, description);
    }

    public Assignment(int id, String name, String customType, int totalPoints, Map<String, Double> gradeBreakdown, Date dueDate, Date lateDueDate, double latePenalty, Description description) {
        this(id, name, null, customType, totalPoints, gradeBreakdown, dueDate, lateDueDate, latePenalty, description);
    }


    private Assignment(int id, String name, AssignmentType type, String customType, int totalPoints,
                       Map<String, Double> gradeBreakdown, Date dueDate, Date lateDueDate, double latePenalty,
                       Description description) {

        if ((type == null && (customType == null || customType.trim().isEmpty()))
            || (type != null && customType != null && !customType.trim().isEmpty())) {
            throw new IllegalArgumentException("Assignment must have either an enum type or a custom type, not both.");
        }

        if (lateDueDate != null && dueDate != null && lateDueDate.compareTo(dueDate) <= 0) {
            throw new IllegalArgumentException("Late due date must be after the regular due date.");
        }

        if (latePenalty < 0 || latePenalty >= totalPoints) {
            throw new IllegalArgumentException("Late penalty must be >= 0 and less than total points.");
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
        this.lateDueDate = lateDueDate;
        this.latePenalty = latePenalty;
        this.description = description;
    }

    // --- Getters ---

    public int getId()                          { return id; }
    public String getName()                     { return name; }
    public AssignmentType getType()             { return type; }
    public String getCustomType()               { return customType; }
    public int getTotalPoints()                 { return totalPoints; }
    public Date getDueDate()                    { return dueDate; }
    public Date getLateDueDate()                { return lateDueDate; }
    public double getLatePenalty()              { return latePenalty; }
    public Description getDescription()         { return description; }

    public Map<String, Double> getGradeBreakdown() {
        return Collections.unmodifiableMap(gradeBreakdown);
    }

    public String getTypeLabel() {
        return type != null ? type.name() : customType;
    }

    public String getDisplayLabel() {
        return name + " (" + getTypeLabel() + ", " + totalPoints + " pts)";
    }

    // --- Setters ---

    public void setName(String name)                { this.name = name; }
    public void setDueDate(Date dueDate)            { this.dueDate = dueDate; }
    public void setTotalPoints(int totalPoints)     { this.totalPoints = totalPoints; }
    public void setDescription(Description desc)    { this.description = desc; }

    public void setLateDueDate(Date lateDueDate) {
        if (lateDueDate != null && this.dueDate != null && lateDueDate.compareTo(this.dueDate) <= 0) {
            throw new IllegalArgumentException("Late due date must be after the regular due date.");
        }
        this.lateDueDate = lateDueDate;
    }

    public void setLatePenalty(double latePenalty) {
        if (latePenalty < 0 || latePenalty >= this.totalPoints) {
            throw new IllegalArgumentException("Late penalty must be >= 0 and less than total points.");
        }
        this.latePenalty = latePenalty;
    }

    // --- toString ---

    @Override
    public String toString() {
        return getDisplayLabel();
    }
}
