package screens.employee;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AllApplicationsScreen extends JPanel {

    public AllApplicationsScreen() {
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("All Loan Applications");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Review and manage all loan applications.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        RoundedPanel tablePanel = new RoundedPanel(14);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] columns = {"Loan ID", "Customer Name", "Loan Type", "Amount", "Status", "Applied Date"};
        Object[][] data = fetchApplicationsFromDatabase();

        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setFont(AppFonts.BODY);
        table.setRowHeight(45);
        table.setGridColor(AppColors.BORDER);
        table.setShowGrid(true);
        table.getTableHeader().setFont(AppFonts.BODY_BOLD);
        table.getTableHeader().setBackground(AppColors.BG_INPUT);
        table.getTableHeader().setForeground(AppColors.TEXT_PRIMARY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setSelectionBackground(AppColors.PRIMARY_BLUE_LIGHT);

        // Custom cell renderer for Status column
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                switch (status) {
                    case "Approved":
                        label.setForeground(new Color(22, 163, 74));
                        label.setBackground(AppColors.STATUS_GREEN_BG);
                        break;
                    case "Pending":
                        label.setForeground(new Color(180, 120, 0));
                        label.setBackground(AppColors.STATUS_AMBER_BG);
                        break;
                    case "Rejected":
                        label.setForeground(AppColors.STATUS_RED);
                        label.setBackground(AppColors.STATUS_RED_BG);
                        break;
                }
                label.setOpaque(true);
                return label;
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        content.add(tablePanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private Object[][] fetchApplicationsFromDatabase() {
        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return new Object[0][6];
            String sql = "SELECT L.loan_id, U.name, L.loan_type, L.amount, L.status, L.application_date FROM Loans L JOIN Users U ON L.user_id = U.user_id ORDER BY L.application_date DESC";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = stmt.executeQuery();
            java.util.List<Object[]> list = new java.util.ArrayList<>();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("loan_id"), rs.getString("name"), rs.getString("loan_type"),
                    "₹" + rs.getString("amount"), rs.getString("status"), rs.getString("application_date")
                });
            }
            return list.toArray(new Object[0][0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0][6];
        }
    }
}
