<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Справочники</title>
</head>
<body>

<h2>Справочники</h2>

<c:if test="${not empty successMessage}">
    <div style="color: green; margin-bottom: 10px;">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<ul>
    <li><a href="/staff/catalog/types">Типы товаров</a></li>
    <li><a href="/staff/catalog/manufacturers">Производители</a></li>
</ul>


</body>
</html>
