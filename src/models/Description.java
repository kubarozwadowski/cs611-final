package models;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import enums.AssignmentType;

public class Description {
    private String title;
    private String descriptionText;
    private String syllabusText;
    private final Map<AssignmentType, Double> assignmentWeights;
    private final Map<String, Double> customAssignmentWeights;

    // Constructor with title and description
    public Description(String title, String descriptionText) {
        this(title, descriptionText, "", new EnumMap<>(AssignmentType.class), new LinkedHashMap<>());
    }

    // Constructor with title, description, and assignment weights
    public Description(String title, String descriptionText, String syllabusText, Map<AssignmentType, Double> assignmentWeights) {
        this(title, descriptionText, syllabusText, assignmentWeights, new LinkedHashMap<>());
    }

    // Full constructor with all fields
    public Description(String title, String descriptionText, String syllabusText, Map<AssignmentType, Double> assignmentWeights, Map<String, Double> customAssignmentWeights) {
        this.title = title;
        this.descriptionText = descriptionText;
        this.syllabusText = syllabusText;
        this.assignmentWeights = new EnumMap<>(AssignmentType.class);
        this.customAssignmentWeights = new LinkedHashMap<>();
        if (assignmentWeights != null) {
            this.assignmentWeights.putAll(assignmentWeights);
        }
        if (customAssignmentWeights != null) {
            this.customAssignmentWeights.putAll(customAssignmentWeights);
        }
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Getter for description
    public String getDescription() {
        return descriptionText;
    }

    // Getter for description text
    public String getDescriptionText() {
        return descriptionText;
    }

    // Getter for syllabus text
    public String getSyllabusText() {
        return syllabusText;
    }

    // Getter for assignment weights
    public Map<AssignmentType, Double> getAssignmentWeights() {
        return Collections.unmodifiableMap(assignmentWeights);
    }

    // Getter for custom assignment weights
    public Map<String, Double> getCustomAssignmentWeights() {
        return Collections.unmodifiableMap(customAssignmentWeights);
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Setter for description
    public void setDescription(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    // Setter for description text
    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    // Setter for syllabus text
    public void setSyllabusText(String syllabusText) {
        this.syllabusText = syllabusText;
    }

    // Setter for assignment weights
    public void setAssignmentWeights(Map<AssignmentType, Double> assignmentWeights) {
        this.assignmentWeights.clear();
        if (assignmentWeights != null) {
            this.assignmentWeights.putAll(assignmentWeights);
        }
    }

    // Setter for custom assignment weights
    public void setCustomAssignmentWeights(Map<String, Double> customAssignmentWeights) {
        this.customAssignmentWeights.clear();
        if (customAssignmentWeights != null) {
            this.customAssignmentWeights.putAll(customAssignmentWeights);
        }
    }
}
