package animations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Adds hover scale/shadow effects to components.
 */
public class HoverAnimator {

    /**
     * Add a hover lift effect (shadow deepens, slight scale feel via border).
     */
    public static void addHoverEffect(JPanel panel) {
        Color normalBorder = panel.getBorder() != null ? null : null;

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(37, 99, 235, 40), 1, true),
                    BorderFactory.createEmptyBorder(14, 19, 14, 19)
                ));
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                    BorderFactory.createEmptyBorder(14, 19, 14, 19)
                ));
                panel.setCursor(Cursor.getDefaultCursor());
                panel.repaint();
            }
        });
    }

    /**
     * Add a button press animation effect.
     */
    public static void addButtonPressEffect(JButton button) {
        Color originalBg = button.getBackground();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(originalBg.darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
    }
}
