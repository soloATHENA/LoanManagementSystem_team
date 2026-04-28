package screens.employee;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import animations.HoverAnimator;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDashboard extends JPanel {
    private int employeeId;
    private String employeeName;

    // Data variables
    private int totalApps = 0;
    private int approvedApps = 0;
    private int rejectedApps = 0;
    private int pendingApps = 0;
    private Object[][] tableData = new Object[0][6];
    
    // Dynamic Chart Data
    private Map<String, Integer> pieData = new HashMap<>();
    private List<String> barMonths = new ArrayList<>();
    private List<Integer> barValues = new ArrayList<>();

    public EmployeeDashboard(int employeeId, String name) {
        this.employeeId = employeeId;
        this.employeeName = name;
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        loadDataFromDatabase();
        buildUI();
    }

    private void loadDataFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            
            // 1. Get stats
            String statsSql = "SELECT status, COUNT(*) as count FROM Loans GROUP BY status";
            try (PreparedStatement stmt = conn.prepareStatement(statsSql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count");
                    totalApps += count;
                    if (status.equals("Approved")) approvedApps = count;
                    else if (status.equals("Rejected")) rejectedApps = count;
                    else if (status.equals("Pending")) pendingApps = count;
                }
            }

            // 2. Fetch Pie Chart Data
            String pieSql = "SELECT loan_type, COUNT(*) as count FROM Loans GROUP BY loan_type";
            try (PreparedStatement stmt = conn.prepareStatement(pieSql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    pieData.put(rs.getString("loan_type"), rs.getInt("count"));
                }
            }

            // 3. Fetch Bar Chart Data
            String barSql = "SELECT MONTHNAME(application_date) as m_name, COUNT(*) as count " +
                            "FROM Loans GROUP BY MONTH(application_date), MONTHNAME(application_date) " +
                            "ORDER BY MONTH(application_date) LIMIT 6";
            try (PreparedStatement stmt = conn.prepareStatement(barSql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    barMonths.add(rs.getString("m_name").substring(0, 3)); // Get "Jan", "Feb", etc.
                    barValues.add(rs.getInt("count"));
                }
            }

            // 4. Get recent applications
            String appsSql = "SELECT L.loan_id, U.name as customer, L.loan_type, L.amount, L.status, L.application_date " +
                             "FROM Loans L JOIN Users U ON L.user_id = U.user_id ORDER BY L.application_date DESC LIMIT 5";
            try (PreparedStatement stmt = conn.prepareStatement(appsSql)) {
                ResultSet rs = stmt.executeQuery();
                List<Object[]> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("loan_id"),
                        rs.getString("customer"),
                        rs.getString("loan_type"),
                        "₹" + rs.getString("amount"),
                        rs.getString("status"),
                        rs.getString("application_date")
                    });
                }
                tableData = list.toArray(new Object[0][0]);
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

        JLabel subtitle = new JLabel("Welcome back, " + employeeName + "! Here's your overview.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(25));

        // Stats cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.add(createStatCard("Total Applications", String.valueOf(totalApps), AppColors.BORDER_BLUE, "\u2610"));
        statsRow.add(createStatCard("Approved Loans", String.valueOf(approvedApps), AppColors.BORDER_GREEN, "\u2713"));
        statsRow.add(createStatCard("Rejected Loans", String.valueOf(rejectedApps), AppColors.STATUS_RED, "\u2717"));
        statsRow.add(createStatCard("Pending", String.valueOf(pendingApps), AppColors.BORDER_AMBER, "\u231B"));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(25));

        // Charts row
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsRow.setOpaque(false);
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        chartsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        chartsRow.add(createBarChartPanel());
        chartsRow.add(createPieChartPanel());

        content.add(chartsRow);
        content.add(Box.createVerticalStrut(25));

        // Recent applications table
        content.add(createRecentApplicationsPanel());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private RoundedPanel createStatCard(String label, String value, Color borderColor, String icon) {
        RoundedPanel card = new RoundedPanel(14, borderColor);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

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

    private RoundedPanel createBarChartPanel() {
        RoundedPanel panel = new RoundedPanel(14);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Monthly Applications");
        title.setFont(AppFonts.HEADING);
        title.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (barValues.isEmpty()) return;

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight() - 40;
                int maxVal = barValues.stream().max(Integer::compare).orElse(1);
                if (maxVal == 0) maxVal = 1;

                int sectionWidth = (w - 60) / barMonths.size();
                int barWidth = Math.min(40, sectionWidth - 10);

                for (int i = 0; i < barMonths.size(); i++) {
                    int barHeight = (int) ((barValues.get(i) / (double) maxVal) * (h - 20));
                    int x = 30 + (i * sectionWidth) + (sectionWidth - barWidth) / 2;
                    int y = h - barHeight;

                    // Bar Gradient
                    GradientPaint gp = new GradientPaint(x, y, AppColors.PRIMARY_BLUE, x, y + barHeight, new Color(96, 165, 250));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 6, 6));

                    // Number on top of bar
                    g2.setColor(AppColors.TEXT_PRIMARY);
                    g2.setFont(AppFonts.TINY);
                    String valStr = String.valueOf(barValues.get(i));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(valStr, x + barWidth / 2 - fm.stringWidth(valStr) / 2, y - 5);

                    // Month label below bar
                    g2.setColor(AppColors.TEXT_SECONDARY);
                    g2.drawString(barMonths.get(i), x + barWidth / 2 - fm.stringWidth(barMonths.get(i)) / 2, h + 15);
                }
                g2.dispose();
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(300, 220));
        panel.add(chart, BorderLayout.CENTER);

        return panel;
    }

    private RoundedPanel createPieChartPanel() {
        RoundedPanel panel = new RoundedPanel(14);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Loan Distribution");
        title.setFont(AppFonts.HEADING);
        title.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int r = Math.min(cx, cy) - 30;

                int total = pieData.values().stream().mapToInt(Integer::intValue).sum();
                if (total == 0) {
                    g2.setColor(AppColors.TEXT_SECONDARY);
                    g2.drawString("No data available", cx - 40, cy);
                    return;
                }

                Color[] colors = {AppColors.PRIMARY_BLUE, AppColors.STATUS_GREEN, AppColors.CHART_ORANGE, AppColors.STATUS_AMBER, new Color(139, 92, 246)};
                int startAngle = 0;
                int i = 0;

                // Draw slices
                for (Map.Entry<String, Integer> entry : pieData.entrySet()) {
                    int angle = (int) Math.round((entry.getValue() / (double) total) * 360.0);
                    g2.setColor(colors[i % colors.length]);
                    g2.fillArc(cx - r, cy - r, r * 2, r * 2, startAngle, angle);
                    startAngle += angle;
                    i++;
                }

                // Inner white circle (Donut shape)
                g2.setColor(Color.WHITE);
                g2.fillOval(cx - r / 2, cy - r / 2, r, r);

                // Draw legend at bottom
                g2.setFont(AppFonts.TINY);
                int legendX = 10;
                int legendY = getHeight() - 15;
                i = 0;
                for (String label : pieData.keySet()) {
                    g2.setColor(colors[i % colors.length]);
                    g2.fillOval(legendX, legendY - 8, 8, 8);
                    g2.setColor(AppColors.TEXT_SECONDARY);
                    String txt = label + " (" + pieData.get(label) + ")";
                    g2.drawString(txt, legendX + 12, legendY);
                    legendX += g2.getFontMetrics().stringWidth(txt) + 25; // Space out properly
                    i++;
                }
                g2.dispose();
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(300, 220));
        panel.add(chart, BorderLayout.CENTER);

        return panel;
    }

    private RoundedPanel createRecentApplicationsPanel() {
        RoundedPanel panel = new RoundedPanel(14);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Recent Applications");
        title.setFont(AppFonts.HEADING);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Loan ID", "Customer", "Type", "Amount", "Status", "Date"};
        
        JTable table = new JTable(tableData, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        // Premium Table Styling
        table.setFont(AppFonts.BODY);
        table.setRowHeight(40);
        table.setGridColor(new Color(241, 245, 249)); // very light gray grid
        table.setShowGrid(true);
        table.getTableHeader().setFont(AppFonts.BODY_BOLD);
        table.getTableHeader().setBackground(AppColors.BG_INPUT);
        table.getTableHeader().setForeground(AppColors.TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER));
        table.setSelectionBackground(AppColors.PRIMARY_BLUE_LIGHT);
        
        // Fix the ugly gray background at the bottom of the table
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240))); // Subtle border
        tableScroll.getViewport().setBackground(Color.WHITE); // Make unused space white!
        tableScroll.setPreferredSize(new Dimension(0, 240));

        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }
}
