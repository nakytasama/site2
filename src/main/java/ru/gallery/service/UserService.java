package ru.gallery.service;

import ru.gallery.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    User getByUsername(String username) throws SQLException;
    void register(User user) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    void deleteUser(int id) throws SQLException;
    void updateUser(int id, String username, String role) throws SQLException;
}
