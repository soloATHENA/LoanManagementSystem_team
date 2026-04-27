package components;

import utils.AppColors;
import utils.AppFonts;
import animations.HoverAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Styled button matching Figma design - rounded with gradient feel.
 */
public class StyledButton extends JButton {
    private Color bgColor;
    private Color hoverColor;
    private Color pressColor;
    private int radius = 12;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public StyledButton(String text) {
        this(text, AppColors.PRIMARY_BLUE, AppColors.PRIMARY_BLUE_HOVER);
    }

    public StyledButton(String text, Color bg, Color hover) {
        super(text);
        this.bgColor = bg;
        this.hoverColor = hover;
        this.pressColor = hover.darker();

        setFont(AppFonts.BUTTON);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(200, 46));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color current = isPressed ? pressColor : (isHovered ? hoverColor : bgColor);
        g2.setColor(current);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

        g2.dispose();
        super.paintComponent(g);
    }
}
