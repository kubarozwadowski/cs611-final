package storage;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import enums.AssignmentType;
import enums.AttendanceStatus;
import enums.GradingStatus;
import enums.LetterGrade;
import enums.StudentStatus;
import logic.SemesterManager;
import models.*;
import storage.SaveData.*;

public class DataLoader {
    private final String filePath;
    private final Gson gson;

    public DataLoader(String filePath, Gson gson) {
        this.filePath = filePath;
        this.gson = gson;
    }

    /**
     * Loads the saved state from disk.
     * Returns null if no save file exists yet.
     */
    public SemesterManager load() {
        File file = new File(filePath);
        if (!file.exists()) return null;

        try (Reader reader = new FileReader(file)) {
            SaveData data = gson.fromJson(reader, SaveData.class);
            return buildSemesterManager(data);
        } catch (IOException e) {
            System.err.println("Failed to load data: " + e.getMessage());
            return null;
        }
    }

    // --- Reconstruct live objects from DTOs ---

    private SemesterManager buildSemesterManager(SaveData data) {
        SemesterManager manager = new SemesterManager();
        for (SemesterRecord semRec : data.semesters) {
            Semester semester = manager.addSemester(semRec.label);
            for (CourseRecord courseRec : semRec.courses) {
                Course course = buildCourse(courseRec);
                manager.addCourseToSemester(semester, course);
            }
        }
        return manager;
    }

    private Course buildCourse(CourseRecord rec) {
        Description description = buildDescription(rec.description);
        Course course = new Course(rec.dept, rec.code, rec.name, description,
                rec.meetingTimes, rec.building, rec.prereqs);

        // Reconstruct students
        Map<Integer, Student> studentById = new HashMap<>();
        for (StudentRecord sRec : rec.students) {
            Student student = buildStudent(sRec);
            studentById.put(student.getId(), student);
            course.addStudent(student);
        }

        // Reconstruct assignments
        Map<Integer, Assignment> assignmentById = new HashMap<>();
        for (AssignmentRecord aRec : rec.assignments) {
            Assignment assignment = buildAssignment(aRec);
            assignmentById.put(assignment.getId(), assignment);
            course.addAssignment(assignment);
        }

        // Reconstruct submissions — resolve IDs back into real object references
        for (SubmissionRecord sRec : rec.submissions) {
            Student student = studentById.get(sRec.studentId);
            Assignment assignment = assignmentById.get(sRec.assignmentId);

            if (student == null || assignment == null) {
                System.err.println("Skipping submission — could not resolve studentId="
                        + sRec.studentId + " or assignmentId=" + sRec.assignmentId);
                continue;
            }

            Submission submission = buildSubmission(sRec, student, assignment);
            student.getSubmissions().add(submission);
        }

        // Restore grades map — studentId -> LetterGrade
        for (Map.Entry<Integer, String> entry : rec.studentGrades.entrySet()) {
            Student student = studentById.get(entry.getKey());
            if (student != null) {
                course.getGrades().put(student, LetterGrade.valueOf(entry.getValue()));
            }
        }

        return course;
    }

    private Student buildStudent(StudentRecord rec) {
        models.Date dob = buildDate(rec.dob);
        StudentStatus status = StudentStatus.valueOf(rec.status);

        Student student;
        if ("UNDERGRAD".equals(rec.type)) {
            student = new UndergradStudent(rec.id, rec.name, dob, rec.email, rec.gradYear, status);
        } else {
            student = new GradStudent(rec.id, rec.name, dob, rec.email, rec.gradYear, status);
        }

        // Restore grades
        if (rec.finalGrade != null)   student.setFinalGrade(LetterGrade.valueOf(rec.finalGrade));
        if (rec.currentGrade != null) student.setCurrentGrade(LetterGrade.valueOf(rec.currentGrade));

        // Restore attendance
        for (Map.Entry<String, String> entry : rec.attendance.entrySet()) {
            models.Date date = parseDateKey(entry.getKey());
            AttendanceStatus status2 = AttendanceStatus.valueOf(entry.getValue());
            if (date != null) student.getAttendance().put(date, status2);
        }

        return student;
    }

    private Assignment buildAssignment(AssignmentRecord rec) {
        models.Date dueDate = buildDate(rec.dueDate);
        models.Date lateDueDate = buildDate(rec.lateDueDate);
        Description description = buildDescription(rec.description);

        if (lateDueDate != null) {
            if (rec.type != null) {
                return new Assignment(rec.id, rec.name, AssignmentType.valueOf(rec.type),
                        rec.totalPoints, rec.gradeBreakdown, dueDate, lateDueDate, rec.latePenalty, description);
            } else {
                return new Assignment(rec.id, rec.name, rec.customType,
                        rec.totalPoints, rec.gradeBreakdown, dueDate, lateDueDate, rec.latePenalty, description);
            }
        } else {
            if (rec.type != null) {
                return new Assignment(rec.id, rec.name, AssignmentType.valueOf(rec.type),
                        rec.totalPoints, rec.gradeBreakdown, dueDate, description);
            } else {
                return new Assignment(rec.id, rec.name, rec.customType,
                        rec.totalPoints, rec.gradeBreakdown, dueDate, description);
            }
        }
    }

    private Submission buildSubmission(SubmissionRecord rec, Student student, Assignment assignment) {
        models.Date submissionDate = buildDate(rec.submissionDate);
        Submission submission = new Submission(assignment, student, submissionDate,
                rec.pointsEarned, rec.penalty);
        submission.setGradingStatus(GradingStatus.valueOf(rec.gradingStatus));
        submission.setNotes(rec.notes != null ? rec.notes : "");
        if (rec.breakdownEarned != null) {
            submission.setBreakdownEarned(new java.util.HashMap<>(rec.breakdownEarned));
        }
        return submission;
    }

    private Description buildDescription(DescriptionRecord rec) {
        if (rec == null) return new Description("", "");
        java.util.EnumMap<AssignmentType, Double> weights = new java.util.EnumMap<>(AssignmentType.class);
        for (Map.Entry<String, Double> entry : rec.assignmentWeights.entrySet()) {
            weights.put(AssignmentType.valueOf(entry.getKey()), entry.getValue());
        }
        return new Description(rec.title, rec.descriptionText, rec.syllabusText,
                weights, rec.customAssignmentWeights);
    }

    private models.Date buildDate(DateRecord rec) {
        if (rec == null) return null;
        if (rec.hour != null && rec.minute != null) {
            return new models.Date(rec.year, rec.month, rec.day, rec.hour, rec.minute);
        }
        return new models.Date(rec.year, rec.month, rec.day);
    }

    private models.Date parseDateKey(String key) {
        try {
            String[] parts = key.split("-");
            return new models.Date(Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (Exception e) {
            return null;
        }
    }

}
