package models;

import enums.StudentStatus;

public class UndergradStudent extends Student{
    // Initializes an undergraduate student with given information
    public UndergradStudent(int id, String name, Date dob, String email, int gradYear, StudentStatus status) {
        super(id, name, dob, email, gradYear, status);
    }
}
