package screens.customer;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

public class ApplyLoanScreen extends JPanel {
    private int userId;
    
    private JComboBox<String> loanTypeCombo;
    private StyledTextField amountField;

    public ApplyLoanScreen(int userId) {
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

        JLabel title = new JLabel("Apply for a Loan");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Fill in the details below to submit your loan application.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(25));

        RoundedPanel formCard = new RoundedPanel(14);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Row 1
        JPanel row1 = new JPanel(new GridLayout(1, 2, 20, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);

        loanTypeCombo = createComboBox(new String[]{"Select loan type", "Home Loan", "Personal Loan", "Education Loan", "Business Loan"});
        amountField = new StyledTextField(""); // empty by default
        
        row1.add(createFieldPanel("Loan Type", loanTypeCombo));
        row1.add(createFieldPanel("Loan Amount (\u20B9)", amountField));
        formCard.add(row1);
        formCard.add(Box.createVerticalStrut(20));

        // Row 2
        JPanel row2 = new JPanel(new GridLayout(1, 2, 20, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);

        row2.add(createFieldPanel("Tenure (Months)", createComboBox(new String[]{"Select tenure", "12 months", "24 months", "36 months", "60 months"})));
        row2.add(createFieldPanel("Monthly Income (\u20B9)", new StyledTextField("")));
        formCard.add(row2);
        formCard.add(Box.createVerticalStrut(20));

        // Required Documents info box
        RoundedPanel docsPanel = new RoundedPanel(10);
        docsPanel.setBackground(new Color(239, 246, 255));
        docsPanel.setLayout(new BoxLayout(docsPanel, BoxLayout.Y_AXIS));
        docsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        docsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        docsPanel.setHasShadow(false);
        docsPanel.setBorderColor(null);

        JLabel docsTitle = new JLabel("\u2610 Required Documents (To be verified later):");
        docsTitle.setFont(AppFonts.BODY_BOLD);
        docsTitle.setForeground(AppColors.PRIMARY_BLUE);
        docsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        docsPanel.add(docsTitle);
        docsPanel.add(Box.createVerticalStrut(8));

        String[] docs = {"Identity Proof (Aadhar/PAN)", "Income Proof (Salary Slips)"};
        for (String doc : docs) {
            JLabel docItem = new JLabel("  \u2022 " + doc);
            docItem.setFont(AppFonts.BODY);
            docItem.setForeground(AppColors.TEXT_PRIMARY);
            docsPanel.add(docItem);
        }
        formCard.add(docsPanel);
        formCard.add(Box.createVerticalStrut(25));

        // Buttons row
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 15, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        StyledButton submitBtn = new StyledButton("Submit Application");
        submitBtn.addActionListener(e -> submitLoanApplication());
        btnRow.add(submitBtn);

        formCard.add(btnRow);

        content.add(formCard);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // --- DATABASE INSERT LOGIC ---
    private void submitLoanApplication() {
        String loanType = (String) loanTypeCombo.getSelectedItem();
        String amountStr = amountField.getText().trim();
        
        if (loanType.contains("Select") || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a loan type and enter an amount.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            // 1. Generate unique loan ID (e.g., L9245)
            String loanId = "L" + (1000 + new Random().nextInt(9000));
            double amount = Double.parseDouble(amountStr);
            
            // 2. Insert into Loans table
            String sql = "INSERT INTO Loans (loan_id, user_id, loan_type, amount, status, application_date) VALUES (?, ?, ?, ?, 'Pending', CURDATE())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, loanId);
                stmt.setInt(2, userId);
                stmt.setString(3, loanType);
                stmt.setDouble(4, amount);
                stmt.executeUpdate();
            }

            // 3. Insert into ActivityLog
            String actSql = "INSERT INTO ActivityLog (user_id, activity_text, activity_date, status_color) VALUES (?, ?, CURDATE(), 'AMBER')";
            try (PreparedStatement stmt = conn.prepareStatement(actSql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, "Submitted " + loanType + " application");
                stmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Application Submitted Successfully!\nYour Loan ID is: " + loanId, "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields for next use
            amountField.setText("");
            loanTypeCombo.setSelectedIndex(0);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the loan amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppFonts.BODY_BOLD);
        lbl.setForeground(AppColors.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(8));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        panel.add(field);

        return panel;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(AppFonts.INPUT);
        combo.setBackground(AppColors.BG_INPUT);
        combo.setForeground(AppColors.TEXT_SECONDARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return combo;
    }
}
