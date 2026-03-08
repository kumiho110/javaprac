<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
    <title>Управление товарами</title>
</head>
<body>

<h2>${pageTitle}</h2>

<c:if test="${not empty successMessage}">
    <div style="color: green; margin-bottom: 10px;">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<div style="margin-bottom: 10px;">
    <a href="/staff/products/new">Добавить товар</a>
</div>

<form method="get" action="/staff/products">
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

    <div style="margin-top:10px; margin-bottom: 14px;">
        <button type="submit">Фильтровать</button>
        <a href="/staff/products">Сбросить</a>
    </div>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Вид</th>
        <th>Производитель</th>
        <th>Цена</th>
        <th>Остаток</th>
        <th>Действия</th>
    </tr>

    <c:forEach var="p" items="${products}">
        <tr>
            <td>${p.id}</td>
            <td>${p.name}</td>
            <td>${p.type.name}</td>
            <td>${p.manufacturer.name}</td>
            <td>${p.price}</td>
            <td>${p.stockQty}</td>
            <td>
                <a href="/products/${p.id}">Открыть</a> |
                <a href="/staff/products/${p.id}/edit">Редактировать</a>

                <form method="post" action="/staff/products/${p.id}/delete" style="display:inline; margin-left: 6px;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" onclick="return confirm('Удалить товар?')">Удалить</button>
                </form>
            </td>
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