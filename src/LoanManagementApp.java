import screens.MainFrame;
import javax.swing.*;

/**
 * Entry point for the LoanHub - Loan Management System.
 * Login credentials: admin / 1234
 */
public class LoanManagementApp {
    public static void main(String[] args) {
        // Use system look and feel for better native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Improve font rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
