package animations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Provides fade-in animation for panels.
 * Gradually increases opacity of a component from 0 to 1.
 */
public class FadeAnimator {

    /**
     * Fade-in a JPanel from transparent to fully opaque.
     * @param panel  The panel to animate (must be non-opaque during animation)
     * @param durationMs Total duration in milliseconds
     * @param onComplete Optional callback when animation completes
     */
    public static void fadeIn(JPanel panel, int durationMs, Runnable onComplete) {
        panel.setVisible(true);
        final float[] alpha = {0f};
        final int steps = 20;
        final int delay = durationMs / steps;
        final float increment = 1.0f / steps;

        Timer timer = new Timer(delay, null);
        timer.addActionListener(e -> {
            alpha[0] += increment;
            if (alpha[0] >= 1.0f) {
                alpha[0] = 1.0f;
                timer.stop();
                panel.repaint();
                if (onComplete != null) onComplete.run();
            }
            // Use AlphaComposite in paint
            panel.putClientProperty("fadeAlpha", alpha[0]);
            panel.repaint();
        });
        panel.putClientProperty("fadeAlpha", 0f);
        timer.start();
    }

    /**
     * Fade-out a JPanel from opaque to transparent.
     */
    public static void fadeOut(JPanel panel, int durationMs, Runnable onComplete) {
        final float[] alpha = {1f};
        final int steps = 20;
        final int delay = durationMs / steps;
        final float decrement = 1.0f / steps;

        Timer timer = new Timer(delay, null);
        timer.addActionListener(e -> {
            alpha[0] -= decrement;
            if (alpha[0] <= 0f) {
                alpha[0] = 0f;
                timer.stop();
                panel.setVisible(false);
                if (onComplete != null) onComplete.run();
            }
            panel.putClientProperty("fadeAlpha", alpha[0]);
            panel.repaint();
        });
        panel.putClientProperty("fadeAlpha", 1f);
        timer.start();
    }
}
