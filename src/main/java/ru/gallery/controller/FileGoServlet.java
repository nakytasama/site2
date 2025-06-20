package ru.gallery.controller;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/uploads/*")
public class FileGoServlet extends HttpServlet {
    private final String UPLOAD_DIR = "C:\\Users\\nakyt\\IdeaProjects\\uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String fileName = req.getPathInfo().substring(1); // /uploads/файл.jpg → файл.jpg
        File file = new File(UPLOAD_DIR, fileName);

        resp.setContentType(getServletContext().getMimeType(fileName));
        resp.setContentLengthLong(file.length());
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}
