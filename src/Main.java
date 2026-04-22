import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import logic.SemesterManager;
import ui.SemesterListFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();

            SemesterManager semesterManager = new SemesterManager();
            SemesterListFrame frame = new SemesterListFrame(semesterManager);
            frame.setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            // If the platform look and feel fails, Swing will use its default.
        }
    }
}
