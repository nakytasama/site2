package ru.gallery.controller;

import ru.gallery.model.User;
import ru.gallery.service.UserService;
import ru.gallery.service.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            User user = userService.getByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);

                // Перенаправляем в зависимости от роли
                if ("admin".equals(user.getRole())) {
                    resp.sendRedirect(req.getContextPath() + "/admin.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/home");
                }
            } else {
                req.setAttribute("error", "Неверный логин или пароль");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при входе");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}