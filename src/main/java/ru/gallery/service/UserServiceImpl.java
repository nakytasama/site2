package ru.gallery.service;

import ru.gallery.model.User;
import ru.gallery.service.UserService;
import java.sql.*;

public class UserServiceImpl implements UserService {

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
    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                return user;
            }
        }
        return null;
    }
    @Override
    public void register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.executeUpdate();
        }
    }
}
