package storage;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import enums.AssignmentType;
import enums.LetterGrade;
import logic.SemesterManager;
import models.*;
import storage.SaveData.*;

public class DataSaver {
    private final String filePath;
    private final Gson gson;

    public DataSaver(String filePath, Gson gson) {
        this.filePath = filePath;
        this.gson = gson;
    }

    public void save(SemesterManager semesterManager) {
        SaveData data = buildSaveData(semesterManager);

        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    // --- Build SaveData from live objects ---

    private SaveData buildSaveData(SemesterManager manager) {
        SaveData data = new SaveData();
        for (Semester semester : manager.getSemesters()) {
            data.semesters.add(buildSemesterRecord(semester));
        }
        return data;
    }

    private SemesterRecord buildSemesterRecord(Semester semester) {
        SemesterRecord rec = new SemesterRecord();
        rec.label = semester.getLabel();
        for (Course course : semester.getCourses()) {
            rec.courses.add(buildCourseRecord(course));
        }
        return rec;
    }

    private CourseRecord buildCourseRecord(Course course) {
        CourseRecord rec = new CourseRecord();
        rec.dept = course.getDept();
        rec.code = course.getCode();
        rec.name = course.getName();
        rec.meetingTimes = course.getMeetingTimes();
        rec.building = course.getBuilding();
        rec.prereqs = course.getPrereqs();
        rec.description = buildDescriptionRecord(course.getDescription());

        // Students — without submissions (stored separately below)
        for (Student student : course.getStudents()) {
            rec.students.add(buildStudentRecord(student));
        }

        // Assignments
        for (Assignment assignment : course.getAssignments()) {
            rec.assignments.add(buildAssignmentRecord(assignment));
        }

        // Submissions — stored as DTOs with IDs only to avoid circular refs
        for (Student student : course.getStudents()) {
            for (Submission sub : student.getSubmissions()) {
                rec.submissions.add(buildSubmissionRecord(sub));
            }
        }

        // Grades map — studentId -> LetterGrade name
        for (Map.Entry<Student, LetterGrade> entry : course.getGrades().entrySet()) {
            rec.studentGrades.put(entry.getKey().getId(), entry.getValue().name());
        }

        return rec;
    }

    private StudentRecord buildStudentRecord(Student student) {
        StudentRecord rec = new StudentRecord();
        rec.type = (student instanceof UndergradStudent) ? "UNDERGRAD" : "GRAD";
        rec.id = student.getId();
        rec.name = student.getName();
        rec.email = student.getEmail();
        rec.gradYear = student.getGradYear();
        rec.status = student.getStatus().name();
        rec.finalGrade = student.getFinalGrade().name();
        rec.currentGrade = student.getCurrentGrade().name();
        rec.dob = buildDateRecord(student.getDob());

        // Attendance — convert Date key to "yyyy-M-d" string
        for (Map.Entry<models.Date, enums.AttendanceStatus> entry : student.getAttendance().entrySet()) {
            String key = dateKey(entry.getKey());
            rec.attendance.put(key, entry.getValue().name());
        }

        return rec;
    }

    private AssignmentRecord buildAssignmentRecord(Assignment assignment) {
        AssignmentRecord rec = new AssignmentRecord();
        rec.id = assignment.getId();
        rec.name = assignment.getName();
        rec.type = assignment.getType() != null ? assignment.getType().name() : null;
        rec.customType = assignment.getCustomType();
        rec.totalPoints = assignment.getTotalPoints();
        rec.dueDate = buildDateRecord(assignment.getDueDate());
        rec.lateDueDate = buildDateRecord(assignment.getLateDueDate());
        rec.latePenalty = assignment.getLatePenalty();
        rec.gradeBreakdown = new HashMap<>(assignment.getGradeBreakdown());
        rec.description = buildDescriptionRecord(assignment.getDescription());
        return rec;
    }

    private SubmissionRecord buildSubmissionRecord(Submission sub) {
        SubmissionRecord rec = new SubmissionRecord();
        rec.studentId = sub.getAuthor().getId();
        rec.assignmentId = sub.getAssignment().getId();
        rec.pointsEarned = sub.getPointsEarned();
        rec.penalty = sub.getPenalty();
        rec.submissionDate = buildDateRecord(sub.getSubmissionDate());
        rec.gradingStatus = sub.getGradingStatus().name();
        rec.breakdownEarned = new HashMap<>(sub.getBreakdownEarned());
        rec.notes = sub.getNotes();
        return rec;
    }

    private DescriptionRecord buildDescriptionRecord(Description desc) {
        if (desc == null) return null;
        DescriptionRecord rec = new DescriptionRecord();
        rec.title = desc.getTitle();
        rec.descriptionText = desc.getDescriptionText();
        rec.syllabusText = desc.getSyllabusText();

        for (Map.Entry<AssignmentType, Double> entry : desc.getAssignmentWeights().entrySet()) {
            rec.assignmentWeights.put(entry.getKey().name(), entry.getValue());
        }
        rec.customAssignmentWeights = new HashMap<>(desc.getCustomAssignmentWeights());

        return rec;
    }

    private DateRecord buildDateRecord(models.Date date) {
        if (date == null) return null;
        DateRecord rec = new DateRecord();
        rec.year = date.getYear();
        rec.month = date.getMonth();
        rec.day = date.getDay();
        rec.hour = date.getHour();
        rec.minute = date.getMinute();
        return rec;
    }

    private String dateKey(models.Date date) {
        if (date == null) return "";
        return date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
    }
}
