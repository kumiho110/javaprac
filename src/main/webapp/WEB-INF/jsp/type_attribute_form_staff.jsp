<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Характеристика</title>
</head>
<body>

<h2>
    <c:choose>
        <c:when test="${formMode == 'edit'}">Редактирование характеристики</c:when>
        <c:otherwise>Создание характеристики</c:otherwise>
    </c:choose>
</h2>

<p><b>Тип товара:</b> <c:out value="${type.name}"/></p>

<div style="margin-bottom: 10px;">
    <a href="/staff/catalog/types/${type.id}/attributes">Назад к характеристикам</a>
</div>

<c:if test="${not empty errorMessage}">
    <div style="color: red; margin-bottom: 10px;"><c:out value="${errorMessage}"/></div>
</c:if>

<c:choose>
    <c:when test="${formMode == 'edit'}">
        <form method="post" action="/staff/catalog/attributes/${attribute.id}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <p>
                <label>Название:</label>
                <input type="text" name="name" value="<c:out value='${attribute.name}'/>" maxlength="100" style="width: 360px;" required/>
            </p>

            <p>
                <label>
                    <input type="checkbox" name="required" value="true" <c:if test="${attribute.required}">checked</c:if> />
                    Обязательная
                </label>
            </p>

            <button type="submit">Сохранить</button>
        </form>
    </c:when>
    <c:otherwise>
        <form method="post" action="/staff/catalog/types/${type.id}/attributes">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <p>
                <label>Название:</label>
                <input type="text" name="name" value="" maxlength="100" style="width: 360px;" required/>
            </p>

            <p>
                <label>
                    <input type="checkbox" name="required" value="true" />
                    Обязательная
                </label>
            </p>

            <button type="submit">Создать</button>
        </form>
    </c:otherwise>
</c:choose>

</body>
</html>