<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<c:url var="registerUrl" value="/register">
    <c:if test="${not empty param['continue']}">
        <c:param name="continue" value="${param['continue']}"/>
    </c:if>
</c:url>



<!doctype html>
<html>
<head>
    <title>Вход</title>
</head>
<body>

<c:if test="${param.disabled == '1'}">
  <p style="color:red;"><b>Аккаунт отключён администратором.</b></p>
</c:if>

<h2>Вход</h2>

<c:if test="${param.error != null}">
    <p style="color:red;"><b>Неверный email или пароль</b></p>
</c:if>

<c:if test="${param.logout != null}">
    <p style="color:green;"><b>Вы вышли из аккаунта</b></p>
</c:if>

<form method="post" action="/login">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <c:if test="${not empty param['continue']}">
        <input type="hidden" name="continue" value="${param['continue']}"/>
    </c:if>

    <div style="margin-bottom: 8px;">
        <label>Email</label><br/>
        <input type="email" name="email" required style="width: 320px;"/>
    </div>

    <div style="margin-bottom: 8px;">
        <label>Пароль</label><br/>
        <input type="password" name="password" required style="width: 320px;"/>
    </div>

    <button type="submit">Войти</button>
</form>

<p style="margin-top: 12px;">
    Нет аккаунта? <a href="${registerUrl}">Зарегистрироваться</a>
</p>

</body>
</html>