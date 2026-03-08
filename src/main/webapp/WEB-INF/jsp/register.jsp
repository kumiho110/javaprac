<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<c:url var="loginUrl" value="/login">
    <c:if test="${not empty param['continue']}">
        <c:param name="continue" value="${param['continue']}"/>
    </c:if>
</c:url>

<!doctype html>
<html>
<head>
    <title>Регистрация</title>
</head>
<body>


<h2>Регистрация</h2>

<form method="post" action="/register">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <c:if test="${not empty param['continue']}">
        <input type="hidden" name="continue" value="${param['continue']}"/>
    </c:if>

    <div>
        <label>Email</label>
        <input type="email" name="email" value="${param.email}" required/>
    </div>

    <div>
        <label>Full name</label>
        <input type="text" name="fullName" value="${param.fullName}" required/>
    </div>

    <div>
        <label>Password</label>
        <input type="password" name="password" required minlength="6"/>
    </div>

    <div>
        <label>Repeat password</label>
        <input type="password" name="password2" required minlength="6"/>
    </div>

    <button type="submit">Create account</button>
</form>

<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>

<p style="margin-top: 12px;">
    Уже есть аккаунт? <a href="${loginUrl}">Войти</a>
</p>

</body>
</html>