# Object Design Document

## Overview

This document outlines the overall design choices made for the grading system final project for CS 611.

## Core object model

### SemesterManager

The `SemesterManager` keeps track and manages all of the semesters that have been defined by the user of the application. It handles validation, creation, and management of the different semesters that have been defined by the client.

See [SemesterManager.java](src/logic/SemesterManager.java#L10-L60).

**Design choice:**
This class acts as the application’s coordination point for semester and course management. Centralizing the validation here keeps the GUI simple and avoids duplicating rules across multiple screens.

### Semester

At the highest level our structure starts with semesters. This is the highest level object that everything else is nested within. Each semester can then contain multiple courses and continue down that level until we get to student and assignment level management.

`Semester` is a lightweight container for courses.

See [Semester.java](src/models/Semester.java#L1-L40).

**Design choice:**
A semester only needs a label and a list of courses, so the class stays small and focused. This makes the object easy to store, display, and serialize.

It also helps organizationally by breaking down courses to a semester level which is easier to manage than having all courses ever taught being shown on the same screen.

### Course

`Course` is the central academic object. It stores course metadata, enrolled students, assignments, recorded grades, and course-specific letter grade cutoffs.

See [Course.java](src/models/Course.java#L8-L176).

Important parts include:
- default letter grade cutoffs in [Course.java](src/models/Course.java#L45-L61)
- validation in [Course.java](src/models/Course.java#L62-L84)
- grade cutoff access and conversion in [Course.java](src/models/Course.java#L163-L176)

**Design choice:**
Letter-grade thresholds are stored per course instead of globally. This allows different courses to follow different grading policies without changing the rest of the system.

### Description

`Description` stores descriptive text and assignment-weight configuration for a course or assignment. This is just a simple helper class that is used generically when some sort of text description or additional information might be needed in a class for accessibility/clearity. 

See [Description.java](src/models/Description.java#L10-L90).

It can be used for:
- title and description text
- syllabus text
- standard assignment weights
- custom assignment weights

See [Description.java](src/models/Description.java#L55-L62) and [Description.java](src/models/Description.java#L79-L90).

**Design choice:**

Having this metadata be its own object helps make other classes as simple as possible and pushes the management of complicated descriptions to another object for management instead of repeating these methods and fields across every class that might have some similar uses. 

### Student hierarchy

`Student` is the abstract base class for student records, while `UndergradStudent` and `GradStudent` provide concrete student types.

- base class: [Student.java](src/models/Student.java#L7-L95)
- undergraduate subclass: [UndergradStudent.java](src/models/UndergradStudent.java#L1-L20)
- graduate subclass: [GradStudent.java](src/models/GradStudent.java#L1-L20)

`Student` stores identity, contact information, attendance, submissions, and current/final grades. It also computes attendance rate in [Student.java](src/models/Student.java#L73-L86).

**Design choice:**
Inheritance is appropriate here because undergrad and graduate students share the same core behavior and differ mainly by type. This reduces duplication while still preserving student category information.

It also allows for later extension if a course needs to treat different student types differently, potentially grading them on separate curves, etc.

### Assignment

`Assignment` represents a graded activity in a course.

See [Assignment.java](src/models/Assignment.java#L7-L140).

It includes:
- assignment type or custom type
- total points
- due date and optional late due date
- late penalty
- description
- grade breakdown

Useful methods include the display label in [Assignment.java](src/models/Assignment.java#L104-L110) and mutators for editable fields in [Assignment.java](src/models/Assignment.java#L116-L135).

**Design choice:**
The assignment model supports both predefined types and custom types so the system can handle a variety of course structures.

### Submission

`Submission` links a student to an assignment and stores grading results.

See [Submission.java](src/models/Submission.java#L7-L95).

Key behavior includes:
- late detection
- final score computation
- final score percentage computation
- graded/ungraded state tracking

See [Submission.java](src/models/Submission.java#L50-L66).

**Design choice:**
The reason this is separate from the assignment class is that in real life there is a logical distinction between an assignment itself and each submission by a student. We wanted to keep this distinction clear in our software as well.

While the assignment itself outlines the requirements and details of the assignment, we wanted submissiosn to be individualized to the student and their own logical entities. 

## GUI structure and relationship to the model

The GUI is based on a hierarchical structure that follows the nested structure our classes follow. Because of the similarity to a file system with different levels of objects we designed the GUI to follow that pattern. It starts with the highest level organization, and gets more granular as you move into deeper layers.

The main layers can be listed below:

- semester list screen: [SemesterListFrame.java](src/ui/SemesterListFrame.java#L20-L20)
- semester detail screen: [SemesterDetailFrame.java](src/ui/SemesterDetailFrame.java#L23-L80)
- course detail screen: [CourseDetailFrame.java](src/ui/CourseDetailFrame.java#L54-L92)

The course detail screen is the main interaction point for a course. It provides sections for students, assignments, grade cutoffs, and settings. The section layout is built in [CourseDetailFrame.java](src/ui/CourseDetailFrame.java#L89-L92) and the section rows themselves are built in [CourseDetailFrame.java](src/ui/CourseDetailFrame.java#L105-L130).

### Students section

The students section lets the user add students and view student grades.

See the student dialog flow in [CourseDetailFrame.java](src/ui/CourseDetailFrame.java#L132-L260).

### Assignments section

The assignments section lets the user create and grade assignments.

See [CourseDetailFrame.java](src/ui/CourseDetailFrame.java#L262-L312).

### Grade Cutoffs section

The grade cutoff editor lets the user modify course-specific letter grade thresholds and immediately preview the resulting grade distribution.

See [CourseDetailFrame.java](src/ui/CourseDetailFrame.java#L639-L760).

### Settings section

The settings dialog lets the user edit the course’s core configuration, including dept, code, name, meeting times, building, prereqs, description, syllabus, assignment categories, and custom assignment weights.

See [CourseDetailFrame.java](src/ui/CourseDetailFrame.java#L313-L638).

### Student grades dialog

The student grades dialog shows assignment-level scores and the overall letter grade using the course’s current cutoff settings.

See [StudentGradesDialog.java](src/ui/StudentGradesDialog.java#L25-L180).

**Design choice:**
Each GUI screen is a thin layer over the model. The UI gathers input, validates it, updates the model, and then saves. That keeps business rules in the model and out of the view layer.

## Persistence design

...

**Design choice:**
...

## Data flow

... 

## Benefits of this design

### Separation of concerns
The project separates data, logic, persistence, and GUI. This makes the system easier to understand and maintain.

### Extensibility
The model already supports:
- multiple semesters
- multiple courses per semester
- standard and custom assignment categories
- custom letter grade cutoffs
- late penalties
- student-specific records and submissions

### Reusability
Because the model is separate from the GUI, the same course and student objects can be used by multiple screens and dialogs.

### Maintainability
Centralized validation in `SemesterManager` and `Course` reduces duplicated logic and helps prevent inconsistent state.

### Persistence safety
...

## Summary

The design is centered on a clear hierarchy: `SemesterManager` manages semesters, semesters contain courses, courses contain students and assignments, and submissions connect students to assignments. 

The GUI is organized around that hierarchy for simple and intuitive navigation. This structure provides a practical balance of flexibility, readability, and ease of extension.
