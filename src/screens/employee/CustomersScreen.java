package screens.employee;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;

public class CustomersScreen extends JPanel {

    public CustomersScreen() {
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Customers");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Manage and view all customer records.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        RoundedPanel tablePanel = new RoundedPanel(14);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] columns = {"Customer ID", "Name", "Email", "Phone", "Active Loans", "Total Approved Amount"};
        Object[][] data = fetchCustomersFromDatabase();

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

    private Object[][] fetchCustomersFromDatabase() {
        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return new Object[0][6];
            String sql = "SELECT U.user_id, U.name, U.email, " +
                         "(SELECT COUNT(*) FROM Loans L WHERE L.user_id = U.user_id AND L.status = 'Approved') as active_loans, " +
                         "(SELECT SUM(amount) FROM Loans L WHERE L.user_id = U.user_id AND L.status = 'Approved') as total_amount " +
                         "FROM Users U WHERE U.role = 'Customer'";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = stmt.executeQuery();
            java.util.List<Object[]> list = new java.util.ArrayList<>();
            while (rs.next()) {
                String total = rs.getString("total_amount");
                list.add(new Object[]{
                    rs.getString("user_id"), rs.getString("name"), rs.getString("email"), 
                    "Not provided", // Phone number isn't in our SQL schema yet
                    rs.getString("active_loans"), "₹" + (total != null ? total : "0")
                });
            }
            return list.toArray(new Object[0][0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0][6];
        }
    }
}
