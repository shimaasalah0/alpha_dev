package com.jarena.util;

import com.jarena.model.User;

import javax.servlet.http.HttpSession;

public class AuthHelper {

    private static final String SESSION_USER_KEY = "loggedInUser";

    public static User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getLoggedInUser(session) != null;
    }

    public static boolean isAdmin(HttpSession session) {
        User user = getLoggedInUser(session);
        return user != null && "ADMIN".equals(user.getRole());
    }

    public static boolean isCustomer(HttpSession session) {
        User user = getLoggedInUser(session);
        return user != null && "CUSTOMER".equals(user.getRole());
    }

    public static void setLoggedInUser(HttpSession session, User user) {
        session.setAttribute(SESSION_USER_KEY, user);
    }

    public static void logout(HttpSession session) {
        session.invalidate();
    }
}
