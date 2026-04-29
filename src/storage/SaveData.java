package storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO classes used exclusively for JSON serialization/deserialization.
 * These are never used by the rest of the application.
 */
public class SaveData {
    List<SemesterRecord> semesters = new ArrayList<>();

    // --- Semester ---

    static class SemesterRecord {
        String label;
        List<CourseRecord> courses = new ArrayList<>();
    }

    // --- Course ---

    static class CourseRecord {
        String dept;
        int code;
        String name;
        String meetingTimes;
        String building;
        String prereqs;
        DescriptionRecord description;
        List<StudentRecord> students = new ArrayList<>();
        List<AssignmentRecord> assignments = new ArrayList<>();
        List<SubmissionRecord> submissions = new ArrayList<>();
        Map<Integer, String> studentGrades = new HashMap<>(); // studentId -> LetterGrade name
    }

    // --- Student ---

    static class StudentRecord {
        String type;           // "UNDERGRAD" or "GRAD"
        int id;
        String name;
        String email;
        int gradYear;
        String status;         // StudentStatus enum name
        String finalGrade;     // LetterGrade enum name
        String currentGrade;   // LetterGrade enum name
        DateRecord dob;
        Map<String, String> attendance = new HashMap<>(); // "yyyy-M-d" -> AttendanceStatus name
    }

    // --- Assignment ---

    static class AssignmentRecord {
        int id;
        String name;
        String type;           // AssignmentType enum name, null if custom
        String customType;     // null if enum type
        int totalPoints;
        DateRecord dueDate;
        DateRecord lateDueDate;
        double latePenalty;
        Map<String, Double> gradeBreakdown = new HashMap<>();
        DescriptionRecord description;
    }

    // --- Submission ---

    static class SubmissionRecord {
        int studentId;
        int assignmentId;
        double pointsEarned;
        double penalty;
        DateRecord submissionDate;
        String gradingStatus;  // GradingStatus enum name
        Map<String, Double> breakdownEarned = new HashMap<>();
        String notes;
    }

    // --- Date (mirrors models.Date fields) ---

    static class DateRecord {
        int year;
        int month;
        int day;
        Integer hour;
        Integer minute;
    }

    // --- Description (mirrors models.Description fields) ---

    static class DescriptionRecord {
        String title;
        String descriptionText;
        String syllabusText;
        Map<String, Double> assignmentWeights = new HashMap<>();       // AssignmentType name -> weight
        Map<String, Double> customAssignmentWeights = new HashMap<>(); // custom name -> weight
    }
}
