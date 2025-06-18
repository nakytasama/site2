package ru.gallery.service;

import ru.gallery.model.User;
import java.sql.SQLException;

public interface UserService {
    User getByUsername(String username) throws SQLException;
    void register(User user) throws SQLException;
}
