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
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final ImageService imageService = new ImageServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if ("details".equals(action)) {
                handleImageDetails(req, resp);
            } else if ("list".equals(action)) {
                handleImageList(req, resp);
            } else {
                handleGalleryRequest(req, resp);
            }
        } catch (SQLException e) {
            handleDatabaseError(resp, e);
        }
    }

    private void handleGalleryRequest(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        List<Image> images = imageService.getAllImages();
        req.setAttribute("images", images);
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    private void handleImageDetails(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Не указан ID изображения");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Image image = imageService.getImageById(id);
            if (image == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Изображение не найдено");
                return;
            }

            sendJsonResponse(resp, image);
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID изображения");
        }
    }

    private void handleImageList(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        List<Image> images = imageService.getAllImages();
        sendJsonResponse(resp, images);
    }

    private void sendJsonResponse(HttpServletResponse resp, Object data)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        gson.toJson(data, resp.getWriter());
    }

    private void sendError(HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.sendError(status, message);
    }

    private void handleDatabaseError(HttpServletResponse resp, SQLException e)
            throws IOException {
        e.printStackTrace();
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Ошибка базы данных: " + e.getMessage());
    }
}