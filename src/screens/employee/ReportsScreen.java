package screens.employee;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportsScreen extends JPanel {
    
    // Analytics Data
    private double totalDisbursedAmt = 0;
    private int approvalRate = 0;
    private double avgLoanAmt = 0;
    
    // Chart Data
    private List<String> monthsList = new ArrayList<>();
    private List<Integer> approvedList = new ArrayList<>();
    private List<Integer> rejectedList = new ArrayList<>();

    public ReportsScreen() {
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        loadDataFromDatabase();
        buildUI();
    }

    private void loadDataFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            
            // 1. Fetch Summary Stats
            String statsSql = "SELECT status, amount FROM Loans";
            try (PreparedStatement stmt = conn.prepareStatement(statsSql)) {
                ResultSet rs = stmt.executeQuery();
                int totalLoans = 0;
                int approvedCount = 0;
                
                while (rs.next()) {
                    totalLoans++;
                    if ("Approved".equals(rs.getString("status"))) {
                        approvedCount++;
                        totalDisbursedAmt += rs.getDouble("amount");
                    }
                }
                if (totalLoans > 0) {
                    approvalRate = (approvedCount * 100) / totalLoans;
                    avgLoanAmt = approvedCount > 0 ? totalDisbursedAmt / approvedCount : 0;
                }
            }

            // 2. Fetch Trend Data (Group by Month)
            String trendSql = "SELECT MONTHNAME(application_date) as m_name, status, COUNT(*) as count " +
                              "FROM Loans GROUP BY MONTH(application_date), MONTHNAME(application_date), status " +
                              "ORDER BY MONTH(application_date) LIMIT 12";
            try (PreparedStatement stmt = conn.prepareStatement(trendSql)) {
                ResultSet rs = stmt.executeQuery();
                
                Map<String, int[]> monthData = new LinkedHashMap<>();
                while (rs.next()) {
                    String month = rs.getString("m_name").substring(0, 3); // "Jan", "Feb"
                    String status = rs.getString("status");
                    int count = rs.getInt("count");
                    
                    monthData.putIfAbsent(month, new int[]{0, 0}); // Index 0: Approved, Index 1: Rejected
                    if ("Approved".equals(status)) {
                        monthData.get(month)[0] = count;
                    } else if ("Rejected".equals(status)) {
                        monthData.get(month)[1] = count;
                    }
                }
                
                for (Map.Entry<String, int[]> entry : monthData.entrySet()) {
                    monthsList.add(entry.getKey());
                    approvedList.add(entry.getValue()[0]);
                    rejectedList.add(entry.getValue()[1]);
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

        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("View live reports and analytics for all database operations.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(25));

        // Summary cards
        JPanel summaryRow = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryRow.setOpaque(false);
        summaryRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        summaryRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryRow.add(createSummaryCard("Total Disbursed", "₹" + String.format("%.0f", totalDisbursedAmt), AppColors.PRIMARY_BLUE));
        summaryRow.add(createSummaryCard("Overall Approval Rate", approvalRate + "%", AppColors.STATUS_GREEN));
        summaryRow.add(createSummaryCard("Avg. Approved Loan Amount", "₹" + String.format("%.0f", avgLoanAmt), AppColors.CHART_ORANGE));

        content.add(summaryRow);
        content.add(Box.createVerticalStrut(25));

        // Trend chart
        RoundedPanel trendPanel = new RoundedPanel(14);
        trendPanel.setLayout(new BorderLayout());
        trendPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        trendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        trendPanel.setPreferredSize(new Dimension(0, 280));
        trendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        JLabel trendTitle = new JLabel("Loan Approval Trend (Historical)");
        trendTitle.setFont(AppFonts.HEADING);
        trendTitle.setForeground(AppColors.TEXT_PRIMARY);
        trendPanel.add(trendTitle, BorderLayout.NORTH);

        JPanel lineChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight() - 40;
                
                if (monthsList.isEmpty()) {
                    g2.setColor(AppColors.TEXT_SECONDARY);
                    g2.drawString("Not enough data to display chart.", w/2 - 80, h/2);
                    return;
                }

                int[] approved = approvedList.stream().mapToInt(i->i).toArray();
                int[] rejected = rejectedList.stream().mapToInt(i->i).toArray();
                String[] months = monthsList.toArray(new String[0]);

                int maxVal = 0;
                for(int v : approved) if(v > maxVal) maxVal = v;
                for(int v : rejected) if(v > maxVal) maxVal = v;
                if (maxVal == 0) maxVal = 1;

                int padding = 40;

                // Grid lines
                g2.setColor(new Color(240, 240, 245));
                g2.setStroke(new BasicStroke(1));
                for (int i = 0; i <= maxVal; i++) {
                    int y = h - (int) ((double) i / maxVal * (h - padding));
                    g2.drawLine(padding, y, w - 10, y);
                }

                // If only 1 month of data exists, draw points instead of lines
                if (months.length == 1) {
                    int x = w/2;
                    int appY = h - (int) ((double) approved[0] / maxVal * (h - padding));
                    int rejY = h - (int) ((double) rejected[0] / maxVal * (h - padding));
                    g2.setColor(AppColors.STATUS_GREEN);
                    g2.fillOval(x - 4, appY - 4, 8, 8);
                    g2.setColor(AppColors.STATUS_RED);
                    g2.fillOval(x - 4, rejY - 4, 8, 8);
                    g2.setColor(AppColors.TEXT_SECONDARY);
                    g2.drawString(months[0], x - 10, h + 20);
                } else {
                    // Draw approved line
                    g2.setColor(AppColors.STATUS_GREEN);
                    g2.setStroke(new BasicStroke(3));
                    int[] approvedX = new int[months.length];
                    int[] approvedY = new int[months.length];
                    for (int i = 0; i < months.length; i++) {
                        approvedX[i] = padding + i * ((w - padding - 10) / (months.length - 1));
                        approvedY[i] = h - (int) ((double) approved[i] / maxVal * (h - padding));
                    }
                    for (int i = 0; i < months.length - 1; i++) {
                        g2.drawLine(approvedX[i], approvedY[i], approvedX[i + 1], approvedY[i + 1]);
                    }
                    for (int i = 0; i < months.length; i++) g2.fillOval(approvedX[i] - 4, approvedY[i] - 4, 8, 8);

                    // Draw rejected line
                    g2.setColor(AppColors.STATUS_RED);
                    int[] rejectedX = new int[months.length];
                    int[] rejectedY = new int[months.length];
                    for (int i = 0; i < months.length; i++) {
                        rejectedX[i] = approvedX[i];
                        rejectedY[i] = h - (int) ((double) rejected[i] / maxVal * (h - padding));
                    }
                    for (int i = 0; i < months.length - 1; i++) {
                        g2.drawLine(rejectedX[i], rejectedY[i], rejectedX[i + 1], rejectedY[i + 1]);
                    }
                    for (int i = 0; i < months.length; i++) g2.fillOval(rejectedX[i] - 4, rejectedY[i] - 4, 8, 8);

                    // Month labels
                    g2.setColor(AppColors.TEXT_SECONDARY);
                    g2.setFont(AppFonts.TINY);
                    FontMetrics fm = g2.getFontMetrics();
                    for (int i = 0; i < months.length; i++) {
                        g2.drawString(months[i], approvedX[i] - fm.stringWidth(months[i]) / 2, h + 20);
                    }
                }

                // Legend
                int ly = h + 35;
                g2.setColor(AppColors.STATUS_GREEN);
                g2.fillOval(w / 2 - 80, ly - 8, 8, 8);
                g2.setColor(AppColors.TEXT_SECONDARY);
                g2.drawString("Approved", w / 2 - 68, ly);

                g2.setColor(AppColors.STATUS_RED);
                g2.fillOval(w / 2 + 10, ly - 8, 8, 8);
                g2.setColor(AppColors.TEXT_SECONDARY);
                g2.drawString("Rejected", w / 2 + 22, ly);

                g2.dispose();
            }
        };
        
        lineChart.setOpaque(false);
        trendPanel.add(lineChart, BorderLayout.CENTER);
        content.add(trendPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private RoundedPanel createSummaryCard(String label, String value, Color accentColor) {
        RoundedPanel card = new RoundedPanel(14, accentColor);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppFonts.SMALL);
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(8));

        JLabel val = new JLabel(value);
        val.setFont(AppFonts.CARD_VALUE);
        val.setForeground(accentColor);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(val);

        return card;
    }
}
