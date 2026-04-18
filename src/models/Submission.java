package models;
import enums.*;

public class Submission {
    private Assignment assignment;
    private Student author;
    private double pointsEarned;
    private int possiblePoints;
    private boolean isLate;
    private Date dueDate;
    private Date lateDueDate;
    private Date submissionDate;
    private GradingStatus gradingStatus;

    // public boolean isLate(){
    //     //return this.submissionDate <= this.dueDate;
    // }
}
