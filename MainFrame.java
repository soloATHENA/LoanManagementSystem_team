package screens;

import components.Sidebar;
import screens.customer.*;
import screens.employee.*;
import utils.AppColors;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;
    private JPanel customerContent;
    private JPanel employeeContent;
    private CardLayout customerCardLayout;
    private CardLayout employeeCardLayout;
    private LoginScreen loginScreen;

    public MainFrame() {
        setTitle("LoanHub - Loan Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 700));
        setLocationRelativeTo(null);

        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);

        // Login Screen captures data and passes it here
        loginScreen = new LoginScreen(() -> {
            // Get the user data from the new SessionManager!
            int userId = utils.SessionManager.getCurrentUserId();
            String name = utils.SessionManager.getCurrentUserName();
            String email = utils.SessionManager.getCurrentUserEmail();
            String role = utils.SessionManager.getCurrentUserRole();
            
            showPortal(role, userId, name, email);
        });


        mainCardPanel.add(loginScreen, "login");
        mainCardLayout.show(mainCardPanel, "login");
        setContentPane(mainCardPanel);
    }

    private JPanel buildCustomerPortal(int userId, String name, String email) {
        JPanel portal = new JPanel(new BorderLayout());

        customerCardLayout = new CardLayout();
        customerContent = new JPanel(customerCardLayout);

        // Pass the dynamic userId, name, and email to all screens!
        customerContent.add(new CustomerDashboard(userId, name), "dashboard");
        customerContent.add(new ApplyLoanScreen(userId), "apply");
        customerContent.add(new LoanStatusScreen(userId), "status");
        customerContent.add(new EMICalculatorScreen(), "emi");
        customerContent.add(new ProfileScreen(userId, name, email), "profile");


        String[] icons = {"\u25A6", "\u2B1C", "\u2B1C", "\u2B1C", "\u263A"};
        String[] labels = {"Dashboard", "Apply Loan", "Loan Status", "EMI Calculator", "Profile"};
        Runnable[] actions = {
            () -> { customerCardLayout.show(customerContent, "dashboard"); },
            () -> { customerCardLayout.show(customerContent, "apply"); },
            () -> { customerCardLayout.show(customerContent, "status"); },
            () -> { customerCardLayout.show(customerContent, "emi"); },
            () -> { customerCardLayout.show(customerContent, "profile"); },
        };

        // Pass real name and email to Sidebar
        Sidebar sidebar = new Sidebar("Customer Portal", icons, labels, actions,
            name, email, this::logout);

        portal.add(sidebar, BorderLayout.WEST);
        portal.add(customerContent, BorderLayout.CENTER);

        return portal;
    }

    private JPanel buildEmployeePortal(int userId, String name, String email) {
        JPanel portal = new JPanel(new BorderLayout());

        employeeCardLayout = new CardLayout();
        employeeContent = new JPanel(employeeCardLayout);

        // Pass the user's ID and name to EmployeeDashboard
        employeeContent.add(new EmployeeDashboard(userId, name), "dashboard");
        employeeContent.add(new AllApplicationsScreen(), "applications");
        employeeContent.add(new ApprovalsScreen(), "approvals");
        employeeContent.add(new CustomersScreen(), "customers");
        employeeContent.add(new ReportsScreen(), "reports");

        String[] icons = {"\u25A6", "\u2B1C", "\u2713", "\u263A", "\u2261"};
        String[] labels = {"Dashboard", "All Loan Applications", "Approvals", "Customers", "Reports"};
        Runnable[] actions = {
            () -> { employeeCardLayout.show(employeeContent, "dashboard"); },
            () -> { employeeCardLayout.show(employeeContent, "applications"); },
            () -> { employeeCardLayout.show(employeeContent, "approvals"); },
            () -> { employeeCardLayout.show(employeeContent, "customers"); },
            () -> { employeeCardLayout.show(employeeContent, "reports"); },
        };

        // Pass real name and email to Sidebar
        Sidebar sidebar = new Sidebar("Admin Panel", icons, labels, actions,
            name, email, this::logout);

        portal.add(sidebar, BorderLayout.WEST);
        portal.add(employeeContent, BorderLayout.CENTER);

        return portal;
    }

    private void showPortal(String role, int userId, String name, String email) {
        // Build the portal freshly when they login to inject their real data
        if (role.equals("Customer")) {
            mainCardPanel.add(buildCustomerPortal(userId, name, email), "customer");
            mainCardLayout.show(mainCardPanel, "customer");
        } else {
            mainCardPanel.add(buildEmployeePortal(userId, name, email), "employee");
            mainCardLayout.show(mainCardPanel, "employee");
        }
    }

    private void logout() {
        mainCardLayout.show(mainCardPanel, "login");
    }
}
