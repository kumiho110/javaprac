<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Тип товара</title>
</head>
<body>

<h2>
    <c:choose>
        <c:when test="${formMode == 'edit'}">Редактирование типа товара</c:when>
        <c:otherwise>Создание типа товара</c:otherwise>
    </c:choose>
</h2>

<div style="margin-bottom: 10px;">
    <a href="/staff/catalog/types">Назад к списку</a>
</div>

<c:if test="${not empty errorMessage}">
    <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<c:choose>
    <c:when test="${formMode == 'edit'}">
        <form method="post" action="/staff/catalog/types/${type.id}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <p>
                <label>Название:</label>
                <input type="text" name="name" value="${type.name}" maxlength="100" style="width: 320px;" required/>
            </p>

            <button type="submit">Сохранить</button>
            <a href="/staff/catalog/types/${type.id}/attributes" style="margin-left: 10px;">Характеристики этого типа</a>
        </form>
    </c:when>
    <c:otherwise>
        <form method="post" action="/staff/catalog/types">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <p>
                <label>Название:</label>
                <input type="text" name="name" value="" maxlength="100" style="width: 320px;" required/>
            </p>

            <button type="submit">Создать</button>
        </form>
    </c:otherwise>
</c:choose>

</body>
</html>
