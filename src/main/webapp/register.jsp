<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Регистрация</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/auth-forms.css">
</head>
<body class="auth-body">
    <div class="auth-form-container">
        <a href="${pageContext.request.contextPath}/home" class="auth-back-link">← На главную</a>

        <div class="auth-form-header">
            <h2>Создать аккаунт</h2>
            <p>Заполните форму регистрации</p>
        </div>

        <c:if test="${not empty error}">
            <div class="auth-error-message">${error}</div>
        </c:if>

        <form method="post" action="register">
            <div class="auth-form-group">
                <label for="username">Логин:</label>
                <input type="text" id="username" name="username" class="auth-form-control"
                       required placeholder="Придумайте логин">
            </div>

            <div class="auth-form-group">
                <label for="password">Пароль:</label>
                <input type="password" id="password" name="password" class="auth-form-control"
                       required placeholder="Придумайте пароль">
            </div>

            <div class="auth-form-group">
                <label for="confirm-password">Подтвердите пароль:</label>
                <input type="password" id="confirm-password" name="confirm-password" class="auth-form-control"
                       required placeholder="Повторите пароль">
            </div>

            <button type="submit" class="auth-btn auth-btn-primary">Зарегистрироваться</button>

            <div class="auth-form-footer">
                Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login.jsp">Войдите</a>
            </div>
        </form>
    </div>

    <script>
        // тут проверка совпадают ли пароли
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;

            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Пароли не совпадают!');
                document.getElementById('password').value = '';
                document.getElementById('confirm-password').value = '';
                document.getElementById('password').focus();
            }
        });
    </script>
</body>
</html>