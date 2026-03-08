<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Характеристики типа товара</title>
</head>
<body>

<h2>Характеристики: <c:out value="${type.name}"/></h2>

<c:if test="${not empty successMessage}">
    <div style="color: green; margin-bottom: 10px;"><c:out value="${successMessage}"/></div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div style="color: red; margin-bottom: 10px;"><c:out value="${errorMessage}"/></div>
</c:if>

<div style="margin-bottom: 10px;">
    <a href="/staff/catalog/types">Назад к типам</a> |
    <a href="/staff/catalog/types/${type.id}/attributes/new">Добавить характеристику</a>
</div>

<c:choose>
    <c:when test="${empty attributes}">
        <p>Характеристик пока нет.</p>
    </c:when>
    <c:otherwise>
        <table border="1" cellpadding="6" cellspacing="0">
            <tr>
                <th>ID</th>
                <th>Позиция</th>
                <th>Название</th>
                <th>Обязательная</th>
                <th>Действия</th>
            </tr>

            <c:forEach var="a" items="${attributes}" varStatus="st">
                <tr>
                    <td><c:out value="${a.id}"/></td>
                    <td><c:out value="${st.index + 1}"/></td>
                    <td><c:out value="${a.name}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${a.required}">Да</c:when>
                            <c:otherwise>Нет</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <form method="post" action="/staff/catalog/attributes/${a.id}/move-up" style="display:inline;">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit">↑</button>
                        </form>

                        <form method="post" action="/staff/catalog/attributes/${a.id}/move-down" style="display:inline; margin-left: 4px;">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit">↓</button>
                        </form>

                        <a href="/staff/catalog/attributes/${a.id}/edit" style="margin-left: 8px;">Редактировать</a>

                        <form method="post" action="/staff/catalog/attributes/${a.id}/delete" style="display:inline; margin-left: 6px;">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" onclick="return confirm('Удалить характеристику? Если она уже используется в товарах, удаление будет запрещено.')">Удалить</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>