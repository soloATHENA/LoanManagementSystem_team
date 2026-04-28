package components;

import utils.AppColors;
import utils.AppFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Styled text field with rounded corners and placeholder text.
 */
public class StyledTextField extends JTextField {
    private String placeholder;
    private int radius = 12;

    public StyledTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(AppFonts.INPUT);
        setForeground(AppColors.TEXT_PRIMARY);
        setBackground(AppColors.BG_INPUT);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        setPreferredSize(new Dimension(300, 46));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));

        g2.setColor(AppColors.BORDER);
        g2.setStroke(new BasicStroke(1));
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));

        g2.dispose();
        super.paintComponent(g);

        // Draw placeholder
        if (getText().isEmpty() && !hasFocus()) {
            Graphics2D g2p = (Graphics2D) g.create();
            g2p.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2p.setColor(AppColors.TEXT_MUTED);
            g2p.setFont(getFont());
            Insets insets = getInsets();
            g2p.drawString(placeholder, insets.left, getHeight() / 2 + g2p.getFontMetrics().getAscent() / 2 - 2);
            g2p.dispose();
        }
    }
}
