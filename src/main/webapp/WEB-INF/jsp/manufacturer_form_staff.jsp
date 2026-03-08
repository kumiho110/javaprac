<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Производитель</title>
</head>
<body>

<h2>
    <c:choose>
        <c:when test="${formMode == 'edit'}">Редактирование производителя</c:when>
        <c:otherwise>Создание производителя</c:otherwise>
    </c:choose>
</h2>

<div style="margin-bottom: 10px;">
    <a href="/staff/catalog/manufacturers">Назад к списку</a>
</div>

<c:if test="${not empty errorMessage}">
    <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<c:choose>
    <c:when test="${formMode == 'edit'}">
        <form method="post" action="/staff/catalog/manufacturers/${manufacturer.id}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <p>
                <label>Название:</label>
                <input type="text" name="name" value="${manufacturer.name}" maxlength="150" style="width: 360px;" required/>
            </p>

            <p>
                <label>Страна сборки:</label>
                <input type="text" name="assemblyCountry" value="${manufacturer.assemblyCountry}" maxlength="100" style="width: 240px;" required/>
            </p>

            <button type="submit">Сохранить</button>
        </form>
    </c:when>
    <c:otherwise>
        <form method="post" action="/staff/catalog/manufacturers">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <p>
                <label>Название:</label>
                <input type="text" name="name" value="" maxlength="150" style="width: 360px;" required/>
            </p>

            <p>
                <label>Страна сборки:</label>
                <input type="text" name="assemblyCountry" value="" maxlength="100" style="width: 240px;" required/>
            </p>

            <button type="submit">Создать</button>
        </form>
    </c:otherwise>
</c:choose>

</body>
</html>
