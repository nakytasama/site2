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
        fileSizeThreshold = 1024 * 1024,      // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class UploadServlet extends HttpServlet {
    private final ImageService imageService = new ImageServiceImpl();
    private String uploadPath;

    @Override
    public void init() {
        uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            Image image = new Image();
            image.setUserId(user.getId());

            // Обработка текстовых полей
            image.setTitle(req.getParameter("title"));
            image.setDescription(req.getParameter("description"));

            // Обработка файла
            Part filePart = req.getPart("file"); // 'file' - name поля загрузки файла в форме
            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = filePart.getSubmittedFileName();
                String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                String fileName = UUID.randomUUID().toString() + "_" + safeFileName;

                File targetFile = new File(uploadPath + File.separator + fileName);
                filePart.write(targetFile.getAbsolutePath());

                image.setImagePath("uploads/" + fileName);
            }

            imageService.addImage(image);
            resp.sendRedirect("profile");
        } catch (Exception e) {
            throw new ServletException("Ошибка при загрузке файла", e);
        }
    }
}