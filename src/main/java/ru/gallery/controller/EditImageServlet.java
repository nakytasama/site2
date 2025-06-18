package ru.gallery.controller;

import ru.gallery.model.Image;
import ru.gallery.model.User;
import ru.gallery.service.ImageService;
import ru.gallery.service.ImageServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/edit-image")
@MultipartConfig
public class EditImageServlet extends HttpServlet {
    private final ImageService imageService = new ImageServiceImpl();
    private String uploadPath;

    @Override
    public void init() {
        uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            int imageId = Integer.parseInt(req.getParameter("id"));

            Image image = imageService.getImageById(imageId);

            if (user == null || user.getId() != image.getUserId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Сохраняем оригинальные данные для отображения
            req.setAttribute("image", image);
            req.getRequestDispatcher("/edit-image.jsp").forward(req, resp);

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            int imageId = Integer.parseInt(req.getParameter("id"));
            Image existingImage = imageService.getImageById(imageId);

            if (existingImage == null || existingImage.getUserId() != user.getId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Получаем данные из формы (если не заполнены - оставляем старые)
            String title = req.getParameter("title");
            String description = req.getParameter("description");

            Image updatedImage = new Image();
            updatedImage.setId(imageId);
            updatedImage.setUserId(user.getId());
            updatedImage.setTitle(title != null && !title.isEmpty() ? title : existingImage.getTitle());
            updatedImage.setDescription(description != null && !description.isEmpty() ? description : existingImage.getDescription());
            updatedImage.setImagePath(existingImage.getImagePath());

            // Обработка нового файла
            Part filePart = req.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                // Удаляем старое изображение
                new File(uploadPath + File.separator +
                        existingImage.getImagePath().replace("uploads/", "")).delete();

                // Сохраняем новое
                String fileName = UUID.randomUUID() + "_" +
                        new File(filePart.getSubmittedFileName()).getName();
                filePart.write(uploadPath + File.separator + fileName);
                updatedImage.setImagePath("uploads/" + fileName);
            }

            // Обновляем в БД
            imageService.updateImage(updatedImage);
            resp.sendRedirect("profile");

        } catch (Exception e) {
            // При ошибке сохраняем введенные данные для повторного показа
            req.setAttribute("error", "Ошибка при обновлении: " + e.getMessage());
            req.setAttribute("enteredTitle", req.getParameter("title"));
            req.setAttribute("enteredDescription", req.getParameter("description"));
            req.setAttribute("id", req.getParameter("id"));
            doGet(req, resp); // Показываем форму снова
        }
    }
}
