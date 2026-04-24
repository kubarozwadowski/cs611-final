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

    public Description(String title, String descriptionText) {
        this(title, descriptionText, "", new EnumMap<>(AssignmentType.class), new LinkedHashMap<>());
    }

    public Description(String title, String descriptionText, String syllabusText, Map<AssignmentType, Double> assignmentWeights) {
        this(title, descriptionText, syllabusText, assignmentWeights, new LinkedHashMap<>());
    }

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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return descriptionText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public String getSyllabusText() {
        return syllabusText;
    }

    public Map<AssignmentType, Double> getAssignmentWeights() {
        return Collections.unmodifiableMap(assignmentWeights);
    }

    public Map<String, Double> getCustomAssignmentWeights() {
        return Collections.unmodifiableMap(customAssignmentWeights);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public void setSyllabusText(String syllabusText) {
        this.syllabusText = syllabusText;
    }
}
