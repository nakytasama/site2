package ru.gallery.controller;

import com.google.gson.Gson;
import ru.gallery.model.Image;
import ru.gallery.service.ImageService;
import ru.gallery.service.ImageServiceImpl;
import ru.gallery.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/image")
public class ImageServlet extends HttpServlet {
    private final ImageService imageService = new ImageServiceImpl();
    private final Gson gson = new Gson();
    private final String uploadPath = "C:\\Users\\nakyt\\IdeaProjects\\uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");
            String idParam = req.getParameter("id");

            if (action == null || idParam == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int id = Integer.parseInt(idParam);

            if ("details".equals(action)) {
                Image image = imageService.getImageById(id);
                if (image == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(gson.toJson(image));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException | SQLException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Требуется авторизация");
            return;
        }

        String action = req.getParameter("action");
        String idParam = req.getParameter("id");

        if (action == null || idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверные параметры");
            return;
        }

        try {
            int imageId = Integer.parseInt(idParam);
            Image imageToDelete = imageService.getImageById(imageId);

            if (imageToDelete == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Изображение не найдено");
                return;
            }

            // проверка прав:
            boolean isAdmin = "admin".equals(currentUser.getRole());
            boolean isOwner = currentUser.getId() == imageToDelete.getUserId();

            if (!isAdmin && !isOwner) {
                resp.sendError(403);
                return;
            }

            // удаление файла
            File file = new File(uploadPath, imageToDelete.getImagePath());
            if (file.exists() && !file.delete()) {
                System.err.println("Не удалось удалить файл: " + file.getAbsolutePath());
            }

            imageService.deleteImage(imageId);

        } catch (NumberFormatException | SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
        }
    }
}