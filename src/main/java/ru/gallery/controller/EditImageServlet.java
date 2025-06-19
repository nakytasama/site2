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
    private final String uploadPath = "C:\\Users\\nakyt\\IdeaProjects\\uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            int imageId = Integer.parseInt(req.getParameter("id"));

            Image image = imageService.getImageById(imageId);

            if (image == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Проверка прав: админ или владелец
            if (user == null ||
                    (!"admin".equals(user.getRole()) && user.getId() != image.getUserId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

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

            if (existingImage == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (!"admin".equals(user.getRole()) && user.getId() != existingImage.getUserId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String title = req.getParameter("title");
            String description = req.getParameter("description");

            Image updatedImage = new Image();
            updatedImage.setId(imageId);
            updatedImage.setUserId(existingImage.getUserId());
            updatedImage.setTitle((title != null && !title.isEmpty()) ? title : existingImage.getTitle());
            updatedImage.setDescription((description != null && !description.isEmpty()) ? description : existingImage.getDescription());
            updatedImage.setImagePath(existingImage.getImagePath());

            Part filePart = req.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                String mimeType = filePart.getContentType();
                // проверяем, что это изображение
                if (mimeType == null || !mimeType.startsWith("image/")) {
                    throw new ServletException("Можно загружать только изображения");
                }
                deleteImageFile(existingImage.getImagePath());
                String originalFileName = filePart.getSubmittedFileName();
                String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                String fileName = UUID.randomUUID() + "_" + safeFileName;

                File targetFile = new File(uploadPath, fileName);
                filePart.write(targetFile.getAbsolutePath());

                updatedImage.setImagePath(fileName);
            }

            imageService.updateImage(updatedImage);
            resp.sendRedirect("profile");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка при обновлении: " + e.getMessage());
            doGet(req, resp);
        }
    }

    private void deleteImageFile(String imagePath) {
        if (imagePath == null) return;
        File file = new File(uploadPath, imagePath);
        if (file.exists()) {
            if (!file.delete()) {
                System.err.println("Не удалось удалить старый файл: " + file.getAbsolutePath());
            }
        }
    }
}
