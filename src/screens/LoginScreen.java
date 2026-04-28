package screens;

import components.*;
import utils.AppColors;
import utils.AppFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import javax.imageio.ImageIO;

public class LoginScreen extends JPanel {
    private StyledTextField emailField;
    private StyledPasswordField passwordField;
    private String selectedRole = "Customer";
    private JPanel customerRoleBtn;
    private JPanel employeeRoleBtn;
    private Runnable onLoginSuccess;
    
    private Image backgroundImage;

    public LoginScreen(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        
        // GridBagLayout naturally centers everything exactly in the middle!
        setLayout(new GridBagLayout()); 
        
        // Try to load image if it exists, otherwise it will just use the gradient
        try {
            File imgFile = new File("src/utils/logo_bg.png");
            if (imgFile.exists()) {
                backgroundImage = ImageIO.read(imgFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        buildUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(0, 0, 0, 80)); // Darker overlay so centered text is readable
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Sleek dark gradient fallback
            GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), getWidth(), getHeight(), new Color(30, 58, 138));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }

    private void buildUI() {
        // Glassmorphism Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Frosted Glass Effect (White with 95% opacity for readability)
                g2.setColor(new Color(255, 255, 255, 245)); 
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 24, 24));
                
                // Subtle glowing border
                g2.setColor(new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(45, 45, 45, 45));
        card.setPreferredSize(new Dimension(420, 520));
        card.setMaximumSize(new Dimension(420, 520));

        // --- TITLE ROW WITH LOGO ---
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Small dynamic logo next to the title
        JPanel smallLogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(37, 99, 235), 40, 40, new Color(59, 130, 246));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, 40, 40, 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2.drawString("₹", 13, 28); // Indian Rupee Symbol
                g2.dispose();
            }
        };
        smallLogo.setOpaque(false);
        smallLogo.setPreferredSize(new Dimension(40, 40));
        titleRow.add(smallLogo);

        JLabel title = new JLabel("Welcome Back!");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        titleRow.add(title);
        
        card.add(titleRow);
        
        JLabel subtitle = new JLabel("Please login to your account.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 55, 0, 0)); // Align with text, skipping the logo
        card.add(subtitle);
        card.add(Box.createVerticalStrut(35));

        // Email field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(AppFonts.BODY_BOLD);
        emailLabel.setForeground(AppColors.TEXT_PRIMARY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(8));

        emailField = new StyledTextField("you@example.com");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setText("admin"); // Demo default
        card.add(emailField);
        card.add(Box.createVerticalStrut(16));

        // Password field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(AppFonts.BODY_BOLD);
        passLabel.setForeground(AppColors.TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passLabel);
        card.add(Box.createVerticalStrut(8));

        passwordField = new StyledPasswordField("Enter your password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setText("1234"); // Demo default
        card.add(passwordField);
        card.add(Box.createVerticalStrut(25));

        // Role selector
        JLabel roleLabel = new JLabel("Login as");
        roleLabel.setFont(AppFonts.BODY_BOLD);
        roleLabel.setForeground(AppColors.TEXT_PRIMARY);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(roleLabel);
        card.add(Box.createVerticalStrut(10));

        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 12, 0));
        rolePanel.setOpaque(false);
        rolePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        customerRoleBtn = createRoleButton("\u263A", "Customer", true);
        employeeRoleBtn = createRoleButton("\u2611", "Employee", false);

        rolePanel.add(customerRoleBtn);
        rolePanel.add(employeeRoleBtn);
        card.add(rolePanel);
        card.add(Box.createVerticalStrut(30));

        // Login button
        StyledButton loginBtn = new StyledButton("Secure Login");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);

        // Add the card directly to the screen (GridBagLayout will auto-center it!)
        add(card);
    }

    private JPanel createRoleButton(String icon, String role, boolean isSelected) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = selectedRole.equals(role);
                g2.setColor(active ? AppColors.PRIMARY_BLUE_LIGHT : Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.setColor(active ? AppColors.PRIMARY_BLUE : AppColors.BORDER);
                g2.setStroke(new BasicStroke(active ? 2 : 1));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel iconLabel = new JLabel(icon + " ", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(iconLabel);

        JLabel nameLabel = new JLabel(role, SwingConstants.CENTER);
        nameLabel.setFont(AppFonts.BODY_BOLD);
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(nameLabel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRole = role;
                customerRoleBtn.repaint();
                employeeRoleBtn.repaint();
            }
        });

        return panel;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try (java.sql.Connection conn = utils.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT user_id, name, role FROM Users WHERE (email = ? OR name = ?) AND password = ? AND role = ?")) {
            
            stmt.setString(1, email);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, selectedRole);
            
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    utils.SessionManager.login(rs.getInt("user_id"), rs.getString("name"), rs.getString("role"), email);
                    if (onLoginSuccess != null) onLoginSuccess.run();
                } else {
                    shakeComponent(this);
                    JOptionPane.showMessageDialog(this, "Invalid credentials or role mismatch!\nPlease try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void shakeComponent(JComponent component) {
        Point originalLocation = component.getLocation();
        Timer timer = new Timer(30, null);
        final int[] count = {0};
        timer.addActionListener(e -> {
            count[0]++;
            int offset = (count[0] % 2 == 0) ? 5 : -5;
            component.setLocation(originalLocation.x + offset, originalLocation.y);
            if (count[0] >= 8) {
                component.setLocation(originalLocation);
                timer.stop();
            }
        });
        timer.start();
    }
}
