package components;

import utils.AppColors;
import utils.AppFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Rounded card panel with optional colored top border.
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color borderTopColor;
    private Color borderColor;
    private boolean hasShadow;

    public RoundedPanel(int cornerRadius) {
        this(cornerRadius, null);
    }

    public RoundedPanel(int cornerRadius, Color borderTopColor) {
        this.cornerRadius = cornerRadius;
        this.borderTopColor = borderTopColor;
        this.borderColor = AppColors.BORDER;
        this.hasShadow = true;
        setOpaque(false);
        setBackground(AppColors.BG_CARD);
    }

    public void setBorderTopColor(Color c) {
        this.borderTopColor = c;
        repaint();
    }

    public void setBorderColor(Color c) {
        this.borderColor = c;
        repaint();
    }

    public void setHasShadow(boolean s) {
        this.hasShadow = s;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Check for fade animation alpha
        Float fadeAlpha = (Float) getClientProperty("fadeAlpha");
        if (fadeAlpha != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
        }

        int w = getWidth();
        int h = getHeight();

        // Shadow
        if (hasShadow) {
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fill(new RoundRectangle2D.Double(2, 3, w - 4, h - 4, cornerRadius, cornerRadius));
        }

        // Background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));

        // Border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));
        }

        // Top colored border accent
        if (borderTopColor != null) {
            g2.setColor(borderTopColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(cornerRadius / 2, 1, w - cornerRadius / 2, 1);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
