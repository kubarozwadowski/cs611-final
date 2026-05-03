package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.AssignmentType;
import enums.LetterGrade;
import models.Assignment;
import models.Course;
import models.Student;
import models.Submission;

public class GradeCalculator {
    private final Course course;

    public GradeCalculator(Course course) {
        this.course = course;
    }

    /**
     * Recalculates and updates the current grade for every student in the course.
     */
    public void calculateAllGrades() {
        for (Student student : course.getStudents()) {
            calculateStudentGrade(student);
        }
    }

    /**
     * Calculates the weighted current grade for a single student.
     * Only categories that have at least one graded submission are included.
     * Weights are renormalized so the result is always out of 100%.
     *
     * Returns the final percentage (0–100).
     */
    public double calculateStudentGrade(Student student) {
        // Build a fast lookup: assignmentId -> graded Submission
        Map<Integer, Submission> submissionByAssignmentId = new HashMap<>();
        for (Submission sub : student.getSubmissions()) {
            if (sub.isGraded()) {
                submissionByAssignmentId.put(sub.getAssignment().getId(), sub);
            }
        }

        double totalWeightedScore = 0;
        double totalWeightUsed    = 0;

        // --- Enum type categories ---
        for (Map.Entry<AssignmentType, Double> entry : course.getDescription().getAssignmentWeights().entrySet()) {
            AssignmentType type = entry.getKey();
            double weight = entry.getValue();

            double contribution = categoryContribution(
                getAssignmentsByEnumType(type), submissionByAssignmentId);

            if (contribution < 0) continue; // no graded submissions in this category

            totalWeightedScore += contribution * weight;
            totalWeightUsed    += weight;
        }

        // --- Custom type categories ---
        for (Map.Entry<String, Double> entry : course.getDescription().getCustomAssignmentWeights().entrySet()) {
            String customType = entry.getKey();
            double weight = entry.getValue();

            double contribution = categoryContribution(
                getAssignmentsByCustomType(customType), submissionByAssignmentId);

            if (contribution < 0) continue;

            totalWeightedScore += contribution * weight;
            totalWeightUsed    += weight;
        }

        if (totalWeightUsed == 0) return 0;

        // Renormalize: scale to 100% based only on graded categories
        double finalPercentage = (totalWeightedScore / totalWeightUsed) * 100.0;

        // Update student and course grade map
        LetterGrade letterGrade = course.toLetterGrade(finalPercentage);
        student.setCurrentGrade(letterGrade);
        course.getGrades().put(student, letterGrade);

        return finalPercentage;
    }

    /**
     * Returns the average score ratio (0.0–1.0) across all graded assignments
     * in a category. Returns -1 if no assignments in this category are graded yet.
     */
    private double categoryContribution(List<Assignment> assignments,
                                        Map<Integer, Submission> submissionMap) {
        double scoreSum = 0;
        int gradedCount = 0;

        for (Assignment assignment : assignments) {
            Submission sub = submissionMap.get(assignment.getId());
            if (sub != null && assignment.getTotalPoints() > 0) {
                scoreSum += sub.getFinalScore() / assignment.getTotalPoints();
                gradedCount++;
            }
        }

        if (gradedCount == 0) return -1;
        return scoreSum / gradedCount; // average ratio for this category
    }

    // --- Helpers ---

    private List<Assignment> getAssignmentsByEnumType(AssignmentType type) {
        List<Assignment> result = new ArrayList<>();
        for (Assignment a : course.getAssignments()) {
            if (a.getType() == type) result.add(a);
        }
        return result;
    }

    private List<Assignment> getAssignmentsByCustomType(String customType) {
        List<Assignment> result = new ArrayList<>();
        for (Assignment a : course.getAssignments()) {
            if (customType.equals(a.getCustomType())) result.add(a);
        }
        return result;
    }
}
