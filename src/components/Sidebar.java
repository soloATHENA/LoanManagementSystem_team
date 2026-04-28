package components;

import utils.AppColors;
import utils.AppFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Dark sidebar navigation matching the Figma design.
 * Supports Customer and Employee menu items.
 */
public class Sidebar extends JPanel {
    private String portalType; // "Customer Portal" or "Admin Panel"
    private List<MenuItem> menuItems = new ArrayList<>();
    private int activeIndex = 0;
    private Runnable[] actions;
    private String userName;
    private String userEmail;
    private Runnable logoutAction;

    public static class MenuItem {
        String icon;
        String label;
        JPanel itemPanel;

        public MenuItem(String icon, String label) {
            this.icon = icon;
            this.label = label;
        }
    }

    public Sidebar(String portalType, String[] icons, String[] labels, Runnable[] actions,
                   String userName, String userEmail, Runnable logoutAction) {
        this.portalType = portalType;
        this.actions = actions;
        this.userName = userName;
        this.userEmail = userEmail;
        this.logoutAction = logoutAction;

        setBackground(AppColors.SIDEBAR_DARK);
        setPreferredSize(new Dimension(240, 0));
        setLayout(new BorderLayout());

        for (int i = 0; i < labels.length; i++) {
            menuItems.add(new MenuItem(icons[i], labels[i]));
        }

        buildSidebar();
    }

    private void buildSidebar() {
        removeAll();

        // Top section: Logo
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Logo area
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setOpaque(false);

        // Logo icon (blue rounded square with "L")
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.PRIMARY_BLUE);
                g2.fill(new RoundRectangle2D.Double(0, 0, 36, 36, 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.drawString("L", 12, 26);
                g2.dispose();
            }
        };
        logoIcon.setOpaque(false);
        logoIcon.setPreferredSize(new Dimension(36, 36));
        logoPanel.add(logoIcon);

        JPanel logoTextPanel = new JPanel();
        logoTextPanel.setLayout(new BoxLayout(logoTextPanel, BoxLayout.Y_AXIS));
        logoTextPanel.setOpaque(false);

        JLabel appName = new JLabel("LoanHub");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        logoTextPanel.add(appName);

        JLabel portalLabel = new JLabel(portalType);
        portalLabel.setFont(AppFonts.TINY);
        portalLabel.setForeground(AppColors.SIDEBAR_TEXT);
        logoTextPanel.add(portalLabel);

        logoPanel.add(logoTextPanel);
        topPanel.add(logoPanel);
        topPanel.add(Box.createVerticalStrut(25));

        // MENU label
        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setFont(AppFonts.MENU_LABEL);
        menuLabel.setForeground(AppColors.SIDEBAR_TEXT);
        menuLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 0));
        topPanel.add(menuLabel);

        // Menu items
        for (int i = 0; i < menuItems.size(); i++) {
            topPanel.add(createMenuItemPanel(i));
            topPanel.add(Box.createVerticalStrut(4));
        }

        add(topPanel, BorderLayout.NORTH);

        // Bottom section: user info + logout
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        userPanel.setOpaque(false);

        // User avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(55, 65, 81));
                g2.fillOval(0, 0, 36, 36);
                g2.setColor(AppColors.SIDEBAR_TEXT);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String initials = userName.substring(0, 1);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, (36 - fm.stringWidth(initials)) / 2, (36 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(36, 36));
        userPanel.add(avatar);

        JPanel userTextPanel = new JPanel();
        userTextPanel.setLayout(new BoxLayout(userTextPanel, BoxLayout.Y_AXIS));
        userTextPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(AppFonts.BODY_BOLD);
        nameLabel.setForeground(Color.WHITE);
        userTextPanel.add(nameLabel);

        JLabel emailLabel = new JLabel(userEmail);
        emailLabel.setFont(AppFonts.TINY);
        emailLabel.setForeground(AppColors.SIDEBAR_TEXT);
        userTextPanel.add(emailLabel);

        userPanel.add(userTextPanel);
        bottomPanel.add(userPanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        // Logout button
        JPanel logoutPanel = createLogoutPanel();
        bottomPanel.add(logoutPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel createMenuItemPanel(int index) {
        MenuItem item = menuItems.get(index);
        boolean isActive = (index == activeIndex);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (index == activeIndex) {
                    g2.setColor(AppColors.PRIMARY_BLUE);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(200, 42));
        panel.setMaximumSize(new Dimension(200, 42));

        // Icon placeholder
        JLabel iconLabel = new JLabel(item.icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setForeground(isActive ? Color.WHITE : AppColors.SIDEBAR_TEXT);
        panel.add(iconLabel);

        JLabel label = new JLabel(item.label);
        label.setFont(AppFonts.MENU_ITEM);
        label.setForeground(isActive ? Color.WHITE : AppColors.SIDEBAR_TEXT);
        panel.add(label);

        item.itemPanel = panel;

        final int idx = index;
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveIndex(idx);
                if (actions != null && idx < actions.length) {
                    actions[idx].run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (idx != activeIndex) {
                    panel.setBackground(new Color(30, 41, 59));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (idx != activeIndex) {
                    panel.setBackground(AppColors.SIDEBAR_DARK);
                }
            }
        });

        return panel;
    }

    private JPanel createLogoutPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(200, 42));
        panel.setMaximumSize(new Dimension(200, 42));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel("\u21B6");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        icon.setForeground(AppColors.STATUS_RED);
        panel.add(icon);

        JLabel label = new JLabel("Logout");
        label.setFont(AppFonts.MENU_ITEM);
        label.setForeground(AppColors.STATUS_RED);
        panel.add(label);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (logoutAction != null) logoutAction.run();
            }
        });

        return panel;
    }

    public void setActiveIndex(int index) {
        this.activeIndex = index;
        buildSidebar();
    }

    public int getActiveIndex() {
        return activeIndex;
    }
}
