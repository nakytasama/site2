<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Редактировать изображение</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/image-forms.css">
</head>
<body>
    <div class="image-form-container">
        <a href="${pageContext.request.contextPath}/profile" class="image-back-link">← Назад</a>
        <div class="image-form-header">
            <h2>Редактировать изображение</h2>
        </div>

        <c:if test="${not empty error}">
            <p class="image-error-message">${error}</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/edit-image" method="post" enctype="multipart/form-data">
            <input type="hidden" name="id" value="${image.id}">

            <div class="image-form-group">
                <label for="title">Название:</label>
                <input type="text" id="title" name="title" class="image-form-control"
                       value="${not empty enteredTitle ? enteredTitle : image.title}"
                       required placeholder="Введите название">
            </div>

            <div class="image-form-group">
                <label for="description">Описание:</label>
                <textarea id="description" name="description" class="image-form-control image-form-textarea"
                          placeholder="Введите описание">${not empty enteredDescription ? enteredDescription : image.description}</textarea>
            </div>

            <div class="image-form-group">
                <label>Текущее изображение:</label>
                <img src="${pageContext.request.contextPath}/uploads/${image.imagePath}"
                     alt="${image.title}" class="image-current-preview">
            </div>

            <div class="image-form-group">
                <label for="file">Новое изображение:</label>
                <input type="file" id="file" name="file" class="image-form-control"
                       accept="image/*">
                <p class="image-file-hint">Оставьте пустым, чтобы сохранить текущее изображение</p>
            </div>

            <div>
                <button type="submit" class="image-btn image-btn-primary">Сохранить</button>
                <a href="${pageContext.request.contextPath}/profile" class="image-btn image-btn-secondary">Отмена</a>
            </div>
        </form>
    </div>
</body>
</html>
