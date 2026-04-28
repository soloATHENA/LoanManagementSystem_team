package components;

import utils.AppColors;
import utils.AppFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Status badge pill (Approved/Pending/Rejected/Active/Under Review).
 */
public class StatusBadge extends JLabel {
    private Color badgeBg;
    private Color badgeFg;

    public StatusBadge(String status) {
        super(status, SwingConstants.CENTER);
        setFont(AppFonts.SMALL_BOLD);
        setPreferredSize(new Dimension(100, 26));
        setOpaque(false);

        switch (status.toLowerCase()) {
            case "approved":
            case "active":
                badgeBg = AppColors.STATUS_GREEN_BG;
                badgeFg = new Color(22, 163, 74);
                break;
            case "pending":
            case "under review":
                badgeBg = AppColors.STATUS_AMBER_BG;
                badgeFg = new Color(180, 120, 0);
                break;
            case "rejected":
                badgeBg = AppColors.STATUS_RED_BG;
                badgeFg = AppColors.STATUS_RED;
                break;
            default:
                badgeBg = AppColors.PRIMARY_BLUE_LIGHT;
                badgeFg = AppColors.PRIMARY_BLUE;
                break;
        }
        setForeground(badgeFg);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(badgeBg);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        g2.dispose();
        super.paintComponent(g);
    }
}
