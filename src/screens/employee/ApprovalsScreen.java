package screens.employee;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;

public class ApprovalsScreen extends JPanel {

    public ApprovalsScreen() {
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Pending Approvals");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Review and approve/reject pending loan applications.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(25));

        loadPendingApprovals(content);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPendingApprovals(JPanel content) {
        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            
            // Fetch ONLY 'Pending' loans from the database
            String sql = "SELECT L.loan_id, U.name, L.loan_type, L.amount, L.application_date " +
                         "FROM Loans L JOIN Users U ON L.user_id = U.user_id WHERE L.status = 'Pending'";
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                java.sql.ResultSet rs = stmt.executeQuery();
                boolean hasPending = false;
                
                while (rs.next()) {
                    hasPending = true;
                    content.add(createApprovalCard(
                        rs.getString("loan_type"), rs.getString("loan_id"), rs.getString("name"),
                        rs.getString("application_date"), "12% p.a.", "36 months", 
                        "₹" + rs.getString("amount"), "Salaried", "₹50,000"
                    ));
                    content.add(Box.createVerticalStrut(20));
                }
                
                if (!hasPending) {
                    JLabel noData = new JLabel("No pending applications to review. Great job!");
                    noData.setFont(AppFonts.BODY_BOLD);
                    noData.setForeground(AppColors.STATUS_GREEN);
                    content.add(noData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RoundedPanel createApprovalCard(String loanType, String loanId, String customerName,
                                             String appliedDate, String interestRate, String tenure,
                                             String amount, String employment, String income) {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel(loanType);
        nameLabel.setFont(AppFonts.TITLE);
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        namePanel.add(nameLabel);
        StatusBadge badge = new StatusBadge("Pending");
        namePanel.add(badge);
        header.add(namePanel, BorderLayout.WEST);

        card.add(header);

        JLabel idLabel = new JLabel("Loan ID: " + loanId);
        idLabel.setFont(AppFonts.SMALL);
        idLabel.setForeground(AppColors.TEXT_SECONDARY);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(idLabel);
        card.add(Box.createVerticalStrut(20));

        JPanel detailsGrid = new JPanel(new GridLayout(2, 4, 15, 15));
        detailsGrid.setOpaque(false);
        detailsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        detailsGrid.add(createDetailItem("\u263A", "Customer", customerName));
        detailsGrid.add(createDetailItem("\uD83D\uDCC5", "Applied Date", appliedDate));
        detailsGrid.add(createDetailItem("$", "Interest Rate", interestRate));
        detailsGrid.add(createDetailItem("\u23F0", "Tenure", tenure));
        detailsGrid.add(createDetailItem("\u20B9", "Loan Amount", amount));
        detailsGrid.add(createDetailItem("\u2611", "Employment", employment));
        detailsGrid.add(createDetailItem("\u20B9", "Monthly Income", income));

        card.add(detailsGrid);
        card.add(Box.createVerticalStrut(25));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // APPROVE BUTTON LOGIC
        StyledButton approveBtn = new StyledButton("Approve", AppColors.STATUS_GREEN, new Color(22, 163, 74));
        approveBtn.setPreferredSize(new Dimension(140, 42));
        approveBtn.addActionListener(e -> updateLoanStatus(loanId, "Approved"));
        btnRow.add(approveBtn);

        // REJECT BUTTON LOGIC
        StyledButton rejectBtn = new StyledButton("Reject", AppColors.STATUS_RED, new Color(220, 38, 38));
        rejectBtn.setPreferredSize(new Dimension(140, 42));
        rejectBtn.addActionListener(e -> updateLoanStatus(loanId, "Rejected"));
        btnRow.add(rejectBtn);

        card.add(btnRow);
        return card;
    }

    private void updateLoanStatus(String loanId, String newStatus) {
        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "UPDATE Loans SET status = ? WHERE loan_id = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newStatus);
                stmt.setString(2, loanId);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Loan " + loanId + " successfully " + newStatus + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the screen to remove the approved/rejected loan
                removeAll();
                buildUI();
                revalidate();
                repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDetailItem(String icon, String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        iconLabel.setForeground(AppColors.PRIMARY_BLUE);
        panel.add(iconLabel);
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppFonts.TINY);
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        textPanel.add(lbl);
        JLabel val = new JLabel(value);
        val.setFont(AppFonts.BODY_BOLD);
        val.setForeground(AppColors.TEXT_PRIMARY);
        textPanel.add(val);
        panel.add(textPanel);
        return panel;
    }
}
