package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Assignment;
import models.Course;
import models.Date;
import models.Student;
import models.Submission;

public class CSVGradeImporter {
    private final Course course;
    private final Assignment assignment;

    public CSVGradeImporter(Course course, Assignment assignment) {
        this.course = course;
        this.assignment = assignment;
    }

    public ImportResult importFromFile(File file) {
        List<String> warnings = new ArrayList<>();
        int imported = 0;
        int skipped = 0;

        // Build SID -> student lookup map
        Map<Integer, Student> studentBySid = new HashMap<>();
        for (Student s : course.getStudents()) {
            studentBySid.put(s.getId(), s);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                warnings.add("CSV file is empty.");
                return new ImportResult(0, 0, warnings);
            }

            // Find column indices from header
            String[] headers = parseCSVLine(headerLine);
            int sidIdx = -1, totalScoreIdx = -1, maxPointsIdx = -1;
            int latenessIdx = -1, submissionTimeIdx = -1, statusIdx = -1;

            for (int i = 0; i < headers.length; i++) {
                String h = headers[i].trim().toLowerCase();
                switch (h) {
                    case "sid":             sidIdx = i;             break;
                    case "total score":     totalScoreIdx = i;      break;
                    case "max points":      maxPointsIdx = i;       break;
                    case "submission time": submissionTimeIdx = i;  break;
                    case "status":          statusIdx = i;          break;
                    default:
                        if (h.startsWith("lateness")) latenessIdx = i;
                        break;
                }
            }

            // Reject if required columns are missing
            if (sidIdx == -1) {
                warnings.add("Missing required column: SID. Aborting import.");
                return new ImportResult(0, 0, warnings);
            }
            if (totalScoreIdx == -1) {
                warnings.add("Missing required column: Total Score. Aborting import.");
                return new ImportResult(0, 0, warnings);
            }

            boolean maxPointsValidated = false;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] cols = parseCSVLine(line);

                // Validate max points once on first data row
                if (!maxPointsValidated && maxPointsIdx != -1 && cols.length > maxPointsIdx) {
                    maxPointsValidated = true;
                    try {
                        double csvMax = Double.parseDouble(cols[maxPointsIdx].trim());
                        if (Math.abs(csvMax - assignment.getTotalPoints()) > 0.001) {
                            warnings.add("Max points mismatch: CSV has " + csvMax
                                    + " pts but assignment has " + assignment.getTotalPoints()
                                    + " pts. Grades may be incorrect.");
                        }
                    } catch (NumberFormatException e) {
                        // ignore unparseable max points
                    }
                }

                if (cols.length <= sidIdx || cols.length <= totalScoreIdx) {
                    skipped++;
                    continue;
                }

                // Match student by SID
                int sid;
                try {
                    sid = Integer.parseInt(cols[sidIdx].trim());
                } catch (NumberFormatException e) {
                    skipped++;
                    continue;
                }

                Student student = studentBySid.get(sid);
                if (student == null) {
                    skipped++;
                    continue;
                }

                // If status is Missing, assign zero and skip further parsing
                if (statusIdx != -1 && cols.length > statusIdx
                        && cols[statusIdx].trim().equalsIgnoreCase("missing")) {
                    Submission submission = findExistingSubmission(student);
                    if (submission == null) {
                        submission = new Submission(assignment, student, new Date());
                        student.getSubmissions().add(submission);
                    }
                    submission.setPointsEarned(0);
                    submission.grade();
                    imported++;
                    continue;
                }

                // Parse total score
                double totalScore;
                try {
                    totalScore = Double.parseDouble(cols[totalScoreIdx].trim());
                } catch (NumberFormatException e) {
                    skipped++;
                    continue;
                }

                // Parse submission time
                Date submissionDate = null;
                if (submissionTimeIdx != -1 && cols.length > submissionTimeIdx) {
                    submissionDate = parseSubmissionTime(cols[submissionTimeIdx].trim());
                }

                // Parse lateness
                boolean isLate = false;
                if (latenessIdx != -1 && cols.length > latenessIdx) {
                    isLate = !isZeroLateness(cols[latenessIdx].trim());
                }

                // Determine points to store
                double pointsToStore = totalScore;
                if (isLate) {
                    Date lateDueDate = assignment.getLateDueDate();
                    if (lateDueDate != null && submissionDate != null
                            && submissionDate.compareTo(lateDueDate) > 0) {
                        // Submitted after late due date window — zero grade
                        pointsToStore = 0;
                    } else {
                        // Late but within window — deduct late penalty directly
                        pointsToStore = Math.max(0, totalScore - assignment.getLatePenalty());
                    }
                }

                // Find existing submission or create a new one
                Submission submission = findExistingSubmission(student);
                if (submission == null) {
                    Date dateForSubmission = submissionDate != null ? submissionDate : new Date();
                    submission = new Submission(assignment, student, dateForSubmission);
                    student.getSubmissions().add(submission);
                } else {
                    if (submissionDate != null) {
                        submission.setSubmissionDate(submissionDate);
                    }
                }

                submission.setPointsEarned(pointsToStore);
                submission.grade();
                imported++;
            }

        } catch (IOException e) {
            warnings.add("Error reading file: " + e.getMessage());
        }

        return new ImportResult(imported, skipped, warnings);
    }

    // --- Helpers ---

    private Submission findExistingSubmission(Student student) {
        for (Submission sub : student.getSubmissions()) {
            if (sub.getAssignment().equals(assignment)) {
                return sub;
            }
        }
        return null;
    }

    private boolean isZeroLateness(String lateness) {
        if (lateness == null || lateness.trim().isEmpty()) return true;
        // Format: H:MM:SS
        try {
            String[] parts = lateness.split(":");
            for (String part : parts) {
                if (Integer.parseInt(part.trim()) != 0) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private Date parseSubmissionTime(String timeStr) {
        // Format: "2026-04-30 20:43:30 -0400"
        try {
            String[] parts = timeStr.split(" ");
            String[] dateParts = parts[0].split("-");
            String[] timeParts = parts[1].split(":");
            int year   = Integer.parseInt(dateParts[0]);
            int month  = Integer.parseInt(dateParts[1]);
            int day    = Integer.parseInt(dateParts[2]);
            int hour   = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            return new Date(year, month, day, hour, minute);
        } catch (Exception e) {
            return null;
        }
    }

    private String[] parseCSVLine(String line) {
        // Handles quoted fields containing commas
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    // --- Result ---

    public static class ImportResult {
        public final int imported;
        public final int skipped;
        public final List<String> warnings;

        public ImportResult(int imported, int skipped, List<String> warnings) {
            this.imported = imported;
            this.skipped = skipped;
            this.warnings = warnings;
        }
    }
}
