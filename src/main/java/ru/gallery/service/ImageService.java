package ru.gallery.service;

import ru.gallery.model.Image;
import java.sql.SQLException;
import java.util.List;

public interface ImageService {
    List<Image> getAllImages() throws SQLException;
    List<Image> getUserImages(int userId) throws SQLException;
    Image getImageById(int id) throws SQLException;
    void addImage(Image image) throws SQLException;
    void updateImage(Image image) throws SQLException;
    void deleteImage(int id) throws SQLException;
    void updateImageInfo(int id, String title, String description) throws SQLException;
}
