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
    <a href="/staff/catalog/manufacturers/new">Добавить производителя</a>
</div>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Страна сборки</th>
        <th>Действия</th>
    </tr>

    <c:forEach var="m" items="${manufacturers}">
        <tr>
            <td>${m.id}</td>
            <td><c:out value="${m.name}"/></td>
            <td><c:out value="${m.assemblyCountry}"/></td>
            <td>
                <a href="/staff/catalog/manufacturers/${m.id}/edit">Редактировать</a>

                <form method="post" action="/staff/catalog/manufacturers/${m.id}/delete" style="display:inline; margin-left: 6px;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
<button type="submit" onclick="return confirm('Удалить производителя? Если к нему привязаны товары, удаление будет запрещено.')">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
