package screens.customer;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import animations.HoverAnimator;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerDashboard extends JPanel {
    private int userId;
    private String userName;

    // Data fetched from database
    private int totalLoans = 0;
    private int activeLoans = 0;
    private double totalBalance = 0.0;
    
    private List<Object[]> recentLoans = new ArrayList<>();
    private List<Object[]> recentActivities = new ArrayList<>();

    public CustomerDashboard(int userId, String name) {
        this.userId = userId;
        this.userName = name;
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        loadDataFromDatabase();
        buildUI();
    }

    private void loadDataFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            
            // 1. Fetch Stats for this specific user
            String statsSql = "SELECT status, amount FROM Loans WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(statsSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    totalLoans++;
                    if ("Approved".equals(rs.getString("status"))) {
                        activeLoans++;
                        totalBalance += rs.getDouble("amount");
                    }
                }
            }

            // 2. Fetch Recent Loans
            String loansSql = "SELECT loan_type, amount, status FROM Loans WHERE user_id = ? ORDER BY application_date DESC LIMIT 3";
            try (PreparedStatement stmt = conn.prepareStatement(loansSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    recentLoans.add(new Object[]{
                        rs.getString("loan_type"),
                        rs.getString("amount"),
                        rs.getString("status")
                    });
                }
            }

            // 3. Fetch Activity Log
            String actSql = "SELECT activity_text, activity_date, status_color FROM ActivityLog WHERE user_id = ? ORDER BY activity_date DESC LIMIT 4";
            try (PreparedStatement stmt = conn.prepareStatement(actSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    recentActivities.add(new Object[]{
                        rs.getString("activity_text"),
                        rs.getString("activity_date"),
                        rs.getString("status_color")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Dashboard");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        // Dynamic Name!
        JLabel subtitle = new JLabel("Welcome back, " + userName + "! Here's your loan overview.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(25));

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.add(createStatCard("Total Loans", String.valueOf(totalLoans), AppColors.BORDER_BLUE, "\u2610"));
        statsRow.add(createStatCard("Active Loans", String.valueOf(activeLoans), AppColors.BORDER_GREEN, "\u2191"));
        statsRow.add(createStatCard("Total Approved Amount", "₹" + String.format("%.2f", totalBalance), AppColors.BORDER_AMBER, "$"));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(25));

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setOpaque(false);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        bottomRow.add(createLoanStatusPanel());
        bottomRow.add(createRecentActivityPanel());

        content.add(bottomRow);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private RoundedPanel createStatCard(String label, String value, Color borderColor, String icon) {
        RoundedPanel card = new RoundedPanel(14, borderColor);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppFonts.SMALL);
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        left.add(lbl);
        left.add(Box.createVerticalStrut(8));

        JLabel val = new JLabel(value);
        val.setFont(AppFonts.CARD_VALUE);
        val.setForeground(AppColors.TEXT_PRIMARY);
        left.add(val);

        card.add(left, BorderLayout.CENTER);

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconLabel.setForeground(borderColor);
        card.add(iconLabel, BorderLayout.EAST);

        HoverAnimator.addHoverEffect(card);
        return card;
    }

    private RoundedPanel createLoanStatusPanel() {
        RoundedPanel panel = new RoundedPanel(14);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Your Loans");
        title.setFont(AppFonts.HEADING);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));

        if (recentLoans.isEmpty()) {
            JLabel noLoans = new JLabel("You haven't applied for any loans yet.");
            noLoans.setForeground(AppColors.TEXT_SECONDARY);
            panel.add(noLoans);
        } else {
            for (Object[] loan : recentLoans) {
                String type = (String) loan[0];
                String amount = "₹" + loan[1];
                String status = (String) loan[2];
                panel.add(createLoanEntry(type, amount, status));
                panel.add(Box.createVerticalStrut(10));
            }
        }
        return panel;
    }

    private JPanel createLoanEntry(String name, String amount, String status) {
        RoundedPanel entry = new RoundedPanel(10);
        entry.setBackground("Approved".equals(status) ? AppColors.STATUS_GREEN_BG : ("Rejected".equals(status) ? AppColors.STATUS_RED_BG : AppColors.STATUS_AMBER_BG));
        entry.setLayout(new BorderLayout());
        entry.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        entry.setAlignmentX(Component.LEFT_ALIGNMENT);
        entry.setHasShadow(false);
        entry.setBorderColor(null);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(AppFonts.BODY_BOLD);
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        left.add(nameLabel);
        left.add(Box.createHorizontalStrut(5));
        JLabel amtLabel = new JLabel(amount);
        amtLabel.setFont(AppFonts.SMALL);
        amtLabel.setForeground(AppColors.TEXT_SECONDARY);
        left.add(amtLabel);
        entry.add(left, BorderLayout.WEST);

        StatusBadge badge = new StatusBadge(status);
        entry.add(badge, BorderLayout.EAST);

        return entry;
    }

    private RoundedPanel createRecentActivityPanel() {
        RoundedPanel panel = new RoundedPanel(14);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Recent Activity");
        title.setFont(AppFonts.HEADING);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));

        if (recentActivities.isEmpty()) {
            JLabel noActs = new JLabel("No recent activity.");
            noActs.setForeground(AppColors.TEXT_SECONDARY);
            panel.add(noActs);
        } else {
            for (Object[] act : recentActivities) {
                String text = (String) act[0];
                String date = (String) act[1];
                String colorStr = (String) act[2];
                Color dotColor = colorStr.equals("GREEN") ? AppColors.STATUS_GREEN : (colorStr.equals("RED") ? AppColors.STATUS_RED : AppColors.STATUS_AMBER);
                
                panel.add(createActivityItem("\u2022", text, date, dotColor));
                panel.add(Box.createVerticalStrut(12));
            }
        }
        return panel;
    }

    private JPanel createActivityItem(String icon, String text, String date, Color dotColor) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(2, 2, 8, 8);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(12, 12));
        leftPanel.add(dot);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(AppFonts.BODY_BOLD);
        textLabel.setForeground(AppColors.TEXT_PRIMARY);
        textPanel.add(textLabel);

        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(AppFonts.SMALL);
        dateLabel.setForeground(AppColors.TEXT_SECONDARY);
        textPanel.add(dateLabel);

        leftPanel.add(textPanel);
        item.add(leftPanel, BorderLayout.WEST);

        return item;
    }
}
