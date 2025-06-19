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
import java.util.UUID;

@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class UploadServlet extends HttpServlet {
    private final ImageService imageService = new ImageServiceImpl();
    private final String uploadPath = "C:\\Users\\nakyt\\IdeaProjects\\uploads";

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
            Image image = new Image();
            image.setUserId(user.getId());
            image.setTitle(req.getParameter("title"));
            image.setDescription(req.getParameter("description"));

            Part filePart = req.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                String mimeType = filePart.getContentType();
                // проверяем, что это изображение
                if (mimeType == null || !mimeType.startsWith("image/")) {
                    throw new ServletException("Можно загружать только изображения");
                }
                String originalFileName = filePart.getSubmittedFileName();
                String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                String fileName = UUID.randomUUID() + "_" + safeFileName;

                File targetFile = new File(uploadPath, fileName);
                filePart.write(targetFile.getAbsolutePath());

                image.setImagePath(fileName);
            }

            imageService.addImage(image);
            resp.sendRedirect("profile");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка при добавлении: " + e.getMessage());
            req.getRequestDispatcher("add-image.jsp").forward(req, resp);
        }
    }
}