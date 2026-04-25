package models;
import enums.*;
import interfaces.Gradeable;

import java.util.*;

public class Submission implements Gradeable{
    private Assignment assignment;
    private Student author;
    private double pointsEarned;
    private Date submissionDate;
    private GradingStatus gradingStatus;
    private HashMap<String, Double> breakdownEarned;
    private String notes; 
    private double penalty; 

    //minimal constructor
    public Submission(Assignment assignment, Student author, Date submissionDate){
        this.assignment = assignment;
        this.author = author;
        this.submissionDate = submissionDate;
        this.pointsEarned = 0;
        this.gradingStatus = GradingStatus.UNGRADED;
        this.breakdownEarned = new HashMap<>();
        this.notes = "";
        this.penalty = 0;
    }

    // Constructor for CSV import
    public Submission(Assignment assignment, Student author, Date submissionDate, double pointsEarned, double penalty){
        this(assignment, author, submissionDate); // reuse minimal constructor
        this.pointsEarned = pointsEarned;
        this.penalty = penalty;
        this.gradingStatus = GradingStatus.GRADED;
    }


    public boolean isLate(){
        if (this.submissionDate == null || assignment.getDueDate() == null) 
            return false;
        return this.submissionDate.compareTo(assignment.getDueDate()) > 0;
    }

    public double getFinalScore(){
        return Math.max(0, this.pointsEarned - this.penalty);
    }

    public double getFinalScorePercentage(){
        if (assignment.getTotalPoints() != 0){
            return (this.getFinalScore() / assignment.getTotalPoints());
        }
        return 0.0;
    }

    // Gradeable

    public void grade(){ 
        this.gradingStatus = GradingStatus.GRADED;
    }
    public boolean isGraded(){ 
        return this.gradingStatus == GradingStatus.GRADED; 
    }
    public GradingStatus getGradingStatus(){ 
        return this.gradingStatus; 
    }

    // Getters
    public Assignment getAssignment(){ 
        return assignment; 
    }
    public Student getAuthor(){
         return author; 
    }
    public double getPointsEarned(){ 
        return pointsEarned;
    }
    public double getPenalty(){ 
        return penalty; 
    }
    public Date getSubmissionDate(){ 
        return submissionDate; 
    }
    public Map<String, Double> getBreakdownEarned(){ 
        return Collections.unmodifiableMap(breakdownEarned); 
    }
    public String getNotes(){ 
        return notes; 
    }
    public int getPossiblePoints(){ 
        return assignment.getTotalPoints(); 
    }

    // Setters (mutable fields only)
    public void setPointsEarned(double pointsEarned){
        this.pointsEarned = pointsEarned; 
    }
    public void setPenalty(double penalty){
         this.penalty = penalty; 
        }
    public void setNotes(String notes){ 
        this.notes = notes; 
    }
    public void setBreakdownEarned(HashMap<String, Double> breakdown){
         this.breakdownEarned = breakdown; 
    }
    public void setGradingStatus(GradingStatus status){
         this.gradingStatus = status; 
    }
}
