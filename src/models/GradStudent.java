package models;
import enums.*;

public class GradStudent extends Student {
    public GradStudent(int id, String name, Date dob, String email, int gradYear, StudentStatus status) {
        super(id, name, dob, email, gradYear, status);
    }
}
