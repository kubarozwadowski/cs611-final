package models;
import enums.*;

public class GradStudent extends Student {
    // Initializes a graduate student with given information
    public GradStudent(int id, String name, Date dob, String email, int gradYear, StudentStatus status) {
        super(id, name, dob, email, gradYear, status);
    }
}
