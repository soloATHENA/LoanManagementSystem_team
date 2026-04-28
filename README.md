# 🏦 Loan Management System

A full-stack Java Desktop Application built with modern **Glassmorphism** UI principles and powered by a robust **MySQL** backend. This system provides a seamless experience for both Customers applying for loans and Employees managing approvals and analytics.

## ✨ Key Features

*   **🔒 Secure Role-Based Authentication:** Distinct portals and permissions for `Customers` and `Employees`.
*   **💳 Customer Portal:**
    *   Dynamic dashboard tracking active loans, total balance, and recent activity.
    *   Interactive loan application form.
    *   Real-time loan status tracking (Pending, Approved, Rejected) with dynamic remaining balance progress bars.
    *   Profile management.
*   **👔 Employee Portal:**
    *   Live analytics dashboard featuring custom-built Java Graphics2D Pie Charts and Bar Graphs.
    *   Master view of all loan applications across the system.
    *   One-click Approve/Reject workflow that updates the database instantly.
    *   Historical trend tracking and reporting.
*   **🎨 Premium UI/UX:** Built entirely from scratch using Java Swing/AWT, featuring rounded borders, hover animations, gradient backgrounds, and frosted glass (glassmorphism) effects.

## 🛠️ Technology Stack

*   **Frontend UI:** Java Swing & AWT (Custom Graphics)
*   **Backend Logic:** Core Java
*   **Database:** MySQL
*   **Connectivity:** JDBC (Java Database Connectivity) API


## 🚀 How to Run Locally

### Prerequisites
*   Java Development Kit (JDK) 11 or higher.
*   MySQL Server (e.g., XAMPP or standalone MySQL).
*   Eclipse IDE (or any Java IDE).
*   `mysql-connector-java.jar` added to your project's build path.

### Setup Instructions
1. **Database Setup:** 
   * Open your MySQL client and run the provided `database.sql` script. This will automatically generate the `Users`, `Loans`, and `ActivityLog` tables and populate them with mock data.
2. **Project Setup:**
   * Clone this repository.
   * Open the project in Eclipse.
3. **Database Connection:**
   * Open `src/utils/DatabaseConnection.java`.
   * Update the `URL`, `USER`, and `PASSWORD` variables to match your local MySQL credentials.
4. **Run:**
   * Run the `LoanManagementApp.java` file to launch the application!

### 🔑 Demo Credentials
*   **Customer Login:** `rahul@example.com` / `1234`
*   **Employee Login:** `admin` / `1234`
