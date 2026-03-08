<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Мои заказы</title>
</head>
<body>

<h2>Мои заказы</h2>

<c:if test="${not empty success}">
    <p style="color: green;"><b>${success}</b></p>
</c:if>

<c:if test="${not empty error}">
    <p style="color: red;"><b>${error}</b></p>
</c:if>

<c:choose>
    <c:when test="${empty orders}">
        <p>У вас пока нет оформленных заказов.</p>
        <p><a href="/products">Перейти в каталог</a></p>
    </c:when>
    <c:otherwise>
        <table border="1" cellpadding="6" cellspacing="0">
            <tr>
                <th>Номер</th>
                <th>Дата</th>
                <th>Статус</th>
                <th>Адрес</th>
                <th>Сумма</th>
                <th>Действия</th>
            </tr>

            <c:forEach var="o" items="${orders}">
                <tr>
                    <td>${o.id}</td>
                    <td>${o.createdAt}</td>
                    <td>
                        ${o.status}
                    </td>
                    <td>${o.deliveryAddress}</td>
                    <td>${o.totalAmount}</td>
                    <td>
                        <a href="/my/orders/${o.id}">Открыть</a>

                        <c:if test="${o.status != 'delivered' && o.status != 'cancelled'}">
                            <form method="post" action="/my/orders/${o.id}/cancel" style="display:inline; margin-left: 8px;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" onclick="return confirm('Отменить заказ? Товар будет возвращён на склад.')">
                                    Отменить
                                </button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>