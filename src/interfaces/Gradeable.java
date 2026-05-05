package interfaces;
import enums.*;

public interface Gradeable {
    // Marks this object as graded
    public void grade();
    
    // Returns true if the object has been graded
    public boolean isGraded();
    
    // Returns the current grading status
    public GradingStatus getGradingStatus();
}
