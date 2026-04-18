package interfaces;
import enums.*;

public interface Gradeable {
    public void grade();
    public boolean isGraded();
    public GradingStatus getGradingStatus();
}
