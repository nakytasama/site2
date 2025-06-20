<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Добавить изображение</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/image-forms.css">
</head>
<body>
    <div class="image-form-container">
        <a href="${pageContext.request.contextPath}/profile" class="image-back-link">← Назад</a>
        <div class="image-form-header">
            <h2>Добавить новое изображение</h2>
        </div>

        <c:if test="${not empty error}">
            <p class="image-error-message">${error}</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/upload" method="post" enctype="multipart/form-data">
            <div class="image-form-group">
                <label for="title">Название:</label>
                <input type="text" id="title" name="title" class="image-form-control"
                       required maxlength="100" placeholder="Введите название">
            </div>

            <div class="image-form-group">
                <label for="description">Описание:</label>
                <textarea id="description" name="description" class="image-form-control image-form-textarea"
                          maxlength="500" placeholder="Добавьте описание"></textarea>
            </div>

            <div class="image-form-group">
                <label for="file">Изображение:</label>
                <input type="file" id="file" name="file" class="image-form-control"
                       accept="image/*" required>
            </div>

            <div>
                <button type="submit" class="image-btn image-btn-primary">Загрузить</button>
                <a href="${pageContext.request.contextPath}/profile" class="image-btn image-btn-secondary">Отмена</a>
            </div>
        </form>
    </div>
</body>
</html>
