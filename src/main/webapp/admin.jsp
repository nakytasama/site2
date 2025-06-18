<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Админ-панель</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <style>
        html, body {
            margin: 0;
            padding: 0;
            height: 100%;
            width: 100%;
            overflow: hidden; /* отключение скролбара */
        }

        .scroll-container {
            height: calc(100vh - 60px);
            overflow-y: auto;
            overflow-x: hidden;
            padding: 20px;
            box-sizing: border-box;
        }

        .scroll-container::-webkit-scrollbar {
            display: none;
            width: 0;
            background: transparent;
        }
        .scroll-container {
            -ms-overflow-style: none;
            scrollbar-width: none;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
        }
        .section {
            background: #fff;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1, h2 {
            color: #333;
            margin-top: 0;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f9f9f9;
        }
        input[type="text"], textarea, select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            height: 80px;
            resize: vertical;
        }
        .preview-img {
            max-width: 80px;
            max-height: 60px;
            display: block;
            border-radius: 4px;
            object-fit: cover;
        }
        .actions {
            white-space: nowrap;
        }
        .actions button {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            margin-right: 5px;
        }
        .btn-update {
            background-color: #4CAF50;
            color: white;
        }
        .btn-delete {
            background-color: #f44336;
            color: white;
        }
        .btn-update:hover {
            background-color: #45a049;
        }
        .btn-delete:hover {
            background-color: #d32f2f;
        }
        .error-message {
            color: #d32f2f;
            padding: 10px;
            background: #ffebee;
            border-radius: 4px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<%@ include file="includes/header.jsp" %>

<div class="scroll-container">
    <c:if test="${not empty error}">
        <div class="error-message">${error}</div>
    </c:if>

    <div class="section">
        <h2>Пользователи</h2>
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Логин</th>
                <th>Роль</th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${users}" var="user">
                <tr>
                    <td>${user.id}</td>
                    <td>
                        <input type="text" name="username"
                               value="${user.username}"
                               form="userForm${user.id}">
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${user.id == currentUserId}">
                                <select name="role" form="userForm${user.id}" disabled>
                                    <option value="admin" selected>admin</option>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <select name="role" form="userForm${user.id}">
                                    <option value="user" ${user.role == 'user' ? 'selected' : ''}>user</option>
                                    <option value="admin" ${user.role == 'admin' ? 'selected' : ''}>admin</option>
                                </select>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="actions">
                        <form id="userForm${user.id}" action="admin" method="post">
                            <input type="hidden" name="action" value="updateUser">
                            <input type="hidden" name="id" value="${user.id}">
                            <button type="submit" class="btn-update">Обновить</button>
                        </form>
                        <form action="admin" method="post" style="display: inline;">
                            <input type="hidden" name="action" value="deleteUser">
                            <input type="hidden" name="id" value="${user.id}">
                            <button type="submit" class="btn-delete"
                                ${user.id == currentUserId ? 'disabled' : ''}>Удалить</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="section">
        <h2>Все изображения</h2>
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Название</th>
                <th>Описание</th>
                <th>Автор</th>
                <th>Превью</th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${images}" var="image">
                <c:set var="author" value="${userMap[image.userId]}"/>
                <tr>
                    <td>${image.id}</td>
                    <td>
                        <input type="text" name="title"
                               value="${image.title}"
                               form="imageForm${image.id}">
                    </td>
                    <td>
                                    <textarea name="description"
                                              form="imageForm${image.id}">${image.description}</textarea>
                    </td>
                    <td>
                        <c:if test="${not empty author}">
                            ${author.username}
                        </c:if>
                    </td>
                    <td>
                        <img src="${pageContext.request.contextPath}/uploads/${image.imagePath}"
                             class="preview-img" alt="Превью">
                    </td>
                    <td class="actions">
                        <form id="imageForm${image.id}" action="admin" method="post">
                            <input type="hidden" name="action" value="updateImage">
                            <input type="hidden" name="id" value="${image.id}">
                            <button type="submit" class="btn-update">Обновить</button>
                        </form>
                        <form action="admin" method="post" style="display: inline;">
                            <input type="hidden" name="action" value="deleteImage">
                            <input type="hidden" name="id" value="${image.id}">
                            <button type="submit" class="btn-delete">Удалить</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>