package screens.customer;

import components.*;
import utils.AppColors;
import utils.AppFonts;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ProfileScreen extends JPanel {
    private int userId;
    private String userName;
    private String userEmail;
    
    private StyledTextField nameField;
    private StyledTextField emailField;
    private StyledPasswordField passField;

    public ProfileScreen(int userId, String name, String email) {
        this.userId = userId;
        this.userName = name;
        this.userEmail = email;
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Profile");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Manage your personal information and preferences.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(25));

        RoundedPanel profileCard = new RoundedPanel(14);
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.PRIMARY_BLUE_LIGHT);
                g2.fillOval(0, 0, 80, 80);
                g2.setColor(AppColors.PRIMARY_BLUE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                
                String initial = userName.substring(0, 1).toUpperCase();
                g2.drawString(initial, (80 - fm.stringWidth(initial)) / 2, (80 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(80, 80));
        avatarPanel.add(avatar);
        profileCard.add(avatarPanel);
        profileCard.add(Box.createVerticalStrut(15));

        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(AppFonts.TITLE);
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileCard.add(nameLabel);

        JLabel emailLabel = new JLabel(userEmail);
        emailLabel.setFont(AppFonts.BODY);
        emailLabel.setForeground(AppColors.TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileCard.add(emailLabel);
        profileCard.add(Box.createVerticalStrut(25));

        JLabel sectionTitle = new JLabel("Personal Information");
        sectionTitle.setFont(AppFonts.HEADING);
        sectionTitle.setForeground(AppColors.TEXT_PRIMARY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(sectionTitle);
        profileCard.add(Box.createVerticalStrut(15));

        JPanel fieldsGrid = new JPanel(new GridLayout(2, 2, 20, 15));
        fieldsGrid.setOpaque(false);
        fieldsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        fieldsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = new StyledTextField(userName);
        emailField = new StyledTextField(userEmail);
        passField = new StyledPasswordField("");
        
        fieldsGrid.add(createFieldPanel("Full Name", nameField));
        fieldsGrid.add(createFieldPanel("Email Address", emailField));
        fieldsGrid.add(createFieldPanel("New Password (optional)", passField));
        fieldsGrid.add(createFieldPanel("Phone", new StyledTextField("+91 9876543210"))); // Mock phone

        profileCard.add(fieldsGrid);
        profileCard.add(Box.createVerticalStrut(25));

        StyledButton updateBtn = new StyledButton("Update Profile");
        updateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateBtn.setMaximumSize(new Dimension(200, 46));
        updateBtn.addActionListener(e -> updateDatabaseProfile());
        profileCard.add(updateBtn);

        content.add(profileCard);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateDatabaseProfile() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPass = new String(passField.getPassword());
        
        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            
            String sql;
            if (!newPass.isEmpty()) {
                sql = "UPDATE Users SET name = ?, email = ?, password = ? WHERE user_id = ?";
            } else {
                sql = "UPDATE Users SET name = ?, email = ? WHERE user_id = ?";
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newName);
                stmt.setString(2, newEmail);
                if (!newPass.isEmpty()) {
                    stmt.setString(3, newPass);
                    stmt.setInt(4, userId);
                } else {
                    stmt.setInt(3, userId);
                }
                stmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Profile updated successfully!\nNote: Changes to name/email will reflect fully when you log back in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            passField.setText("");
            
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
        lbl.setFont(AppFonts.SMALL);
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(field);

        return panel;
    }
}
