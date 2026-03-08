<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>${pageTitle}</title>
</head>
<body>

<h2>${pageTitle}</h2>

<c:if test="${not empty successMessage}">
    <div style="color: green; margin-bottom: 10px;">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<div style="margin-bottom: 10px;">
    <a href="/staff/catalog">Справочники</a> |
    <a href="/staff/catalog/types/new">Добавить тип товара</a>
</div>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Действия</th>
    </tr>

    <c:forEach var="t" items="${types}">
        <tr>
            <td>${t.id}</td>
            <td><c:out value="${t.name}"/></td>
            <td>
                <a href="/staff/catalog/types/${t.id}/attributes">Характеристики</a> |
                <a href="/staff/catalog/types/${t.id}/edit">Редактировать</a>

                <form method="post" action="/staff/catalog/types/${t.id}/delete" style="display:inline; margin-left: 6px;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
<button type="submit" onclick="return confirm('Удалить тип товара? Если к нему привязаны товары, удаление будет запрещено.')">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
