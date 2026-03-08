<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<c:url var="loginUrl" value="/login">
    <c:param name="continue" value="${continueUrl}" />
</c:url>

<c:url var="registerUrl" value="/register">
    <c:param name="continue" value="${continueUrl}" />
</c:url>

<html>
<head>
    <title>Товар</title>
</head>
<body>

<h2>${product.name}</h2>

<p><b>Описание:</b> ${product.description}</p>
<p><b>Цена:</b> ${product.price}</p>
<p><b>Наличие:</b>
    <c:choose>
        <c:when test="${product.stockQty == 0}">Нет в наличии</c:when>
        <c:otherwise>${product.stockQty}</c:otherwise>
    </c:choose>
</p>

<c:choose>
    <c:when test="${canUseCart}">
        <p><b>У вас в корзине:</b>
            <c:choose>
                <c:when test="${empty cartQtyByProductId[product.id.toString()]}">0</c:when>
                <c:otherwise>${cartQtyByProductId[product.id.toString()]}</c:otherwise>
            </c:choose>
        </p>

<c:choose>
    <c:when test="${product.stockQty == 0}">
        <p style="margin-bottom: 16px;"><b>Нет в наличии</b></p>
    </c:when>
    <c:otherwise>
        <form method="post" action="/cart/add" style="margin-bottom: 16px;">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="hidden" name="productId" value="${product.id}"/>

            <label>Количество:</label>
            <input
                    type="number"
                    name="qty"
                    value="1"
                    min="1"
                    max="${product.stockQty}"
                    style="width: 80px;"
            />

            <button type="submit">В корзину</button>
        </form>
    </c:otherwise>
</c:choose>
    </c:when>

<c:otherwise>
    <c:choose>
    <c:when test="${not empty pageContext.request.userPrincipal}">
        </c:when>

        <c:otherwise>
            <div style="margin: 12px 0; padding: 10px; border: 1px solid #ccc;">
                <a href="${loginUrl}">войдите</a> или <a href="${registerUrl}">зарегистрируйтесь</a>,
                чтобы добавить товар в корзину.
            </div>
        </c:otherwise>
    </c:choose>
</c:otherwise>
</c:choose>

<p>
    <b>Вид:</b>
    <c:out value="${product.type.name}"/>
</p>

<p>
    <b>Производитель:</b>
    <c:out value="${product.manufacturer.name}"/> /
    <c:out value="${product.manufacturer.assemblyCountry}"/>
</p>

<h3>Характеристики</h3>
<c:choose>
    <c:when test="${empty attributeRows}">
        <p>Характеристик нет</p>
    </c:when>
    <c:otherwise>
        <table border="1" cellpadding="6" cellspacing="0">
            <tr>
                <th>Характеристика</th>
                <th>Значение</th>
            </tr>
            <c:forEach var="a" items="${attributeRows}">
                <tr>
                    <td><c:out value="${a.name}"/></td>
                    <td><c:out value="${a.value}"/></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>