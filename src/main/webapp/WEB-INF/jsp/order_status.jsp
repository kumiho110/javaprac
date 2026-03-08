<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head><title>Статус заказа</title></head>
<body>

<h2>Заказ #${order.id}</h2>

<p><b>Статус:</b> ${order.status}</p>
<p><b>Создан:</b> ${order.createdAt}</p>
<p><b>Доставка:</b> ${order.deliveryAddress} (${order.deliveryTimeWindow})</p>
<p><b>Сумма:</b> ${order.totalAmount}</p>

<h3>Позиции</h3>

<c:if test="${empty order.items}">
    <p>В заказе нет позиций.</p>
</c:if>

<c:if test="${not empty order.items}">
    <table border="1" cellpadding="6" cellspacing="0">
        <tr>
            <th>Товар</th>
            <th>Тип</th>
            <th>Производитель</th>
            <th>Цена за шт.</th>
            <th>Кол-во</th>
            <th>Сумма</th>
        </tr>

        <c:forEach var="it" items="${order.items}">
            <tr>
                <td><a href="/products/${it.product.id}">${it.product.name}</a></td>
                <td>${it.product.type.name}</td>
                <td>${it.product.manufacturer.name}</td>
                <td>${it.unitPrice}</td>
                <td>${it.qty}</td>
                <td>${it.lineTotal}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<p><a href="/my/orders">К списку заказов</a> | <a href="/products">Каталог</a></p>
<c:if test="${order.status != 'delivered' && order.status != 'cancelled'}">
    <form method="post" action="/my/orders/${order.id}/cancel" style="margin-top: 12px;">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button type="submit" onclick="return confirm('Отменить заказ? Товар будет возвращён на склад.')">
            Отменить заказ
        </button>
    </form></c:if>
<c:if test="${order.status == 'cancelled'}">
    <p><b>Заказ отменён.</b></p>
</c:if>
</body>
</html>