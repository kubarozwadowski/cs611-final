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
}
