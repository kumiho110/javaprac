<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>
<html>
<head>
  <title>Корзина</title>
</head>
<body>

<h2>Корзина</h2>

<c:if test="${not empty success}">
  <p style="color: green;"><b>${success}</b></p>
</c:if>

<c:if test="${not empty error}">
  <p style="color: red;"><b>${error}</b></p>
</c:if>

<c:choose>
  <c:when test="${empty cart.items}">
    <p>Корзина пуста.</p>
    <p><a href="/products">Перейти в каталог</a></p>
  </c:when>
  <c:otherwise>
    <table border="1" cellpadding="6" cellspacing="0">
      <tr>
        <th>Товар</th>
        <th>Цена</th>
        <th>В корзине</th>
        <th>Доступно добавить</th>
        <th>Сумма</th>
        <th>Действия</th>
      </tr>

      <c:forEach var="it" items="${cart.items}">
        <tr>
          <td>
            <a href="/products/${it.product.id}">${it.product.name}</a>
          </td>
          <td>${it.unitPrice}</td>
          <td>${it.qty}</td>
          <td>${it.product.stockQty}</td>
          <td>${it.lineTotal}</td>
          <td>
            <form method="post" action="/cart/item/${it.id}/qty" style="display:inline;">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <input
                      type="number"
                      name="qty"
                      value="${it.qty}"
                      min="0"
                      max="${it.qty + it.product.stockQty}"
                      style="width: 80px;"
                      required
              />
              <button type="submit">Обновить</button>
            </form>

            <form method="post" action="/cart/item/${it.id}/remove" style="display:inline; margin-left: 8px;">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <button type="submit">Удалить</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>

    <p style="margin-top: 12px;"><b>Итого:</b> ${cart.totalAmount}</p>

    <h3>Оформление</h3>
    <form method="post" action="/cart/checkout">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

      <div>
        <label>Адрес доставки:</label>
        <input type="text" name="deliveryAddress" value="${user.address}" required style="width: 420px;"/>
      </div>

      <div style="margin-top: 8px;">
        <label>Окно доставки:</label>
        <input type="text" name="deliveryTimeWindow" placeholder="например 18:00-21:00" style="width: 220px;"/>
      </div>

      <div style="margin-top: 12px;">
        <button type="submit">Оформить заказ</button>
      </div>
    </form>
  </c:otherwise>
</c:choose>

</body>
</html>