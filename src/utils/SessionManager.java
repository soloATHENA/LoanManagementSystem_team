package utils;

public class SessionManager {
    private static int currentUserId = -1;
    private static String currentUserName = null;
    private static String currentUserRole = null;
    private static String currentUserEmail = null;

    public static void login(int userId, String name, String role, String email) {
        currentUserId = userId;
        currentUserName = name;
        currentUserRole = role;
        currentUserEmail = email;
    }

    public static void logout() {
        currentUserId = -1;
        currentUserName = null;
        currentUserRole = null;
        currentUserEmail = null;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static boolean isLoggedIn() {
        return currentUserId != -1;
    }
}

