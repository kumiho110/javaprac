<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
  <title>
    <c:choose>
      <c:when test="${formMode eq 'edit'}">Редактирование товара</c:when>
      <c:otherwise>Новый товар</c:otherwise>
    </c:choose>
  </title>
</head>
<body>

<h2>
  <c:choose>
    <c:when test="${formMode eq 'edit'}">Редактирование товара</c:when>
    <c:otherwise>Новый товар</c:otherwise>
  </c:choose>
</h2>

<c:if test="${not empty errorMessage}">
  <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<c:choose>
  <c:when test="${formMode eq 'edit'}">
    <form method="post" action="/staff/products/${product.id}">
  </c:when>
  <c:otherwise>
    <form method="post" action="/staff/products">
  </c:otherwise>
</c:choose>

  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div style="margin-bottom: 8px;">
    <label>Название:</label><br/>
    <input type="text" name="name" required style="width: 450px;" value="${product.name}"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Тип товара:</label><br/>
    <select id="typeId" name="typeId" required>
      <option value="">-- выбрать тип --</option>
      <c:forEach var="t" items="${types}">
        <option value="${t.id}" <c:if test="${t.id == qTypeId}">selected</c:if>>
          ${t.name}
        </option>
      </c:forEach>
    </select>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Производитель:</label><br/>
    <select name="manufacturerId" required>
      <option value="">-- выбрать производителя --</option>
      <c:forEach var="m" items="${manufacturers}">
        <option value="${m.id}" <c:if test="${m.id == qManufacturerId}">selected</c:if>>
          ${m.name} (${m.assemblyCountry})
        </option>
      </c:forEach>
    </select>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Описание:</label><br/>
    <input type="text" name="description" style="width: 600px;" value="${product.description}"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Цена:</label><br/>
    <input type="number" name="price" min="0" step="0.01" required value="${product.price}"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Остаток:</label><br/>
    <input type="number" name="stockQty" min="0" step="1" required value="${product.stockQty}"/>
  </div>

  <hr/>
  <h3>Характеристики товара</h3>

  <p style="color: #555;">
    Набор характеристик определяется выбранным типом товара.
  </p>

  <div id="attrBlock">
    <c:choose>
      <c:when test="${empty attributes}">
        <p id="attrPlaceholder" style="color:#666;">Сначала выберите тип товара</p>
      </c:when>
      <c:otherwise>
        <table id="attrTable" border="1" cellpadding="6" cellspacing="0">
          <tr>
            <th>ID</th>
            <th>Характеристика</th>
            <th>Значение</th>
            <th>Обязательная</th>
          </tr>

          <c:forEach var="a" items="${attributes}">
            <tr>
              <td>
                ${a.attributeId}
                <input type="hidden" name="attributeId" value="${a.attributeId}"/>
              </td>
              <td>${a.attributeName}</td>
              <td>
                <input type="text"
                       name="attributeValue"
                       value="${a.value}"
                       <c:if test="${a.required}">required</c:if>
                       style="width: 320px;"/>
              </td>
              <td>
                <c:choose>
                  <c:when test="${a.required}">Да</c:when>
                  <c:otherwise>Нет</c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
        </table>
      </c:otherwise>
    </c:choose>
  </div>

  <div style="margin-top: 12px;">
    <button type="submit">
      <c:choose>
        <c:when test="${formMode eq 'edit'}">Сохранить</c:when>
        <c:otherwise>Создать</c:otherwise>
      </c:choose>
    </button>
    <a href="/staff/products">Назад</a>
  </div>
</form>

<script>
  const typeSelect = document.getElementById('typeId');
  const attrBlock = document.getElementById('attrBlock');

  function escapeHtml(value) {
    return String(value == null ? '' : value)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function renderPlaceholder(text) {
    attrBlock.innerHTML = '<p id="attrPlaceholder" style="color:#666;">' + escapeHtml(text) + '</p>';
  }

  function renderAttributes(attributes) {
    if (!attributes || attributes.length === 0) {
      renderPlaceholder('Для выбранного типа характеристики не заданы');
      return;
    }

    let html = '';
    html += '<table id="attrTable" border="1" cellpadding="6" cellspacing="0">';
    html += '<tr>';
    html += '<th>ID</th>';
    html += '<th>Характеристика</th>';
    html += '<th>Значение</th>';
    html += '<th>Обязательная</th>';
    html += '</tr>';

    for (const a of attributes) {
      const value = a.value == null ? '' : a.value;

      html += '<tr>';

      html += '<td>';
      html += escapeHtml(a.attributeId);
      html += '<input type="hidden" name="attributeId" value="' + escapeHtml(a.attributeId) + '"/>';
      html += '</td>';

      html += '<td>' + escapeHtml(a.attributeName) + '</td>';

      html += '<td>';
      html += '<input type="text" name="attributeValue" value="' + escapeHtml(value) + '"';
      if (a.required) {
        html += ' required';
      }
      html += ' style="width: 320px;"/>';
      html += '</td>';

      html += '<td>' + (a.required ? 'Да' : 'Нет') + '</td>';

      html += '</tr>';
    }

    html += '</table>';
    attrBlock.innerHTML = html;
  }

  async function loadAttributesByType(typeId) {
    if (!typeId) {
      renderPlaceholder('Сначала выберите тип товара');
      return;
    }

    try {
      const response = await fetch('/staff/products/type-attributes?typeId=' + encodeURIComponent(typeId), {
        headers: {
          'X-Requested-With': 'XMLHttpRequest'
        }
      });

      if (!response.ok) {
        renderPlaceholder('Не удалось загрузить характеристики');
        return;
      }

      const data = await response.json();
      renderAttributes(data);
    } catch (e) {
      renderPlaceholder('Ошибка загрузки характеристик');
    }
  }

  typeSelect.addEventListener('change', function () {
    loadAttributesByType(this.value);
  });
</script>

</body>
</html>