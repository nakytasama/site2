<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="header">
    <!-- левая часть -->
    <div class="header-left">
        <a href="${pageContext.request.contextPath}/home" class="logo-link">Главная</a>
    </div>

    <!-- правая часть -->
    <div class="header-right">
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/login.jsp" class="nav-link">Вход</a>
                <a href="${pageContext.request.contextPath}/register.jsp" class="nav-link">Регистрация</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/profile" class="nav-link">${sessionScope.user.username}</a>

                <c:if test="${sessionScope.user.role == 'admin'}">
                    <a href="${pageContext.request.contextPath}/admin" class="nav-link">Администрирование</a>
                </c:if>

                <a href="${pageContext.request.contextPath}/logout" class="nav-link">Выход</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>