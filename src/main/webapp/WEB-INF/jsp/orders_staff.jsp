<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Заказы</title>
</head>
<body>

<h2>Заказы</h2>

<c:if test="${not empty successMessage}">
    <div style="color:green; margin-bottom:10px;">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div style="color:red; margin-bottom:10px;">${errorMessage}</div>
</c:if>

<form method="get" action="/staff/orders">
    <label>Статус:</label>
    <select name="status">
        <option value="">-- любой --</option>
        <c:forEach var="st" items="${statuses}">
            <option value="${st}" <c:if test="${st.toString() == qStatus}">selected</c:if>>
                    ${st}
            </option>
        </c:forEach>
    </select>
    <button type="submit">Фильтровать</button>
    <a href="/staff/orders">Сбросить</a>
</form>

<hr/>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>ID</th>
        <th>Дата</th>
        <th>Клиент</th>
        <th>Email</th>
        <th>Статус</th>
        <th>Сумма</th>
        <th>Доставка</th>
        <th>Действия</th>
    </tr>

    <c:forEach var="o" items="${orders}">
        <tr>
            <td>${o.id}</td>
            <td>${o.createdAt}</td>
            <td>${o.user.fullName}</td>
            <td>${o.user.email}</td>
            <td>${o.status}</td>
            <td>${o.totalAmount}</td>
            <td>${o.deliveryAddress}</td>
            <td>
                <a href="/staff/orders/${o.id}">Открыть</a>

                <form method="post" action="/staff/orders/${o.id}/delete" style="display:inline; margin-left: 8px;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" onclick="return confirm('Удалить заказ и вернуть товар на склад?')">
                        Удалить
                    </button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

</body>
</html>