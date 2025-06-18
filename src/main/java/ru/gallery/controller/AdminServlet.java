package ru.gallery.controller;

import ru.gallery.model.Image;
import ru.gallery.model.User;
import ru.gallery.service.ImageService;
import ru.gallery.service.ImageServiceImpl;
import ru.gallery.service.UserService;
import ru.gallery.service.UserServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();
    private final ImageService imageService = new ImageServiceImpl();
    private final String uploadPath = "C:\\Users\\nakyt\\IdeaProjects\\uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            List<User> users = userService.getAllUsers();
            List<Image> images = imageService.getAllImages();

            // Создаем карту пользователей для быстрого доступа
            Map<Integer, User> userMap = new HashMap<>();
            for (User user : users) {
                userMap.put(user.getId(), user);
            }

            req.setAttribute("users", users);
            req.setAttribute("images", images);
            req.setAttribute("userMap", userMap);
            req.setAttribute("currentUserId", currentUser.getId());
            req.getRequestDispatcher("/admin.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Ошибка при загрузке данных", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        String idParam = req.getParameter("id");

        if (action == null || idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            if ("deleteUser".equals(action)) {
                if (id == currentUser.getId()) {
                    req.setAttribute("error", "Вы не можете удалить себя");
                    doGet(req, resp);
                    return;
                }
                List<Image> userImages = imageService.getUserImages(id);
                for (Image image : userImages) {
                    deleteImageFile(image.getImagePath());
                }
                imageService.deleteImage(id);
                userService.deleteUser(id);

            } else if ("deleteImage".equals(action)) {
                Image image = imageService.getImageById(id);
                if (image != null) {
                    deleteImageFile(image.getImagePath());
                    imageService.deleteImage(id);
                }

            } else if ("updateUser".equals(action)) {
                String username = req.getParameter("username");
                String role = req.getParameter("role");

                // Для текущего админа фиксируем роль
                if (id == currentUser.getId()) {
                    role = "admin";
                }

                userService.updateUser(id, username, role);

            } else if ("updateImage".equals(action)) {
                String title = req.getParameter("title");
                String description = req.getParameter("description");
                imageService.updateImageInfo(id, title, description);
            }

            resp.sendRedirect("admin");
        } catch (NumberFormatException | SQLException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void deleteImageFile(String imagePath) {
        if (imagePath == null) return;
        File file = new File(uploadPath, imagePath);
        if (file.exists() && !file.delete()) {
            System.err.println("Не удалось удалить файл: " + file.getAbsolutePath());
        }
    }
}