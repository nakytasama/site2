package ru.gallery.service;

import ru.gallery.model.Image;
import ru.gallery.service.ImageService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageServiceImpl implements ImageService {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver не найден( ", e);
        }
    }

    private final String jdbcURL = "jdbc:mysql://localhost:3306/image_gallery";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "1337";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    @Override
    public List<Image> getAllImages() throws SQLException {
        List<Image> images = new ArrayList<>();
        String sql = "SELECT * FROM images";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                images.add(createImageFromResultSet(rs));
            }
        }
        return images;
    }

    @Override
    public List<Image> getUserImages(int userId) throws SQLException {
        List<Image> images = new ArrayList<>();
        String sql = "SELECT * FROM images WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    images.add(createImageFromResultSet(rs));
                }
            }
        }
        return images;
    }

    @Override
    public Image getImageById(int id) throws SQLException {
        String sql = "SELECT * FROM images WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? createImageFromResultSet(rs) : null;
            }
        }
    }

    @Override
    public void addImage(Image image) throws SQLException {
        String sql = "INSERT INTO images (user_id, title, description, image_path) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, image.getUserId());
            stmt.setString(2, image.getTitle());
            stmt.setString(3, image.getDescription());
            stmt.setString(4, image.getImagePath());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateImage(Image image) throws SQLException {
        String sql = "UPDATE images SET title = ?, description = ?, image_path = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, image.getTitle());
            stmt.setString(2, image.getDescription());
            stmt.setString(3, image.getImagePath());
            stmt.setInt(4, image.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteImage(int id) throws SQLException {
        String sql = "DELETE FROM images WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Image createImageFromResultSet(ResultSet rs) throws SQLException {
        Image image = new Image();
        image.setId(rs.getInt("id"));
        image.setUserId(rs.getInt("user_id"));
        image.setTitle(rs.getString("title"));
        image.setDescription(rs.getString("description"));
        image.setImagePath(rs.getString("image_path"));
        return image;
    }
}