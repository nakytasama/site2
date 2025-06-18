<%--
  Created by IntelliJ IDEA.
  User: nakyt
  Date: 17.06.2025
  Time: 18:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html>
<head><title>Регистрация</title></head>
<body>
<div style="margin-bottom: 20px;">
    <a href="home">На главную</a>
</div>
<h2>Регистрация</h2>
<form method="post" action="register">
    Логин: <input type="text" name="username" required><br>
    Пароль: <input type="password" name="password" required><br>
    <input type="submit" value="Зарегистрироваться">
</form>
</body>
</html>
