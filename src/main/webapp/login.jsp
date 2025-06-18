<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Вход в систему</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/auth-forms.css">
</head>
<body class="auth-body">
    <div class="auth-form-container">
        <a href="${pageContext.request.contextPath}/home" class="auth-back-link">← На главную</a>

        <div class="auth-form-header">
            <h2>Вход в систему</h2>
            <p>Введите свои учетные данные</p>
        </div>

        <c:if test="${not empty error}">
            <div class="auth-error-message">${error}</div>
        </c:if>

        <form method="post" action="login">
            <div class="auth-form-group">
                <label for="username">Логин:</label>
                <input type="text" id="username" name="username" class="auth-form-control"
                       required placeholder="Введите ваш логин">
            </div>

            <div class="auth-form-group">
                <label for="password">Пароль:</label>
                <input type="password" id="password" name="password" class="auth-form-control"
                       required placeholder="Введите ваш пароль">
            </div>

            <button type="submit" class="auth-btn auth-btn-primary">Войти</button>

            <div class="auth-form-footer">
                Еще нет аккаунта? <a href="${pageContext.request.contextPath}/register.jsp">Зарегистрируйтесь</a>
            </div>
        </form>
    </div>
</body>
</html>