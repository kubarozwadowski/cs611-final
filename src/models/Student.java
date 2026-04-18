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
}
