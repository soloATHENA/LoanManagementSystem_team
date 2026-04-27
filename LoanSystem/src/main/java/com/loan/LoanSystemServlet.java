package com.loan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoanSystemServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	boolean isAdminLoggedIn = false;

	Connection getConnection() {
	    try {
	        return DriverManager.getConnection(
	            "jdbc:mysql://localhost:3306/loan_system",
	            "root",
	            "password"
	        );
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
	        throws IOException {

	    res.setContentType("text/html");
	    PrintWriter out = res.getWriter();

	    out.println("""
	<!DOCTYPE html>
	<html>
	<head>
	<title>Loan System</title>
	<style>
	body { font-family: Arial; background:#0f172a; color:white; }
	.container { width:80%; margin:auto; }
	.card { background:#1e293b; padding:20px; margin:20px; border-radius:10px; }
	input { width:100%; padding:10px; margin:5px 0; }
	button { width:100%; padding:10px; background:#38bdf8; border:none; }
	</style>
	</head>
	<body>
	<div class='container'>

	<h1>Loan System</h1>

	<div class='card'>
	<h2>Admin Login</h2>
	<form method='post'>
	<input type='hidden' name='action' value='login'>
	<input name='user' required placeholder='Username'>
	<input name='pass' type='password' required placeholder='Password'>
	<button>Login</button>
	</form>
	</div>

	<div class='card'>
	<h2>Add Customer</h2>
	<form method='post'>
	<input type='hidden' name='action' value='addCustomer'>
	<input name='name' required placeholder='Name'>
	<input name='phone' pattern='[0-9]{10}' required placeholder='Phone'>
	<input name='email' type='email' required placeholder='Email'>
	<input name='address' required placeholder='Address'>
	<button>Add Customer</button>
	</form>
	</div>

	<div class='card'>
	<h2>Apply Loan</h2>
	<form method='post'>
	<input type='hidden' name='action' value='applyLoan'>
	<input name='cid' type='number' required placeholder='Customer ID'>
	<input name='type' required placeholder='Loan Type'>
	<input name='amount' type='number' min='1' required placeholder='Amount'>
	<input name='interest' type='number' required placeholder='Interest'>
	<input name='duration' type='number' min='1' required placeholder='Months'>
	<button>Apply Loan</button>
	</form>
	</div>

	<div class='card'>
	<h2>Approve Loan (Admin Only)</h2>
	<form method='post'>
	<input type='hidden' name='action' value='approveLoan'>
	<input name='loanid' required placeholder='Loan ID'>
	<button>Approve</button>
	</form>
	</div>

	<div class='card'>
	<h2>Make Payment</h2>
	<form method='post'>
	<input type='hidden' name='action' value='payment'>
	<input name='loanid' required placeholder='Loan ID'>
	<input name='pay' type='number' min='1' required placeholder='Amount'>
	<input name='mode' required placeholder='Mode'>
	<button>Pay</button>
	</form>
	</div>

	</div>
	</body>
	</html>
	""");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
	        throws IOException {

	    String action = req.getParameter("action");

	    if ("login".equals(action)) adminLogin(req);
	    else if ("addCustomer".equals(action)) addCustomer(req);
	    else if ("applyLoan".equals(action)) applyLoan(req);
	    else if ("approveLoan".equals(action)) approveLoan(req);
	    else if ("payment".equals(action)) makePayment(req);

	    res.sendRedirect("LoanSystemServlet");
	}

	void adminLogin(HttpServletRequest req) {
	    try {
	        Connection con = getConnection();
	        PreparedStatement ps = con.prepareStatement(
	            "SELECT * FROM Admin WHERE username=? AND password=?"
	        );

	        ps.setString(1, req.getParameter("user"));
	        ps.setString(2, req.getParameter("pass"));

	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            isAdminLoggedIn = true;
	            System.out.println("Admin Logged In");
	        } else {
	            System.out.println("Invalid Login");
	        }

	    } catch (Exception e) { e.printStackTrace(); }
	}

	void addCustomer(HttpServletRequest req) {
	    try {
	        if (req.getParameter("name").isEmpty()) return;

	        Connection con = getConnection();
	        PreparedStatement ps = con.prepareStatement(
	            "INSERT INTO Customer(name,phone,email,address) VALUES(?,?,?,?)"
	        );

	        ps.setString(1, req.getParameter("name"));
	        ps.setString(2, req.getParameter("phone"));
	        ps.setString(3, req.getParameter("email"));
	        ps.setString(4, req.getParameter("address"));

	        ps.executeUpdate();

	    } catch (Exception e) { e.printStackTrace(); }
	}

	void applyLoan(HttpServletRequest req) {
	    try {
	        double amount = Double.parseDouble(req.getParameter("amount"));

	        if (amount <= 0) return;

	        Connection con = getConnection();
	        PreparedStatement ps = con.prepareStatement(
	            "INSERT INTO Loan(customer_id,loan_type,amount,interest_rate,duration,status,applied_date) VALUES(?,?,?,?,?,'Pending',CURDATE())"
	        );

	        ps.setInt(1, Integer.parseInt(req.getParameter("cid")));
	        ps.setString(2, req.getParameter("type"));
	        ps.setDouble(3, amount);
	        ps.setDouble(4, Double.parseDouble(req.getParameter("interest")));
	        ps.setInt(5, Integer.parseInt(req.getParameter("duration")));

	        ps.executeUpdate();

	    } catch (Exception e) { e.printStackTrace(); }
	}

	void approveLoan(HttpServletRequest req) {
	    try {
	        if (!isAdminLoggedIn) return;

	        Connection con = getConnection();
	        PreparedStatement ps = con.prepareStatement(
	            "UPDATE Loan SET status='Approved' WHERE loan_id=?"
	        );

	        ps.setInt(1, Integer.parseInt(req.getParameter("loanid")));
	        ps.executeUpdate();

	    } catch (Exception e) { e.printStackTrace(); }
	}

	void makePayment(HttpServletRequest req) {
	    try {
	        Connection con = getConnection();

	        PreparedStatement check = con.prepareStatement(
	            "SELECT status FROM Loan WHERE loan_id=?"
	        );
	        check.setInt(1, Integer.parseInt(req.getParameter("loanid")));
	        ResultSet rs = check.executeQuery();

	        if (rs.next() && rs.getString("status").equals("Approved")) {

	            PreparedStatement ps = con.prepareStatement(
	                "INSERT INTO Payment(loan_id,amount_paid,payment_date,payment_mode) VALUES(?,?,CURDATE(),?)"
	            );

	            ps.setInt(1, Integer.parseInt(req.getParameter("loanid")));
	            ps.setDouble(2, Double.parseDouble(req.getParameter("pay")));
	            ps.setString(3, req.getParameter("mode"));

	            ps.executeUpdate();
	        } else {
	            System.out.println("Loan not approved!");
	        }

	    } catch (Exception e) { e.printStackTrace(); }
	}
}