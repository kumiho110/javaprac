<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Заказ #${order.id}</title>
</head>
<body>

<h2>Заказ #${order.id}</h2>

<c:if test="${not empty errorMessage}">
    <div style="color:red; margin-bottom:10px;">${errorMessage}</div>
</c:if>

<div><strong>Дата:</strong> ${order.createdAt}</div>
<div><strong>Статус:</strong> ${order.status}</div>
<div><strong>Сумма:</strong> ${order.totalAmount}</div>

<hr/>

<h3>Клиент</h3>
<div><strong>ФИО:</strong> ${order.user.fullName}</div>
<div><strong>Email:</strong> ${order.user.email}</div>
<div><strong>Телефон:</strong> ${order.user.phone}</div>

<hr/>

<h3>Доставка</h3>
<div><strong>Адрес:</strong> ${order.deliveryAddress}</div>
<div><strong>Окно времени:</strong> ${order.deliveryTimeWindow}</div>

<hr/>

<h3>Позиции заказа</h3>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>ID товара</th>
        <th>Название</th>
        <th>Кол-во</th>
        <th>Цена за шт.</th>
        <th>Сумма</th>
    </tr>

    <c:forEach var="i" items="${order.items}">
        <tr>
            <td>${i.product.id}</td>
            <td>${i.product.name}</td>
            <td>${i.qty}</td>
            <td>${i.unitPrice}</td>
            <td>${i.lineTotal}</td>
        </tr>
    </c:forEach>
</table>

<c:if test="${order.status != 'cancelled' && order.status != 'delivered'}">
<hr/>

<h3>Изменить статус</h3>

<form method="post" action="/staff/orders/${order.id}/status">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <select name="status">
        <c:if test="${order.status != 'processing'}">
            <option value="processing">Processing</option>
        </c:if>
        <c:if test="${order.status != 'packed'}">
            <option value="packed">Packed</option>
        </c:if>
        <c:if test="${order.status != 'delivered'}">
            <option value="delivered">Delivered</option>
        </c:if>
        <c:if test="${order.status != 'cancelled'}">
            <option value="cancelled">Cancelled</option>
        </c:if>
    </select>

    <button type="submit">Сохранить статус</button>
</form>

</c:if>
<hr/>

<c:if test="${order.status == 'cancelled' || order.status == 'delivered'}">
    <form method="post" action="/staff/orders/${order.id}/delete" style="margin-top: 12px;">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button type="submit" onclick="return confirm('Удалить заказ?')">
            Удалить заказ
        </button>
    </form>
</c:if>

</body>
</html>