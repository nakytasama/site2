package ru.gallery.controller;

import com.google.gson.Gson;
import ru.gallery.model.Image;
import ru.gallery.service.ImageService;
import ru.gallery.service.ImageServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/image")
public class ImageServlet extends HttpServlet {
    private final ImageService imageService = new ImageServiceImpl();
    private final Gson gson = new Gson();

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");
            String idParam = req.getParameter("id");

            if (action == null || idParam == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int id = Integer.parseInt(idParam);

            if ("delete".equals(action)) {
                imageService.deleteImage(id);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException | SQLException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}