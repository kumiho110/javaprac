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
    <title>Каталог товаров</title>
</head>

<body>

<c:if test="${not empty success}">
    <p style="color: green;"><b>${success}</b></p>
</c:if>

<c:if test="${not empty error}">
    <p style="color: red;"><b>${error}</b></p>
</c:if>

<h2>Каталог товаров</h2>

<form method="get" action="/products">
    <div>
        <label>Вид:</label>
        <select id="typeSelect" name="type">
            <option value="">-- любой --</option>
            <c:forEach var="t" items="${types}">
                <option value="${t.name}" <c:if test="${t.name == qType}">selected</c:if>>${t.name}</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label>Производитель:</label>
        <select id="manufacturerSelect" name="manufacturer">
            <option value="">-- любой --</option>
            <c:forEach var="m" items="${manufacturers}">
                <option value="${m.name}" <c:if test="${m.name == qManufacturer}">selected</c:if>>${m.name}</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label>Характеристика:</label>
        <select id="attributeName" name="attributeName">
            <option value="">-- выбери характеристику --</option>
        </select>

        <label>Значение:</label>
        <select id="attributeValue" name="attributeValue">
            <option value="">-- выбери значение --</option>
        </select>
    </div>

    <div style="margin-top:10px;">
        <button type="submit">Фильтровать</button>
        <a href="/products">Сбросить</a>
    </div>
</form>

<hr/>

<c:choose>
    <c:when test="${canUseCart}">
    </c:when>

    <c:otherwise>
        <c:choose>
            <c:when test="${not empty pageContext.request.userPrincipal}">
            </c:when>

            <c:otherwise>
                <div style="margin: 10px 0; padding: 10px; border: 1px solid #ccc;">
                    Чтобы добавлять товары в корзину и оформлять заказы,
                    <a href="${loginUrl}">войдите</a> или <a href="${registerUrl}">зарегистрируйтесь</a>
                </div>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Цена</th>
        <th>Свободно</th>

        <c:if test="${canUseCart}">
            <th>У вас в корзине</th>
            <th>Добавить</th>
        </c:if>

        <th>Действия</th>
    </tr>

    <c:forEach var="p" items="${products}">
        <tr>
            <td>${p.id}</td>
            <td>${p.name}</td>
            <td>${p.price}</td>
            <td>
                <c:choose>
                    <c:when test="${p.stockQty == 0}">Нет в наличии</c:when>
                    <c:otherwise>${p.stockQty}</c:otherwise>
                </c:choose>
            </td>

            <c:if test="${canUseCart}">
                <td>
                    <c:choose>
                        <c:when test="${empty cartQtyByProductId[p.id.toString()]}">0</c:when>
                        <c:otherwise>${cartQtyByProductId[p.id.toString()]}</c:otherwise>
                    </c:choose>
                </td>

                <td>
                    <c:choose>
                        <c:when test="${p.stockQty == 0}">
                            Нет в наличии
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="/cart/add" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <input type="hidden" name="productId" value="${p.id}"/>

                                <input
                                        type="number"
                                        name="qty"
                                        value="1"
                                        min="1"
                                        max="${p.stockQty}"
                                        style="width: 70px;"
                                />

                                <button type="submit">В корзину</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </td>
            </c:if>

            <td><a href="/products/${p.id}">Открыть</a></td>
        </tr>
    </c:forEach>
</table>

<script>
    const typeSelect = document.getElementById('typeSelect');
    const attributeNameSelect = document.getElementById('attributeName');
    const attributeValueSelect = document.getElementById('attributeValue');

    const initialAttributeName = "<c:out value='${qAttributeName}'/>";
    const initialAttributeValue = "<c:out value='${qAttributeValue}'/>";

    function setOptions(selectEl, items, placeholder) {
        selectEl.innerHTML = "";
        const opt0 = document.createElement("option");
        opt0.value = "";
        opt0.textContent = placeholder;
        selectEl.appendChild(opt0);

        for (const v of items) {
            const opt = document.createElement("option");
            opt.value = v;
            opt.textContent = v;
            selectEl.appendChild(opt);
        }
    }

    async function loadAttributeNames(typeName) {
        if (!typeName) {
            setOptions(attributeNameSelect, [], "-- выбери характеристику --");
            setOptions(attributeValueSelect, [], "-- выбери значение --");
            return;
        }

        const url = "/products/attribute-names?type=" + encodeURIComponent(typeName);
        const r = await fetch(url);
        const names = await r.json();

        setOptions(attributeNameSelect, names, "-- выбери характеристику --");

        if (initialAttributeName) {
            attributeNameSelect.value = initialAttributeName;
            await loadAttributeValues(typeName, initialAttributeName);
        } else {
            setOptions(attributeValueSelect, [], "-- выбери значение --");
        }
    }

    async function loadAttributeValues(typeName, attributeName) {
        if (!typeName || !attributeName) {
            setOptions(attributeValueSelect, [], "-- выбери значение --");
            return;
        }

        const url = "/products/attribute-values?type=" + encodeURIComponent(typeName) +
            "&attributeName=" + encodeURIComponent(attributeName);
        const r = await fetch(url);
        const vals = await r.json();

        setOptions(attributeValueSelect, vals, "-- выбери значение --");

        if (initialAttributeValue) {
            attributeValueSelect.value = initialAttributeValue;
        }
    }

    typeSelect.addEventListener("change", async () => {
        attributeNameSelect.value = "";
        attributeValueSelect.value = "";
        await loadAttributeNames(typeSelect.value);
    });

    attributeNameSelect.addEventListener("change", async () => {
        attributeValueSelect.value = "";
        await loadAttributeValues(typeSelect.value, attributeNameSelect.value);
    });

    loadAttributeNames(typeSelect.value);
</script>
</body>
</html>