package ru.gallery.controller;

import ru.gallery.model.User;
import ru.gallery.service.UserService;
import ru.gallery.service.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("user");
        try {
            userService.register(user);
            resp.sendRedirect("login.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при регистрации");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
        }
    }
}