package models;
import enums.*;
import java.util.*;

public abstract class Student {
    private int id;
    private String name;
    private Date dob;
    private String email;
    private LetterGrade finalGrade;
    private LetterGrade currentGrade;
    private List<Submission> submissions;
    private HashMap<Date, AttendanceStatus> attendance;
    private int gradYear;
    private StudentStatus status;

    public Student(int id, String name, Date dob, String email, int gradYear, StudentStatus status) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.gradYear = gradYear;
        this.finalGrade = LetterGrade.F;
        this.currentGrade = LetterGrade.F;
        this.submissions = new ArrayList<>();
        this.attendance = new HashMap<>();
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public LetterGrade getFinalGrade() {
        return finalGrade;
    }

    public LetterGrade getCurrentGrade() {
        return currentGrade;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public HashMap<Date, AttendanceStatus> getAttendance() {
        return attendance;
    }

    public int getGradYear() {
        return gradYear;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public double getAttendanceRate() {
        if (attendance.isEmpty()) {
            return 0.0;
        }
        
        int present = 0;

        for (AttendanceStatus status : attendance.values()) {
            if (status == AttendanceStatus.PRESENT) {
                present++;
            }
        }
        return (double) present / attendance.size() * 100;  
    }
}
