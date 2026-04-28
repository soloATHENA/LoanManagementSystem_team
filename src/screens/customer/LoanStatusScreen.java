package screens.customer;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoanStatusScreen extends JPanel {
    private int userId;

    public LoanStatusScreen(int userId) {
        this.userId = userId;
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Loan Status");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Track all your loan applications and their current status.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(25));

        loadUserLoans(content);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUserLoans(JPanel content) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            
            String sql = "SELECT loan_id, loan_type, amount, status, application_date FROM Loans WHERE user_id = ? ORDER BY application_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                boolean hasLoans = false;
                
                while (rs.next()) {
                    hasLoans = true;
                    String status = rs.getString("status");
                    double amount = rs.getDouble("amount");
                    
                    String remBal = null;
                    String paidAmt = null;
                    int progress = 0;
                    
                    if (status.equals("Approved")) {
                        // Mock progress data for realism
                        progress = 15;
                        double paid = amount * 0.15;
                        double remaining = amount - paid;
                        remBal = "₹" + String.format("%.2f", remaining);
                        paidAmt = "₹" + String.format("%.2f", paid);
                    }
                    
                    content.add(createLoanCard(
                        rs.getString("loan_type"), rs.getString("loan_id"), status,
                        "₹" + String.format("%.2f", amount), rs.getString("application_date"), 
                        "10.5% p.a.", "36 months", "₹" + String.format("%.2f", (amount * 0.03)),
                        remBal, progress, paidAmt
                    ));
                    content.add(Box.createVerticalStrut(20));
                }
                
                if (!hasLoans) {
                    JLabel noLoans = new JLabel("You have no loan history. Apply for one today!");
                    noLoans.setFont(AppFonts.BODY_BOLD);
                    noLoans.setForeground(AppColors.TEXT_SECONDARY);
                    content.add(noLoans);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RoundedPanel createLoanCard(String loanName, String loanId, String status,
                                         String amount, String appliedDate, String interestRate,
                                         String tenure, String emi, String remainingBalance,
                                         int progressPercent, String paidAmount) {
        Color bgTint = status.equals("Approved") ? new Color(240, 253, 244) : (status.equals("Rejected") ? AppColors.STATUS_RED_BG : new Color(255, 251, 235));

        RoundedPanel card = new RoundedPanel(14);
        card.setBackground(bgTint);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorderColor(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel(loanName);
        nameLabel.setFont(AppFonts.TITLE);
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        namePanel.add(nameLabel);
        StatusBadge badge = new StatusBadge(status);
        namePanel.add(badge);
        header.add(namePanel, BorderLayout.WEST);

        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        amountPanel.setOpaque(false);
        JLabel amountTopLabel = new JLabel("Loan Amount");
        amountTopLabel.setFont(AppFonts.SMALL);
        amountTopLabel.setForeground(AppColors.TEXT_SECONDARY);
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(AppFonts.CARD_VALUE);
        amountLabel.setForeground(AppColors.TEXT_PRIMARY);
        JPanel amtCol = new JPanel();
        amtCol.setOpaque(false);
        amtCol.setLayout(new BoxLayout(amtCol, BoxLayout.Y_AXIS));
        amountTopLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        amountLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        amtCol.add(amountTopLabel);
        amtCol.add(amountLabel);
        amountPanel.add(amtCol);
        header.add(amountPanel, BorderLayout.EAST);

        card.add(header);
        card.add(Box.createVerticalStrut(3));

        JLabel idLabel = new JLabel("Loan ID: " + loanId);
        idLabel.setFont(AppFonts.SMALL);
        idLabel.setForeground(AppColors.TEXT_SECONDARY);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(idLabel);
        card.add(Box.createVerticalStrut(15));

        JPanel detailsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        detailsRow.setOpaque(false);
        detailsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        detailsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsRow.add(createDetailItem("\uD83D\uDCC5", "Applied Date", appliedDate));
        detailsRow.add(createDetailItem("\u2197", "Interest Rate", interestRate));
        detailsRow.add(createDetailItem("\u23F0", "Tenure", tenure));
        detailsRow.add(createDetailItem("$", "Monthly EMI", emi));
        card.add(detailsRow);

        if (remainingBalance != null) {
            card.add(Box.createVerticalStrut(20));

            RoundedPanel balancePanel = new RoundedPanel(10);
            balancePanel.setBackground(new Color(240, 253, 244));
            balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
            balancePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            balancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            balancePanel.setHasShadow(false);
            balancePanel.setBorderColor(null);

            JPanel balanceHeader = new JPanel(new BorderLayout());
            balanceHeader.setOpaque(false);
            balanceHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            balanceHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            JLabel balLabel = new JLabel("Remaining Balance");
            balLabel.setFont(AppFonts.BODY_BOLD);
            balLabel.setForeground(AppColors.TEXT_PRIMARY);
            balanceHeader.add(balLabel, BorderLayout.WEST);

            JLabel balAmount = new JLabel(remainingBalance);
            balAmount.setFont(AppFonts.HEADING);
            balAmount.setForeground(AppColors.STATUS_GREEN);
            balanceHeader.add(balAmount, BorderLayout.EAST);

            balancePanel.add(balanceHeader);
            balancePanel.add(Box.createVerticalStrut(10));

            JPanel progressBar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(209, 250, 229));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(AppColors.STATUS_GREEN);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() * progressPercent / 100.0, getHeight(), 10, 10));
                    g2.dispose();
                }
            };
            progressBar.setPreferredSize(new Dimension(0, 10));
            progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
            progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
            balancePanel.add(progressBar);
            balancePanel.add(Box.createVerticalStrut(8));

            JLabel progressLabel = new JLabel(progressPercent + "% paid \u2022 " + paidAmount + " repaid");
            progressLabel.setFont(AppFonts.SMALL);
            progressLabel.setForeground(AppColors.TEXT_SECONDARY);
            progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            balancePanel.add(progressLabel);

            card.add(balancePanel);
        }

        return card;
    }

    private JPanel createDetailItem(String icon, String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setForeground(AppColors.PRIMARY_BLUE);
        panel.add(iconLabel);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppFonts.SMALL);
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
