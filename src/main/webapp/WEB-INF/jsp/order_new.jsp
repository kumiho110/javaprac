<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>

<head><title>Оформление заказа</title></head>
<body>

<h2>Оформление заказа</h2>

<form method="post" action="/orders/new">
    <div>
        <label>Клиент:</label>
        <select name="customerId" required>
            <c:forEach var="cst" items="${customers}">
                <option value="${cst.id}">${cst.fullName} (${cst.email})</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label>Товар:</label>
        <select id="productSelect" name="productId" required>
            <c:forEach var="p" items="${products}">
                <option value="${p.id}"
                        data-stock="${p.stockQty}"
                        <c:if test="${p.stockQty == 0}">disabled</c:if>
                        <c:if test="${p.id == qProductId}">selected</c:if>>
                        ${p.name} — ${p.price} (остаток: ${p.stockQty})
                </option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label>Количество:</label>
        <input id="qtyInput" type="number" name="qty" min="1" value="${empty qQty ? 1 : qQty}" required/>
        <span id="stockHint" style="margin-left: 8px;"></span>
    </div>

    <div>
        <label>Адрес доставки:</label>
        <input type="text" name="deliveryAddress" required style="width: 400px;"/>
    </div>

    <div>
        <label>Окно доставки (опц.):</label>
        <input type="text" name="deliveryTimeWindow" placeholder="например 18:00-21:00"/>
    </div>

    <div style="margin-top: 10px;">
        <button type="submit">Оформить</button>
    </div>
</form>

<script>
    const productSelect = document.getElementById("productSelect");
    const qtyInput = document.getElementById("qtyInput");
    const stockHint = document.getElementById("stockHint");
    const submitBtn = document.querySelector('button[type="submit"]');

    function updateQtyConstraints() {
        const selected = productSelect.options[productSelect.selectedIndex];
        const stock = Number(selected?.dataset?.stock ?? 0);

        qtyInput.max = String(stock);

        if (stock <= 0) {
            qtyInput.value = 0;
            qtyInput.min = "0";
            qtyInput.disabled = true;
            if (submitBtn) submitBtn.disabled = true;
            stockHint.textContent = "Нет в наличии";
            return;
        }

        qtyInput.disabled = false;
        qtyInput.min = "1";
        if (submitBtn) submitBtn.disabled = false;

        const currentQty = Number(qtyInput.value || 1);
        if (!Number.isFinite(currentQty) || currentQty < 1) {
            qtyInput.value = 1;
        } else if (currentQty > stock) {
            qtyInput.value = stock;
        }

        stockHint.textContent = "Доступно: " + stock;
    }

    productSelect.addEventListener("change", updateQtyConstraints);
    qtyInput.addEventListener("input", () => {
        const selected = productSelect.options[productSelect.selectedIndex];
        const stock = Number(selected?.dataset?.stock ?? 0);
        let q = Number(qtyInput.value || 0);

        if (q < 1 && stock > 0) qtyInput.value = 1;
        if (q > stock) qtyInput.value = stock;
    });

    updateQtyConstraints();
</script>

</body>
</html>