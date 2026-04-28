package animations;

import javax.swing.*;
import java.awt.*;

/**
 * Slide-in animation utility.
 * Slides a component from an offset position to its target position.
 */
public class SlideAnimator {

    public enum Direction { LEFT, RIGHT, TOP, BOTTOM }

    /**
     * Slide a component into view.
     * @param component  The component to animate
     * @param direction  The direction to slide from
     * @param distance   Pixel distance to slide
     * @param durationMs Duration of the animation
     * @param onComplete Optional callback
     */
    public static void slideIn(JComponent component, Direction direction, int distance, int durationMs, Runnable onComplete) {
        Point target = component.getLocation();
        Point start = new Point(target);

        switch (direction) {
            case LEFT:   start.x -= distance; break;
            case RIGHT:  start.x += distance; break;
            case TOP:    start.y -= distance; break;
            case BOTTOM: start.y += distance; break;
        }

        component.setLocation(start);
        component.setVisible(true);

        final int steps = 25;
        final int delay = durationMs / steps;
        final double dx = (target.x - start.x) / (double) steps;
        final double dy = (target.y - start.y) / (double) steps;
        final double[] currentX = {start.x};
        final double[] currentY = {start.y};
        final int[] step = {0};

        Timer timer = new Timer(delay, null);
        timer.addActionListener(e -> {
            step[0]++;
            // Ease-out cubic
            double t = (double) step[0] / steps;
            double ease = 1 - Math.pow(1 - t, 3);

            int newX = start.x + (int) ((target.x - start.x) * ease);
            int newY = start.y + (int) ((target.y - start.y) * ease);
            component.setLocation(newX, newY);

            if (step[0] >= steps) {
                component.setLocation(target);
                timer.stop();
                if (onComplete != null) onComplete.run();
            }
        });
        timer.start();
    }
}
